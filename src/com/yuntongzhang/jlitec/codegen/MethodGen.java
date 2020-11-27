package com.yuntongzhang.jlitec.codegen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yuntongzhang.jlitec.asm.*;
import com.yuntongzhang.jlitec.ast.Operator;
import com.yuntongzhang.jlitec.ir3.*;


/**
 * In charge of generation of a method.
 *
 * -------------- Stack Organization --------------
 * arg4 for methodCallStmt (first arg is arg0)
 * arg5 for methodCallStmt
 *  ......
 * spilled variables
 *  ......
 * callee-saved regs
 * saved LR
 * saved PC
 *
 * Note for stack:
 * 1. If methodCallStmt has more than 4 arguments, they are saved on stack.
 *    the # of stack slots reserved is for the methodCall with most # of arguments.
 * 2. For these args saved on stack:
 *    (1) callee access arg4 with FP+4. These args are loaded to reg at callee prologue.
 *    (2) caller prepares arg4 with SP, prepares arg5 with SP+4.
 */
public class MethodGen {
    private static final int stackEntryWidth = 4;
    private Id3 methodName;
    private List<FmlItem3> argList;
    private List<Stmt3> stmts;
    private Map<Id3, Reg> varToReg;
    List<Id3> spilledVars;
    LabelArm epilogueLabel;
    // contains offsets for spilled vars from FP
    Map<Id3, Integer> spilledOffsetsFromFP = new HashMap<>();
    // DataSection of the program, keep here because method translation can insert new data
    private DataSection dataSection;
    // keep track of max # of args that a method called inside here can have
    // need this info to allocate stack space in prologue
    // set to 4, since less than 4 args dont need to update this state
    private int maxNoOfArgs = 4;
    // maps vars to type (in String)
    private Map<Id3, String> varTypeInfo;
    // passed in from CodeGen, used to look up field offset in objects
    private Map<String, Map<String, Integer>> classMap;
    // passed in from CodeGen, used to keep track of class field types
    private Map<String, Map<String, String>> classTypeMap;

    public MethodGen(CMtd3 cMtd3, DataSection dataSection,
                     Map<String, Map<String, Integer>> classMap, Map<String, Map<String, String>> classTypeMap) {
        this.dataSection = dataSection;
        this.classMap = classMap;
        this.classTypeMap = classTypeMap;
        this.methodName = cMtd3.getMethodName();
        this.argList = cMtd3.getFmlList();
        this.varTypeInfo = extractArgumentTypes(cMtd3.getFmlList());
        List<Stmt3> originalStmts = cMtd3.getMethodBody().getAllStmts();
        // register allocation for a single method
        RegAllocator regAllocator = new RegAllocator(originalStmts);
        regAllocator.runAllocation();
        this.stmts = regAllocator.getUpdatedCodeSequence();
        this.varToReg = regAllocator.getAllocationResult();
        this.spilledVars = regAllocator.getSpilledVars();
    }

    // record down types of this method arguments
    private Map<Id3, String> extractArgumentTypes(List<FmlItem3> fmlList) {
        Map<Id3, String> result = new HashMap<>();
        for (FmlItem3 fmlItem3 : fmlList) {
            result.put(fmlItem3.getId(), fmlItem3.getType().toString());
        }
        return result;
    }

    // entry point to generate a method
    public MethodBlock gen() {
        MethodBlock result = new MethodBlock();
        populateSpilledOffsets();
        // generate method name
        result.appendIns(new LabelArm(this.methodName.toString()));
        // method epilogue, consider first because body needs epilogue label
        List<InsArm> epilogueResult = genEpilogue();
        // method body
        List<InsArm> bodyResult = genBody();
        // method prologue, need complete stack offset info, so consider prologue after body
        List<InsArm> prologueResult = genPrologue();
        // add them into MethodBlock
        result.appendInsList(prologueResult);
        result.appendInsList(bodyResult);
        result.appendInsList(epilogueResult);
        return result;
    }

    private void populateSpilledOffsets() {
        int numOfCalleeSavedRegs = getCalleeSavedRegs().size();
        int currentSlot = numOfCalleeSavedRegs + 1 + 1;
        for (Id3 spilledVar : spilledVars) {
            int offset = - currentSlot * stackEntryWidth;
            spilledOffsetsFromFP.put(spilledVar, offset);
            currentSlot++;
        }
    }

    private Set<Reg> getCalleeSavedRegs() {
        Set<Reg> calleeSavedRegs = new HashSet<>();
        for (Id3 var : varToReg.keySet()) {
            calleeSavedRegs.add(varToReg.get(var));
        }
        return calleeSavedRegs;
    }

    private List<InsArm> genPrologue() {
        List<InsArm> result = new ArrayList<>();
        // push callee-saved regs onto stack
        Set<Reg> calleeSavedRegs = getCalleeSavedRegs();
        List<Reg> regsToPush = new ArrayList<>();
        regsToPush.add(Reg.FP);
        regsToPush.add(Reg.LR);
        regsToPush.addAll(calleeSavedRegs);
        result.add(new StackIns(StackIns.Opcode.PUSH, regsToPush));
        // set FP to correct position
        int fpSpInitDist = (calleeSavedRegs.size() + 1) * stackEntryWidth;
        result.add(new DataIns(DataIns.Opcode.ADD, Reg.FP, Reg.SP, new Operand2.ImmedOp(fpSpInitDist)));
        // set SP to correct position
        int maxNumOfArgsOnStack = maxNoOfArgs - 4;
        int fpSpFinalDist = (calleeSavedRegs.size() + 1 + spilledVars.size() + maxNumOfArgsOnStack) * stackEntryWidth;
        result.add(new DataIns(DataIns.Opcode.SUB, Reg.SP, Reg.FP, new Operand2.ImmedOp(fpSpFinalDist)));
        // if any arguments are live at entry, they are already allocated to some reg in varToReg
        // here we move them from their original place (r0-r3/stack) to their allocate regs
        for (int i = 0; i < argList.size(); i++) {
            Id3 argId = argList.get(i).getId();
            Reg destReg = varToReg.get(argId);
            if (destReg == null) {
                // this argument is not live in method body, so we can skip it
                continue;
            }
            if (i <= 3) { // was in register
                Reg originalReg = Reg.fromInt(i);
                result.add(new MovIns(destReg, new Operand2.RegOp(originalReg)));
            } else { // was on stack
                // if arg was saved on stack, access them from FP
                // arg4 at fp+4, arg5 at fp+8 ...
                int offset = (i - 3) * stackEntryWidth;
                result.add(new MemIns(MemIns.Opcode.LDR, destReg, new Address.RegPreIndexed(Reg.FP, offset)));
            }
        }
        return result;
    }

    private List<InsArm> genEpilogue() {
        List<InsArm> result = new ArrayList<>();
        this.epilogueLabel = LabelArm.genNewEpilogueLabel();
        result.add(epilogueLabel);
        // move sp to correct position to pop spilled vars and arguments for other methods
        int fpSpDist = (getCalleeSavedRegs().size() + 1) * stackEntryWidth;
        result.add(new DataIns(DataIns.Opcode.SUB, Reg.SP, Reg.FP, new Operand2.ImmedOp(fpSpDist)));
        // pop saved regs and return
        List<Reg> regsToPopTo = new ArrayList<>();
        regsToPopTo.add(Reg.FP);
        regsToPopTo.add(Reg.PC);
        regsToPopTo.addAll(getCalleeSavedRegs());
        result.add(new StackIns(StackIns.Opcode.POP, regsToPopTo));
        return result;
    }

    private List<InsArm> genBody() {
        List<InsArm> bodyResult = new ArrayList<>();
        for (Stmt3 stmt : this.stmts) {
            bodyResult.addAll(translateStmt3ToInsArm(stmt));
        }
        return bodyResult;
    }

    private List<InsArm> translateStmt3ToInsArm(Stmt3 stmt) {
        List<InsArm> result = new ArrayList<>();
        if (stmt instanceof VarDecl3) {
            VarDecl3 varDecl3 = (VarDecl3) stmt;
            varTypeInfo.put(varDecl3.getId(), varDecl3.getTypeInString());
        }
        if (stmt instanceof Store3) {
            Store3 storeStmt = (Store3) stmt;
            Id3 var = storeStmt.getVar();
            Reg reg = varToReg.get(var);
            Address.RegPreIndexed addr = new Address.RegPreIndexed(Reg.FP, spilledOffsetsFromFP.get(var));
            result.add(new MemIns(MemIns.Opcode.STR, reg, addr));
        }
        if (stmt instanceof Load3) {
            Load3 loadStmt = (Load3) stmt;
            Id3 var = loadStmt.getVar();
            Reg reg = varToReg.get(var);
            Address.RegPreIndexed addr = new Address.RegPreIndexed(Reg.FP, spilledOffsetsFromFP.get(var));
            result.add(new MemIns(MemIns.Opcode.LDR, reg, addr));
        }
        if (stmt instanceof Return3) {
            // prepare return value
            Return3 returnStmt = (Return3) stmt;
            Id3 returnVal = returnStmt.getReturnVal();
            if (returnVal != null) { // has return value
                Reg returnValReg = varToReg.get(returnVal);
                result.add(new MovIns(Reg.R0, new Operand2.RegOp(returnValReg)));
            }
            // jump to epilogue
            result.add(new BranchIns(BranchIns.Opcode.B, epilogueLabel));
        }
        if (stmt instanceof Println3) {
            Println3 printlnStmt = (Println3) stmt;
            Idc3 arg = printlnStmt.getArg();
            if (arg instanceof IntegerLiteral3 ||
                    (arg instanceof Id3 && varTypeInfo.get(arg).equals("Int"))) {
                // need int format specifier
                result.add(prepareArgToReg(new StringLiteral3("%i\n"), Reg.R0));
            } else {
                result.add(prepareArgToReg(new StringLiteral3("%s\n"), Reg.R0));
            }
            result.add(prepareArgToReg(arg, Reg.R1));
            // now, make the function call to printf
            result.add(new BranchIns(BranchIns.PRINTF));
        }
        if (stmt instanceof MethodCallStmt3) {
            MethodCallStmt3 methodCallStmt = (MethodCallStmt3) stmt;
            Id3 methodName = methodCallStmt.getMethodName();
            List<Idc3> argList = methodCallStmt.getArgList();
            result.addAll(translateMethodCall(methodName, argList));
        }
        if (stmt instanceof Label3) {
            result.add(new LabelArm((Label3) stmt));
        }
        if (stmt instanceof IfGoto3) {
            IfGoto3 ifGotoStmt = (IfGoto3) stmt;
            RelationExp3 condition = ifGotoStmt.getCondition();
            Label3 target = ifGotoStmt.getLabel();
            if (condition instanceof RelationOp3) {
                RelationOp3 relationOp3 = (RelationOp3) condition;
                Boolean reduced = attemptToReduceRelationOp3(relationOp3);
                if (reduced != null) { // the condition is constCMPconst
                    if (reduced) { // this stmt is actually Goto
                        result.add(new BranchIns(BranchIns.Opcode.B, new LabelArm(target)));
                    }
                    // else this stmt is never executed, dont add anything to result
                } else { // condition is varCMPconst or varCMPvar
                    result.add(genCmpInsFromRelationOp(relationOp3));
                    // generate branch with condition
                    Cond cond = Cond.fromOperator(relationOp3.getOperator());
                    result.add(new BranchIns(BranchIns.Opcode.B, cond, new LabelArm(target)));
                }
            } else if (condition instanceof Id3) {
                Id3 varCondition = (Id3) condition;
                // compare the value stored in register with 1, and generate BEQ
                result.add(new CmpIns(varToReg.get(varCondition), new Operand2.ImmedOp(1)));
                result.add(new BranchIns(BranchIns.Opcode.B, Cond.EQ, new LabelArm(target)));
            } else { // BooleanLiteral3
                BooleanLiteral3 booleanLiteral = (BooleanLiteral3) condition;
                if (booleanLiteral.getValue()) { // always true, so stmt is actually Goto
                    result.add(new BranchIns(BranchIns.Opcode.B, new LabelArm(target)));
                }
                // dont need to do anything if condition is false
            }
        }
        if (stmt instanceof Goto3) {
            Label3 target = ((Goto3) stmt).getLabel();
            result.add(new BranchIns(BranchIns.Opcode.B, new LabelArm(target)));
        }
        if (stmt instanceof AssignId3) {
            AssignId3 assignId3 = (AssignId3) stmt;
            Id3 left = assignId3.getLhs();
            Reg lhsReg = varToReg.get(left);
            // simple dead-code-elimination: if lhs is not live at all in this method,
            // dont need to translate
            if (lhsReg != null) {
                result.addAll(translateRhsExp(assignId3.getRhs(), lhsReg, varTypeInfo.get(left)));
            }
        }
        if (stmt instanceof AssignDeclare3) {
            AssignDeclare3 assignDeclare3 = (AssignDeclare3) stmt;
            Id3 lhs = assignDeclare3.getLhs();
            Reg lhsReg = varToReg.get(lhs);
            // simple dead-code-elimination: if lhs is not live at all in this method,
            // dont need to translate
            if (lhsReg != null) {
                // record type info since a variable is declared
                varTypeInfo.put(lhs, assignDeclare3.getType().toString());
                result.addAll(translateRhsExp(assignDeclare3.getRhs(), lhsReg, varTypeInfo.get(lhs)));
            }
        }
        if (stmt instanceof AssignAccess3) {
            AssignAccess3 assignAccess3 = (AssignAccess3) stmt;
            Id3 object = assignAccess3.getLhsLeft();
            Id3 fieldName = assignAccess3.getLhsRight();
            Reg objectReg = varToReg.get(object);
            String cname = varTypeInfo.get(object);
            // get the type of this field
            String fieldType = classTypeMap.get(cname).get(fieldName.toString());
            // mov the result of rhs exp into R3
            result.addAll(translateRhsExp(assignAccess3.getRhs(), Reg.R3, fieldType));
            // store R3 into memory location object+offset
            int offset = classMap.get(cname).get(fieldName.toString());
            result.add(new MemIns(MemIns.Opcode.STR, Reg.R3, new Address.RegPreIndexed(objectReg, offset)));
        }

        return result;
    }

    private List<InsArm> translateMethodCall(Id3 methodName, List<Idc3> argList) {
        List<InsArm> result = new ArrayList<>();
        // prepare arguments
        for (int i = argList.size() - 1; i >= 0; i--) {
            // iterate from behind because we can then use r0-r4 to prepare stack arguments
            Idc3 arg = argList.get(i);
            if (i <= 3) { // arg should be in register
                result.add(prepareArgToReg(arg, Reg.fromInt(i)));
            } else { // arg should be in stack
                // arg4 at sp, arg5 at sp+4, arg6 at sp+8 ......
                int offsetFromSP = (i - 4) * stackEntryWidth;
                result.addAll(prepareArgToStack(arg, offsetFromSP));
            }
        }
        // Need to keep track of the max # of args of all the methods called
        if (argList.size() > maxNoOfArgs) {
            maxNoOfArgs = argList.size();
        }
        // make the method call
        result.add(new BranchIns(methodName.toString()));
        return result;
    }

    // put the result into destReg
    private List<InsArm> translateRelationOp3(RelationOp3 relationOp3, Reg destReg) {
        List<InsArm> result = new ArrayList<>();
        Idc3 left = relationOp3.getLeft();
        Idc3 right = relationOp3.getRight();
        Operator operator = relationOp3.getOperator();
        Boolean evalResult = attemptToReduceRelationOp3(relationOp3);
        if (evalResult != null) { // relationOp3 was constOPconst
            result.addAll(loadConstantIntoReg(new BooleanLiteral3(evalResult), destReg));
        } else { // relationOp3 is varOPconst or constOPvar or varOPvar
            Cond cond = Cond.fromOperator(operator);
            if (right instanceof IntegerLiteral3) { // relationOp3 is varOPconst
                Reg leftReg = varToReg.get((Id3) left);
                int rightVal = ((IntegerLiteral3) right).getValue();
                result.add(new CmpIns(leftReg, new Operand2.ImmedOp(rightVal)));
            } else if (left instanceof IntegerLiteral3) { // relationOp3 is constOPvar
                // to reduce the number of instructions, negate the operator
                Reg rightReg = varToReg.get((Id3) right);
                int leftVal = ((IntegerLiteral3) left).getValue();
                result.add(new CmpIns(rightReg, new Operand2.ImmedOp(leftVal)));
                // negate the operator
                cond = Cond.fromOperator(operator.relationalNegate());
            } else { // relationOp is varOPvar
                Reg leftReg = varToReg.get((Id3) left);
                Reg rightReg = varToReg.get((Id3) right);
                result.add(new CmpIns(leftReg, new Operand2.RegOp(rightReg)));
            }
            // mov 0 first (false)
            result.add(new MovIns(destReg, new Operand2.ImmedOp(0)));
            // mov 1 if operator evals to true
            result.add(new MovIns(cond, destReg, new Operand2.ImmedOp(1)));
        }
        return result;
    }

    // put the result in destReg
    // pre-condition: operator is not relational
    private List<InsArm> translateBinaryOp3(BinaryOp3 binaryOp3, Reg destReg) {
        List<InsArm> result = new ArrayList<>();
        Idc3 left = binaryOp3.getLeft();
        Idc3 right = binaryOp3.getRight();
        Operator operator = binaryOp3.getOperator();

        if (left instanceof BooleanLiteral3 && right instanceof BooleanLiteral3) {
            boolean evalResult = operator.logicalApplyTo(((BooleanLiteral3) left).getValue(),
                    ((BooleanLiteral3) right).getValue());
            result.addAll(loadConstantIntoReg(new BooleanLiteral3(evalResult), destReg));
        } else if (left instanceof IntegerLiteral3 && right instanceof IntegerLiteral3) {
            int evalResult = operator.arithmeticApplyTo(((IntegerLiteral3) left).getValue(),
                    ((IntegerLiteral3) right).getValue());
            result.addAll((loadConstantIntoReg(new IntegerLiteral3(evalResult), destReg)));
        } else if (left instanceof StringLiteral3 && right instanceof StringLiteral3) {
            // perform concatenation at compile time, operator must be +
            String evalResult = ((StringLiteral3) left).getValue() + ((StringLiteral3) right).getValue();
            result.addAll(loadConstantIntoReg(new StringLiteral3(evalResult), destReg));
        } else if (left instanceof BooleanLiteral3) { // right is var, operator is || or &&
            Reg rightReg = varToReg.get(right);
            int leftVal = ((BooleanLiteral3) left).getValue() ? 1 : 0;
            result.add(new DataIns(DataIns.Opcode.fromOperator(operator), destReg,
                    rightReg, new Operand2.ImmedOp(leftVal)));
        } else if (right instanceof BooleanLiteral3) { // left is var, operator is || or &&
            Reg leftReg = varToReg.get(left);
            int rightVal = ((BooleanLiteral3) right).getValue() ? 1 : 0;
            result.add(new DataIns(DataIns.Opcode.fromOperator(operator), destReg,
                    leftReg, new Operand2.ImmedOp(rightVal)));
        } else if (left instanceof IntegerLiteral3 && operator != Operator.TIMES) { // right is var, operator is + or -
            // deal with multiply in the common case, since mul need both arguments in register
            Reg rightReg = varToReg.get(right);
            int leftVal = ((IntegerLiteral3) left).getValue();
            result.add(new DataIns(DataIns.Opcode.fromOperator(operator), destReg,
                    rightReg, new Operand2.ImmedOp(leftVal)));
        } else if (right instanceof IntegerLiteral3 && operator != Operator.TIMES) { // left is var, operator is + or -
            Reg leftReg = varToReg.get(left);
            int rightVal = ((IntegerLiteral3) right).getValue();
            result.add(new DataIns(DataIns.Opcode.fromOperator(operator), destReg,
                    leftReg, new Operand2.ImmedOp(rightVal)));
        } else if (left instanceof StringLiteral3) { // const+var, string concat
            Reg rightReg = varToReg.get(right);
            // get size of right
            result.add(new MovIns(Reg.R0, new Operand2.RegOp(rightReg)));
            result.add(new BranchIns(BranchIns.STRLEN));
            // length of right now in R0
            int leftLength = ((StringLiteral3) left).getLength();
            // need to +1 for the null at the end
            result.add(new DataIns(DataIns.Opcode.ADD, Reg.R0, Reg.R0, new Operand2.ImmedOp(leftLength+1)));
            result.add(new BranchIns(BranchIns.MALLOC));
            // pointer to result buffer stored in R0
            // load left address to R1
            LabelArm leftLabel = this.dataSection.addNewData(((StringLiteral3) left).getValue());
            result.add(new MemIns(MemIns.Opcode.LDR, Reg.R1, new Address.LabelAddr(leftLabel)));
            // add left to result buffer
            result.add(new BranchIns(BranchIns.STRCAT));
            // add right to result buffer
            result.add(new MovIns(Reg.R1, new Operand2.RegOp(rightReg)));
            result.add(new BranchIns(BranchIns.STRCAT));
            // operation result in R0 now
            result.add(new MovIns(destReg, new Operand2.RegOp(Reg.R0)));
        } else if (right instanceof StringLiteral3) { // var+const, string concat
            Reg leftReg = varToReg.get(left);
            // get size of left
            result.add(new MovIns(Reg.R0, new Operand2.RegOp(leftReg)));
            result.add(new BranchIns(BranchIns.STRLEN));
            // length of left now in R0
            int rightLength = ((StringLiteral3) right).getLength();
            result.add(new DataIns(DataIns.Opcode.ADD, Reg.R0, Reg.R0, new Operand2.ImmedOp(rightLength+1)));
            result.add(new BranchIns(BranchIns.MALLOC));
            // add left to buffer (pointer to result buffer now in R0)
            result.add(new MovIns(Reg.R1, new Operand2.RegOp(leftReg)));
            result.add(new BranchIns(BranchIns.STRCAT));
            // load right address into R1
            LabelArm rightLabel = this.dataSection.addNewData(((StringLiteral3) right).getValue());
            result.add(new MemIns(MemIns.Opcode.LDR, Reg.R1, new Address.LabelAddr(rightLabel)));
            // add right to buffer
            result.add(new BranchIns(BranchIns.STRCAT));
            // pointer to result in R0 now
            result.add(new MovIns(destReg, new Operand2.RegOp(Reg.R0)));
        } else if (left instanceof Id3 && varTypeInfo.get(left).equals("String")) { // var+var, string concat
            Reg leftReg = varToReg.get(left);
            Reg rightReg = varToReg.get(right);
            // get left length
            result.add(new MovIns(Reg.R0, new Operand2.RegOp(leftReg)));
            result.add(new BranchIns(BranchIns.STRLEN));
            result.add(new MovIns(Reg.R1, new Operand2.RegOp(Reg.R0)));
            // left length in R1 now, get right length
            result.add(new MovIns(Reg.R0, new Operand2.RegOp(rightReg)));
            result.add(new BranchIns(BranchIns.STRLEN));
            // right length in R0, left length in R1
            result.add(new DataIns(DataIns.Opcode.ADD, Reg.R0, Reg.R0, new Operand2.RegOp(Reg.R1)));
            result.add(new DataIns(DataIns.Opcode.ADD, Reg.R0, Reg.R0, new Operand2.ImmedOp(1)));
            result.add(new BranchIns(BranchIns.MALLOC));
            // pointer to result buffer now in R0
            // add left to buffer
            result.add(new MovIns(Reg.R1, new Operand2.RegOp(leftReg)));
            result.add(new BranchIns(BranchIns.STRCAT));
            // add right to buffer
            result.add(new MovIns(Reg.R1, new Operand2.RegOp(rightReg)));
            result.add(new BranchIns(BranchIns.STRCAT));
            // pointer to result in R0 now
            result.add(new MovIns(destReg, new Operand2.RegOp(Reg.R0)));
        } else { // varOPvar, or var*const, or const*var (guaranteed not string concat)
            Reg leftReg;
            Reg rightReg;
            if (left instanceof IntegerLiteral3) { //const*var
                rightReg = varToReg.get(right);
                // use R0 as scratch since it's safe, destReg may hold right value here
                result.addAll(loadConstantIntoReg((IntegerLiteral3) left, Reg.R0));
                result.add(new MultiplyIns(destReg, Reg.R0, rightReg));
            } else if (right instanceof IntegerLiteral3) { // var*const
                leftReg = varToReg.get(left);
                result.addAll(loadConstantIntoReg((IntegerLiteral3) right, Reg.R0));
                result.add(new MultiplyIns(destReg, leftReg, Reg.R0));
            } else { // varOPvar
                leftReg = varToReg.get(left);
                rightReg = varToReg.get(right);
                if (operator == Operator.TIMES) { //var*var
                    result.add(new MultiplyIns(destReg, leftReg, rightReg));
                } else { // operator can be +, -, ||, &&
                    result.add(new DataIns(DataIns.Opcode.fromOperator(operator), destReg,
                            leftReg, new Operand2.RegOp(rightReg)));
                }
            }
        }
        return result;
    }

    // effect: load RHS expression value into destReg
    // the varType argument is the type of Lhs variable, this is necessary for separate treatment of null
    private List<InsArm> translateRhsExp(Exp3 exp, Reg destReg, String varType) {
        List<InsArm> result = new ArrayList<>();
        if (exp instanceof Access3) {
            Access3 access3 = (Access3) exp;
            Id3 objectVar = access3.getLhs();
            Reg objectReg = varToReg.get(access3.getLhs());
            String cname = varTypeInfo.get(objectVar);
            int offset = classMap.get(cname).get(access3.getRhs().toString());
            result.add(new MemIns(MemIns.Opcode.LDR, destReg, new Address.RegPreIndexed(objectReg, offset)));
        }
        if (exp instanceof BinaryOp3) {
            // binaryOp3 contains its own operators as well as the RelationOp3 operators
            BinaryOp3 binaryOp3 = (BinaryOp3) exp;
            Operator operator = binaryOp3.getOperator();
            if (operator.isRelational()) {
                // call the relationOp sub-routine
                RelationOp3 relationOp3 = new RelationOp3(binaryOp3);
                result.addAll(translateRelationOp3(relationOp3, destReg));
            } else {
                // call the binaryOp sub-routine
                result.addAll(translateBinaryOp3(binaryOp3, destReg));
            }
        }
        if (exp instanceof UnaryOp3) {
            // use R2 as scratch
            UnaryOp3 unaryOp3 = (UnaryOp3) exp;
            Operator operator = unaryOp3.getOperator();
            Idc3 operand = unaryOp3.getOperand();
            Reg operandReg = Reg.R2;
            if (operand instanceof Id3) {
                operandReg = varToReg.get(operand);
            } else { // operand is either booleanLiteral or IntegerLiteral
                result.addAll(loadConstantIntoReg(operand, operandReg));
            }
            // operand is now in operandReg
            if (operator == Operator.NOT) {
                result.add(new MovIns(MovIns.Opcode.MVN, destReg, new Operand2.RegOp(operandReg)));
            } else { // Operator.MINUS
                result.add(new DataIns(DataIns.Opcode.RSB, destReg, operandReg, new Operand2.ImmedOp(0)));
            }
        }
        if (exp instanceof Id3) {
            Id3 id3 = (Id3) exp;
            result.add(new MovIns(destReg, new Operand2.RegOp(varToReg.get(id3))));
        }
        if(exp instanceof MethodCallExp3) {
            MethodCallExp3 methodCallExp3 = (MethodCallExp3) exp;
            Id3 methodName = methodCallExp3.getMethodName();
            List<Idc3> argList = methodCallExp3.getArgList();
            result.addAll(translateMethodCall(methodName, argList));
            // transfer the return result to R3
            result.add(new MovIns(destReg, new Operand2.RegOp(Reg.R0)));
        }
        if (exp instanceof New3) {
            New3 new3 = (New3) exp;
            String cname = new3.getCname();
            // decide size for calloc
            Map<String, Integer> fieldsMap = classMap.get(cname);
            int callocSize = fieldsMap.size() * 4;
            result.add(new MovIns(Reg.R0, new Operand2.ImmedOp(1)));
            result.add(new MovIns(Reg.R1, new Operand2.ImmedOp(callocSize)));
            result.add(new BranchIns(BranchIns.CALLOC));
            // move returned pointer to destReg
            result.add(new MovIns(destReg, new Operand2.RegOp(Reg.R0)));
        }
        if (exp instanceof Null3) {
            if (varType.equals("String")) { // rhs should be perceived as empty String
                LabelArm label = this.dataSection.addNewData("");
                result.add(new MemIns(MemIns.Opcode.LDR, destReg, new Address.LabelAddr(label)));
            } else { // rhs is null for some object type, load a null pointer to destReg
                result.add(new MovIns(destReg, new Operand2.ImmedOp(0)));
            }
        }
        if (exp instanceof RelationOp3) {
            RelationOp3 relationOp3 = (RelationOp3) exp;
            result.addAll(translateRelationOp3(relationOp3, destReg));
        }
        if (exp instanceof BooleanLiteral3 || exp instanceof IntegerLiteral3 || exp instanceof StringLiteral3) {
            Idc3 constant = (Idc3) exp;
            result.addAll(loadConstantIntoReg(constant, destReg));
        }
        return result;
    }

    // return boolean if can be reduced; return null if cannot reduce
    private Boolean attemptToReduceRelationOp3(RelationOp3 relationOp3) {
        Idc3 left = relationOp3.getLeft();
        Idc3 right = relationOp3.getRight();
        if (left instanceof IntegerLiteral3 && right instanceof IntegerLiteral3) {
            int leftVal = ((IntegerLiteral3) left).getValue();
            int rightVal = ((IntegerLiteral3) right).getValue();
            return relationOp3.getOperator().relationalApplyTo(leftVal, rightVal);
        } else {
            return null;
        }
    }

    // pre-condition: of the form varCMPconst or varCMPvar
    private InsArm genCmpInsFromRelationOp(RelationOp3 relationOp3) {
        Id3 left = (Id3) relationOp3.getLeft();
        Idc3 right = relationOp3.getRight();
        if (right instanceof IntegerLiteral3) {
            return new CmpIns(varToReg.get(left), new Operand2.ImmedOp(((IntegerLiteral3) right).getValue()));
        } else {
            return new CmpIns(varToReg.get(left), new Operand2.RegOp(varToReg.get((Id3) right)));
        }
    }

    // pre-condition: first argument is constant, not variable
    private List<InsArm> loadConstantIntoReg(Idc3 constant, Reg destReg) {
        List<InsArm> result = new ArrayList<>();
        if (constant instanceof IntegerLiteral3) {
            IntegerLiteral3 integerConst = (IntegerLiteral3) constant;
            int val = integerConst.getValue();
            result.add(new MovIns(destReg, new Operand2.ImmedOp(val)));
        }
        if (constant instanceof BooleanLiteral3) {
            int val = ((BooleanLiteral3) constant).getValue() ? 1 : 0;
            result.add(new MovIns(destReg, new Operand2.ImmedOp(val)));
        }
        if (constant instanceof StringLiteral3) {
            String val = ((StringLiteral3) constant).getValue();
            LabelArm label = this.dataSection.addNewData(val);
            result.add(new MemIns(MemIns.Opcode.LDR, destReg, new Address.LabelAddr(label)));
        }
        if (constant instanceof Null3) { // treat as empty string
            LabelArm label = this.dataSection.addNewData("");
            result.add(new MemIns(MemIns.Opcode.LDR, destReg, new Address.LabelAddr(label)));
        }
        return result;
    }

    private List<InsArm> prepareArgToStack(Idc3 arg, int offsetFromSP) {
        List<InsArm> result = new ArrayList<>();
        Reg tempReg = Reg.R1;
        if (arg instanceof Id3) { // if arg is variable, it should already be in some Reg
            tempReg = varToReg.get(arg);
        } else { // if arg is a constant, move it to R1 so it can be stored to stack later
            result.addAll(loadConstantIntoReg(arg, tempReg));
        }
        // now we can store tempReg to its corresponding location on stack
        Address.RegPreIndexed addr = new Address.RegPreIndexed(Reg.SP, offsetFromSP);
        result.add(new MemIns(MemIns.Opcode.STR, tempReg, addr));
        return result;
    }

    // generate the instruction to prepare an argument into a register
    // this should be used before making function call with BL
    private InsArm prepareArgToReg(Idc3 arg, Reg destinationReg) {
        if (arg instanceof Id3) {
            Reg argReg = varToReg.get(arg);
            return new MovIns(destinationReg, new Operand2.RegOp(argReg));
        }
        if (arg instanceof IntegerLiteral3) {
            int val = ((IntegerLiteral3) arg).getValue();
            return new MovIns(destinationReg, new Operand2.ImmedOp(val));
        }
        if (arg instanceof BooleanLiteral3) {
            int val = ((BooleanLiteral3) arg).getValue() ? 1 : 0;
            return new MovIns(destinationReg, new Operand2.ImmedOp(val));
        }
        if (arg instanceof StringLiteral3) {
            String val = ((StringLiteral3) arg).getValue();
            LabelArm label = this.dataSection.addNewData(val);
            return new MemIns(MemIns.Opcode.LDR, destinationReg, new Address.LabelAddr(label));
        }
        if (arg instanceof Null3) { // treat as empty string
            LabelArm label = this.dataSection.addNewData("");
            return new MemIns(MemIns.Opcode.LDR, destinationReg, new Address.LabelAddr(label));
        }
        // should never be executed
        return null;
    }
}
