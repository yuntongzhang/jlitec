package com.yuntongzhang.jlitec.ir3;

import java.util.ArrayList;
import java.util.List;

import com.yuntongzhang.jlitec.ast.*;

public class Ir3Gen {
    private Program program;
    private ClassTable classTable;
    private List<CData3> dataList;
    private List<CMtd3> methodList;

    private String currentClassContext; // store which class is being processed

    public Ir3Gen(Program program) {
        this.program = program;
        this.classTable = ClassTable.genClasssTable(program);
        this.dataList = new ArrayList<>();
        this.methodList = new ArrayList<>();
    }

    public Program3 gen() {
        extractDataMethodFrom(program.getMainClass());
        for (ClassDeclaration classDeclaration : program.getClassDeclarations()) {
            extractDataMethodFrom(classDeclaration);
        }
        return new Program3(dataList, methodList);
    }

    private void extractDataMethodFrom(ClassDeclaration classDeclaration) {
        String cname = classDeclaration.getCname();
        this.currentClassContext = cname;

        // data
        List<VarDeclaration> varDeclarations = classDeclaration.getVarDeclarations();
        CData3 extractedCData3 = new CData3(cname, varDeclarations);
        dataList.add(extractedCData3);

        // method
        List<MethodDeclaration> methodDeclarations = classDeclaration.getMethodDeclarations();
        // for each method declaration, construct the corresponding CMtd3
        for (MethodDeclaration methodDeclaration : methodDeclarations) {
            Type3 returnType = new Type3(methodDeclaration.getReturnType());
            String originalMethodName = methodDeclaration.getId().getName();
            Id3 translatedMethodName = this.classTable.getTranslatedMethodName(currentClassContext, originalMethodName);

            // construct fmlList
            List<FmlItem3> fml3List = new ArrayList<>();
            fml3List.add(new FmlItem3(cname)); // the "this" fmlItem3
            for (FmlItem fmlItem : methodDeclaration.getFmlList()) {
                fml3List.add(new FmlItem3(fmlItem));
            }
            // construct method body
            MethodBody methodBody = methodDeclaration.getMethodBody();
            List<VarDeclaration> methodVarDeclarations = methodBody.getVarDeclarations();
            List<Stmt> methodStmts = methodBody.getStmts();
            // construct stmt3s from stmts
            List<Stmt3> translatedStmt3s = genStmt3Chunk(methodStmts);
            MdBody3 mdBody3 = new MdBody3(methodVarDeclarations, translatedStmt3s);
            methodList.add(new CMtd3(returnType, translatedMethodName, fml3List, mdBody3));
        }
    }

    // entry point for translating a chunk of Stmt.
    private List<Stmt3> genStmt3Chunk(List<Stmt> stmts) {
        List<Stmt3> result = new ArrayList<>();
        Label3 chunkNext = Label3.genNewLabel();
        result.addAll(translateStmtList(stmts, chunkNext));
        // NOTE: end label added here
        result.add(chunkNext);
        return result;
    }

    // the stmt list need to be passed in together with a "next" label for this chunk of stmts.
    private List<Stmt3> translateStmtList(List<Stmt> stmts, Label3 chunkNext) {
        List<Stmt3> result = new ArrayList<>();
        if (stmts.size() == 0) {
            return result;
        } else if (stmts.size() == 1) {
            Stmt lastStmt = stmts.get(0);
            lastStmt.next = chunkNext;
            result.addAll(translateStmt(lastStmt));
            return result;
        } else { // >= 2 statements
            Label3 frontPartNextLabel = Label3.genNewLabel(); //S1.next
            Stmt lastStmt = stmts.get(stmts.size() - 1);
            lastStmt.next = chunkNext;
            result.addAll(translateStmtList(stmts.subList(0, stmts.size() - 1), frontPartNextLabel));
            result.add(frontPartNextLabel);
            result.addAll(translateStmt(lastStmt));
            return result;
        }
    }

    private List<Stmt3> translateStmt(Stmt stmt) {

        if (stmt instanceof IfElseStmt) {
            return translateIfElseStmt((IfElseStmt) stmt);
        }

        if (stmt instanceof WhileStmt) {
            return translateWhileStmt((WhileStmt) stmt);
        }

        if (stmt instanceof ReadlnStmt) {
            return translateReadlnStmt((ReadlnStmt) stmt);
        }

        if (stmt instanceof PrintlnStmt) {
            return translatePrintlnStmt((PrintlnStmt) stmt);
        }

        if (stmt instanceof AssignStmt) {
            return translateAssignStmt((AssignStmt) stmt);
        }

        if (stmt instanceof MethodCallStmt) {
            return translateMethodCallStmt((MethodCallStmt) stmt);
        }

        if (stmt instanceof ReturnStmt) {
            return translateReturnStmt((ReturnStmt) stmt);
        }

        // should never happen
        return new ArrayList<>();
    }

    private List<Stmt3> translateIfElseStmt(IfElseStmt ifElseStmt) {
        List<Stmt3> result = new ArrayList<>();
        Expression condition = ifElseStmt.getCondition();
        condition.trueLabel = Label3.genNewLabel();
        condition.falseLabel = Label3.genNewLabel();
        result.addAll(translateExpForJump(condition));
        result.add(condition.trueLabel);

        List<Stmt> ifBranch = ifElseStmt.getIfBranch();
        List<Stmt> elseBranch = ifElseStmt.getElseBranch();
        result.addAll(translateStmtList(ifBranch, ifElseStmt.next));
        result.add(new Goto3(ifElseStmt.next));
        result.add(condition.falseLabel);
        result.addAll(translateStmtList(elseBranch, ifElseStmt.next));

        return result;
    }

    private List<Stmt3> translateWhileStmt(WhileStmt whileStmt) {
        List<Stmt3> result = new ArrayList<>();
        Label3 beginLabel = Label3.genNewLabel();
        Expression condition = whileStmt.getCondition();
        condition.trueLabel = Label3.genNewLabel();
        condition.falseLabel = whileStmt.next;
        List<Stmt> body = whileStmt.getBody();

        result.add(beginLabel);
        result.addAll(translateExpForJump(condition));
        result.add(condition.trueLabel);
        result.addAll(translateStmtList(body, beginLabel));
        result.add(new Goto3(beginLabel));

        return result;
    }

    private List<Stmt3> translateReadlnStmt(ReadlnStmt readlnStmt) {
        List<Stmt3> result = new ArrayList<>();
        result.add(new Readln3(readlnStmt));
        return result;
    }

    private List<Stmt3> translatePrintlnStmt(PrintlnStmt printlnStmt) {
        List<Stmt3> result = new ArrayList<>();
        Expression arg = printlnStmt.getArg();
        result.addAll(translateExpForValue(arg)); // arg.idc3 set
        result.add(new Println3(arg.idc3));
        return result;
    }

    private List<Stmt3> translateAssignStmt(AssignStmt assignStmt) {
        List<Stmt3> result = new ArrayList<>();
        Atom lhs = assignStmt.getLhs();
        Expression rhs = assignStmt.getRhs();
        // if lhs Atom is of the form _._ , we can't translate lhs to a single Id3
        if (lhs instanceof Access) {
            Access lhsAccess = (Access) lhs;
            Atom lhsAccessLhs = lhsAccess.getAtom();
            Identifier lhsAccessRhs = lhsAccess.getId();
            result.addAll(translateAtomForValue(lhsAccessLhs)); //lhsAccessLhs.idc3 set
            result.addAll(translateExpForValue(rhs)); // rhs.idc set
            result.add(new AssignAccess3((Id3) lhsAccessLhs.idc3, new Id3(lhsAccessRhs), rhs.idc3));
        } else if (lhs instanceof Identifier
                && classTable.isAFieldForClass(currentClassContext, ((Identifier) lhs).getName())) {
            // field = ..., need to add `this` in front
            Id3 fieldId = new Id3((Identifier) lhs);
            Id3 thisId = new Id3();
            result.addAll(translateExpForValue(rhs));
            result.add(new AssignAccess3(thisId, fieldId, rhs.idc3));
        } else {
            result.addAll(translateAtomForValue(lhs)); // lhs.idc3 set
            result.addAll(translateExpForValue(rhs)); // rhs.idc3 set
            result.add(new AssignId3((Id3) lhs.idc3, rhs.idc3));
        }
        return result;
    }

    private List<Stmt3> translateMethodCallStmt(MethodCallStmt methodCallStmt) {
        List<Stmt3> result = new ArrayList<>();
        Atom lhs = methodCallStmt.getAtom();
        List<Expression> argList = methodCallStmt.getExpressionList();

        List<Idc3> translatedArgList = new ArrayList<>();
        Id3 methodNameToUse = null;

        if (lhs instanceof Access) {
            // object.method(args...)
            // lhsAccess here is object.method
            Access lhsAccess = (Access) lhs;
            // lhsAccessLhs should be translated as item and result in one id
            // this id is then used as the "this" argument in the overall translated method call
            Atom lhsAccessLhs = lhsAccess.getAtom();
            result.addAll(translateAtomForValue(lhsAccessLhs)); // lhsAccessLhs.idc3 set
            translatedArgList.add(lhsAccessLhs.idc3);
            // lhsAccessLhs.checkedtype should be the class type
            // Now, find the translated method name according to the .method part using methodTable
            String cname = lhsAccessLhs.checkedType.getName();
            String originalMethodName = lhsAccess.getId().getName();
            methodNameToUse = this.classTable.getTranslatedMethodName(cname, originalMethodName);
        } else {
            // method being called is in the same class as this callstmt
            String originalMethodName = ((Identifier) lhs).getName();
            String cname = this.currentClassContext;
            methodNameToUse = this.classTable.getTranslatedMethodName(cname, originalMethodName);
        }

        for (Expression arg : argList) {
            result.addAll(translateExpForValue(arg)); // arg.idc set
            translatedArgList.add(arg.idc3);
        }

        result.add(new MethodCallStmt3(methodNameToUse, translatedArgList));
        return result;
    }

    private List<Stmt3> translateReturnStmt(ReturnStmt returnStmt) {
        List<Stmt3> result = new ArrayList<>();
        if (returnStmt.hasReturnValue()) {
            Expression returnVal = returnStmt.getReturnValue();
            result.addAll(translateExpForValue(returnVal)); // returnVal.idc3 set
            if (!(returnVal.idc3 instanceof Id3)) {
                // Return3 does not allow Const as return value, but returnVal.idc3 can be Const
                // To fix this, insert an additional assignment stmt3
                Id3 temp = Id3.genNewTemp();
                result.add(new AssignDeclare3(new Type3(returnVal.checkedType), temp, returnVal.idc3));
                returnVal.idc3 = temp;
            }
            result.add(new Return3((Id3) returnVal.idc3));
        } else {
            result.add(new Return3());
        }
        return result;
    }

    // Since type checking has passed,
    // only need to consider BooleanLiteral/BinaryOpComp/BinaryOpLogical/UnaryOpLogical/Atom
    private List<Stmt3> translateExpForJump(Expression expression) {
        List<Stmt3> result = new ArrayList<>();

        if (expression instanceof BooleanLiteral) {
            BooleanLiteral booleanLiteral = (BooleanLiteral) expression;
            if (booleanLiteral.isTrue()) {
                result.add(new Goto3(booleanLiteral.trueLabel));
            } else { //false
                result.add(new Goto3(booleanLiteral.falseLabel));
            }
            return result;
        }

        if (expression instanceof BinaryOpComp) {
            BinaryOpComp binaryOpComp = (BinaryOpComp) expression;
            Expression left = binaryOpComp.getLeftOperand();
            Expression right = binaryOpComp.getRightOperand();
            Operator operator = binaryOpComp.getOperator();
            result.addAll(translateExpForValue(left)); // left.idc3 set
            result.addAll(translateExpForValue(right)); // right.idc3 set
            result.add(new IfGoto3(new RelationOp3(operator, left.idc3, right.idc3), binaryOpComp.trueLabel));
            result.add(new Goto3(binaryOpComp.falseLabel));
            return result;
        }

        if (expression instanceof BinaryOpLogical) {
            BinaryOpLogical binaryOpLogical = (BinaryOpLogical) expression;
            Expression left = binaryOpLogical.getLeftOperand();
            Expression right = binaryOpLogical.getRightOperand();
            if (binaryOpLogical.getOperator() == Operator.OR) { // ||
                left.trueLabel = binaryOpLogical.trueLabel;
                left.falseLabel = Label3.genNewLabel();
                right.trueLabel = binaryOpLogical.trueLabel;
                right.falseLabel = binaryOpLogical.falseLabel;
                result.addAll(translateExpForJump(left));
                result.add(left.falseLabel);
                result.addAll(translateExpForJump(right));
            } else { // &&
                left.trueLabel = Label3.genNewLabel();
                left.falseLabel = binaryOpLogical.falseLabel;
                right.trueLabel = binaryOpLogical.trueLabel;
                right.falseLabel = binaryOpLogical.falseLabel;
                result.addAll(translateExpForJump(left));
                result.add(left.trueLabel);
                result.addAll(translateExpForJump(right));
            }
            return result;
        }

        if (expression instanceof UnaryOpLogical) {
            UnaryOpLogical unaryOpLogical = (UnaryOpLogical) expression;
            Expression operand = unaryOpLogical.getOperand();
            operand.trueLabel = unaryOpLogical.falseLabel;
            operand.falseLabel = unaryOpLogical.trueLabel;
            result.addAll(translateExpForJump(operand));
            return result;
        }

        if (expression instanceof Atom) {
            Atom atom = (Atom) expression;
            result.addAll(translateAtomForValue(atom));
            result.add(new IfGoto3(atom.idc3, atom.trueLabel));
            result.add(new Goto3(atom.falseLabel));
            return result;
        }

        // should never happen
        return result;
    }

    // Case (1): return list of generated new statements, set idc3 pointer
    // Case (2): return empty list, set idc3 pointer
    // Note that this method guarantees to set idc3 pointer for the passed in Ast Node
    private List<Stmt3> translateExpForValue(Expression expression) {
        List<Stmt3> result = new ArrayList<>();
        if (expression instanceof Atom) {
            Atom atom = (Atom) expression;
            result.addAll(translateAtomForValue(atom));
            // atom.idc3 already set
            return result;
        }

        if (expression instanceof IntegerLiteral) {
            IntegerLiteral integerLiteral = (IntegerLiteral) expression;
            integerLiteral.idc3 = new IntegerLiteral3(integerLiteral);
            return result;
        }

        if (expression instanceof BooleanLiteral) {
            BooleanLiteral booleanLiteral = (BooleanLiteral) expression;
            booleanLiteral.idc3 = new BooleanLiteral3(booleanLiteral);
            return result;
        }

        if (expression instanceof StringLiteral) {
            StringLiteral stringLiteral = (StringLiteral) expression;
            stringLiteral.idc3 = new StringLiteral3(stringLiteral);
            return result;
        }

        if (expression instanceof BinaryOperation) {
            BinaryOperation binaryOperation = (BinaryOperation) expression;
            Operator operator = binaryOperation.getOperator();
            Expression left = binaryOperation.getLeftOperand();
            Expression right = binaryOperation.getRightOperand();

            result.addAll(translateExpForValue(left)); // left.idc3 set
            result.addAll(translateExpForValue(right)); // right.idc3 set
            Id3 temp = Id3.genNewTemp();
            result.add(new AssignDeclare3(new Type3(binaryOperation.checkedType),
                    temp, new BinaryOp3(operator, left.idc3, right.idc3)));
            binaryOperation.idc3 = temp;
            return result;
        }

        if (expression instanceof UnaryOperation) {
            UnaryOperation unaryOperation = (UnaryOperation) expression;
            Operator operator = unaryOperation.getOperator();
            Expression operand = unaryOperation.getOperand();

            result.addAll(translateExpForValue(operand)); // operand.idc3 set
            Id3 temp = Id3.genNewTemp();
            result.add(new AssignDeclare3(new Type3(unaryOperation.checkedType),
                    temp, new UnaryOp3(operator, operand.idc3)));
            unaryOperation.idc3 = temp;
            return result;
        }

        // should never happen
        return result;
    }

    // Case (1): return list of generated new statements, set idc3 pointer
    // Case (2): return empty list, set idc3 pointer
    // Note that this method guarantees to set idc3 pointer for the passed in Ast Node
    private List<Stmt3> translateAtomForValue(Atom atom) {
        List<Stmt3> result = new ArrayList<>();

        if (atom instanceof Null) {
            Null n = (Null) atom;
            Id3 temp = Id3.genNewTemp();
            result.add(new AssignDeclare3(new Type3(n.checkedType), temp, new Null3()));
            n.idc3 = temp;
            return result;
        }
        if (atom instanceof ParenthesizedExp) {
            ParenthesizedExp parenthesizedExp = (ParenthesizedExp) atom;
            Expression insideExpression = parenthesizedExp.getExpression();
            result.addAll(translateExpForValue(insideExpression)); // insideExpression.idc3 set
            parenthesizedExp.idc3 = insideExpression.idc3;
            return result;
        }
        if (atom instanceof New) {
            New n = (New) atom;
            Id3 temp = Id3.genNewTemp();
            result.add(new AssignDeclare3(new Type3(n.checkedType), temp, new New3(n)));
            n.idc3 = temp;
            return result;
        }
        if (atom instanceof Identifier) {
            Identifier identifier = (Identifier) atom;
            String idName = identifier.getName();
            if (classTable.isAFieldForClass(currentClassContext, idName)) {
                // identifier is a field in the current class, have to generate "this.idName"
                Access3 access = new Access3(new Id3(), new Id3(identifier));
                Id3 temp = Id3.genNewTemp();
                result.add(new AssignDeclare3(new Type3(identifier.checkedType), temp, access));
                identifier.idc3 = temp;
            } else {
                // normal identifier, just create new ir3 node and assign idc3
                identifier.idc3 = new Id3(identifier);
            }
            return result;
        }
        if (atom instanceof This) {
            This t = (This) atom;
            t.idc3 = new Id3();
            return result;
        }
        if (atom instanceof MethodCall) {
            MethodCall methodCall = (MethodCall) atom;
            Atom lhs = methodCall.getAtom();
            List<Expression> argList = methodCall.getExpressionList();

            List<Idc3> translatedArgList = new ArrayList<>();
            Id3 methodNameToUse = null;

            if (lhs instanceof Access) {
                // object.method(args...)
                // lhsAccess here is object.method
                Access lhsAccess = (Access) lhs;
                // lhsAccessLhs should be translated as item and result in one id
                // this id is then used as the "this" argument in the overall translated method call
                Atom lhsAccessLhs = lhsAccess.getAtom();
                result.addAll(translateAtomForValue(lhsAccessLhs)); // lhsAccessLhs.idc3 set
                translatedArgList.add(lhsAccessLhs.idc3);
                // lhsAccessLhs.checkedtype should be the class type
                // Now, find the translated method name according to the .method part using methodTable
                String cname = lhsAccessLhs.checkedType.getName();
                String originalMethodName = lhsAccess.getId().getName();
                methodNameToUse = this.classTable.getTranslatedMethodName(cname, originalMethodName);
            } else {
                // method being called is in the same class as this callstmt
                String originalMethodName = ((Identifier) lhs).getName();
                String cname = this.currentClassContext;
                methodNameToUse = this.classTable.getTranslatedMethodName(cname, originalMethodName);
            }

            for (Expression arg : argList) {
                result.addAll(translateExpForValue(arg)); // arg.idc set
                translatedArgList.add(arg.idc3);
            }

            // generate assignment to the call result
            Id3 temp = Id3.genNewTemp();
            result.add(new AssignDeclare3(new Type3(methodCall.checkedType),
                    temp, new MethodCallExp3(methodNameToUse, translatedArgList)));
            methodCall.idc3 = temp;
            return result;
        }
        if (atom instanceof Access) {
            Access access = (Access) atom;
            Atom accessAtom = access.getAtom();
            Identifier accessId = access.getId();
            // translate accessAtom
            result.addAll(translateAtomForValue(accessAtom)); // accessAtom.idc3 set
            // generate assignment to the access result
            Id3 temp = Id3.genNewTemp();
            // can cast because lhs of access can never be translated to constant
            Id3 accessAtomId3 = (Id3) accessAtom.idc3;
            result.add(new AssignDeclare3(new Type3(access.checkedType),
                    temp, new Access3(accessAtomId3, new Id3(accessId))));
            access.idc3 = temp;
            return result;
        }

        // should never happen
        return result;
    }
}
