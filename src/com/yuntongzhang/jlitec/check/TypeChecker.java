package com.yuntongzhang.jlitec.check;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.yuntongzhang.jlitec.ast.*;
import com.yuntongzhang.jlitec.exceptions.NameNotFoundError;
import com.yuntongzhang.jlitec.exceptions.SemanticError;
import com.yuntongzhang.jlitec.exceptions.TypeError;


public class TypeChecker {
    // primitive types used in type checking
    private static final SType intType = new SType("Int");
    private static final SType stringType = new SType("String");
    private static final SType boolType = new SType("Bool");
    private static final SType voidType = new SType("Void");

    private Program program;
    private ClassDescriptor classDescriptor;
    private LocalEnvironment env;

    public TypeChecker(Program program) {
        this.program = program;
        this.classDescriptor = new ClassDescriptor(program);
        this.env = new LocalEnvironment();
    }

    public void check() throws SemanticError {
        checkClass(program.getMainClass());
        for (ClassDeclaration classDeclaration : program.getClassDeclarations()) {
            checkClass(classDeclaration);
        }
    }

    private void checkClass(ClassDeclaration classDeclaration) throws SemanticError {
        env.setToClass(classDeclaration, classDescriptor);
        for (MethodDeclaration methodDeclaration : classDeclaration.getMethodDeclarations()) {
            checkMethod(methodDeclaration);
        }
    }

    private void checkMethod(MethodDeclaration methodDeclaration) throws SemanticError {
        Identifier methodId = methodDeclaration.getId();
        FuncType funcType = env.lookupMethod(methodId.getName(), methodId.getLoc());
        // extend environment for method parameters
        Map<String, SType> newVarStore = new HashMap<>();
        // generate the new var store by match retrieved parameter types with parameter names declared.
        List<SType> paraTypes = funcType.getParaTypes();
        List<String> paraNames = methodDeclaration.getFmlList().stream().map(f -> f.getId().getName())
                .collect(Collectors.toList());
        for (int i = 0; i < paraNames.size(); i++) {
            newVarStore.put(paraNames.get(i), paraTypes.get(i));
        }
        newVarStore.put("Ret", funcType.getReturnType());
        env = env.extend(newVarStore);
        // check return type
        SType checkedReturnType = checkMethodBody(methodDeclaration.getMethodBody());
        if (!checkedReturnType.equals(funcType.getReturnType())) {
            throw new TypeError("Type of last statement in method does not match with the return type!",
                    methodDeclaration.getLoc());
        }
        // after checking a method, pop the current env created for this method
        env = env.popEnv();
    }

    private SType checkMethodBody(MethodBody methodBody) throws SemanticError {
        return checkBlock(methodBody.getVarDeclarations(), methodBody.getStmts());
    }

    // [Block]
    private SType checkBlock(List<VarDeclaration> varDeclarations, List<Stmt> statements) throws SemanticError {
        Map<String, SType> newVarStore = new HashMap<>();
        for (VarDeclaration varDeclaration : varDeclarations) {
            newVarStore.put(varDeclaration.getId().getName(), varDeclaration.getType());
        }
        env = env.extend(newVarStore);
        SType result = checkStmtSeq(statements);
        env = env.popEnv();
        return result;
    }

    // [Seq]
    // [Empty statement]
    private SType checkStmtSeq(List<Stmt> statements) throws SemanticError {
        SType result = voidType;
        for (Stmt statement : statements) {
            result = checkStmt(statement);
        }
        return result;
    }

    private SType checkStmt(Stmt statement) throws SemanticError {
        // [VarAss]
        // [FdAss]
        if (statement instanceof AssignStmt) {
            AssignStmt assignStmt = (AssignStmt) statement;
            Atom assignLhs = assignStmt.getLhs();
            SType lhsType = null;
            if (assignLhs instanceof Identifier) {
                Identifier assignLhsIdentifier = (Identifier) assignLhs;
                lhsType = env.lookupVar(assignLhsIdentifier.getName(), assignLhs.getLoc());
            } else if (assignLhs instanceof This) {
                lhsType = env.lookupVar("this", assignLhs.getLoc());
            } else if (assignLhs instanceof Access) {
                Access assignLhsAccess = (Access) assignLhs;
                Atom accessLhs = assignLhsAccess.getAtom();
                SType accessLhsType = checkAtom(accessLhs);
                String accessLhsClassName = accessLhsType.getName();
                String accessRhsName = assignLhsAccess.getId().getName();
                if (!classDescriptor.classContainsField(accessLhsClassName, accessRhsName)) {
                    throw new NameNotFoundError("The name \"" + accessRhsName + "\" is not found in class \""
                            + accessLhsClassName + "\".", accessLhs.getLoc());
                }
                Map<String, SType> fields = classDescriptor.getFieldsForClass(accessLhsClassName, null);
                // confirm can find the wanted field
                lhsType = fields.get(accessRhsName);
            } else {
                // other kind of lhs for assignment is not allowed
                throw new TypeError("Invalid assign statement LHS!", assignStmt.getLoc());
            }
            // Now, get RHS type and compare with LHS type
            Expression assignRhs = assignStmt.getRhs();
            Type rhsType;
            if (assignRhs instanceof Null) {
                // SPECIAL: RHS of assignment is null
                if (lhsType.isNullable()) {
                    rhsType = lhsType;
                    assignRhs.checkedType = lhsType;
                } else {
                    throw new TypeError(lhsType.getName() + " type cannot be null!", assignRhs.getLoc());
                }
            } else {
                rhsType = checkExpression(assignStmt.getRhs());
            }
            if (!lhsType.equals(rhsType)) {
                throw new TypeError("LHS and RHS of assignment need to have same type!", assignStmt.getLoc());
            }
            return voidType;
        }

        // [Cond]
        // Don't need new env since var declaration not allowed inside
        if (statement instanceof IfElseStmt) {
            IfElseStmt ifElseStmt = (IfElseStmt) statement;
            SType ifBranchType = checkStmtSeq(ifElseStmt.getIfBranch());
            SType elseBranchType = checkStmtSeq(ifElseStmt.getElseBranch());
            Type conditionType = checkExpression(ifElseStmt.getCondition());
            if (!(boolType.equals(conditionType))) {
                throw new TypeError("If condition needs to be Bool type!", ifElseStmt.getLoc());
            }
            if (!(ifBranchType.equals(elseBranchType))) {
                throw new TypeError("If branch and Else branch need to have same type!", ifElseStmt.getLoc());
            }
            return elseBranchType;
        }

        // [While]
        // Don't need new env since var declaration not allowed inside
        if (statement instanceof WhileStmt) {
            WhileStmt whileStmt = (WhileStmt) statement;
            Type conditionType = checkExpression(whileStmt.getCondition());
            if (!(boolType.equals(conditionType))) {
                throw new TypeError("While condition needs to be Bool type!", whileStmt.getLoc());
            }
            SType bodyType = checkStmtSeq(whileStmt.getBody());
            return bodyType;
        }

        // [Read]
        if (statement instanceof ReadlnStmt) {
            ReadlnStmt readlnStmt = (ReadlnStmt) statement;
            SType argType = env.lookupVar(readlnStmt.getArg().getName(), readlnStmt.getLoc());
            if (!intType.equals(argType) && !boolType.equals(argType) && !stringType.equals(argType)) {
                throw new TypeError("readln needs to have argument with Int/Bool/String type!", readlnStmt.getLoc());
            }
            return voidType;
        }

        // [Print]
        if (statement instanceof PrintlnStmt) {
            PrintlnStmt printlnStmt = (PrintlnStmt) statement;
            Expression arg = printlnStmt.getArg();
            Type argType;
            if (arg instanceof Null) {
                // null as argument to println should be interpreted as empty String
                // It is not sensible if null is of other Nullable type
                argType = stringType;
                arg.checkedType = stringType;
            } else {
                argType = checkExpression(printlnStmt.getArg());
            }
            if (!intType.equals(argType) && !boolType.equals(argType) && !stringType.equals(argType)) {
                throw new TypeError("println needs to have argument with Int/Bool/String type!", printlnStmt.getLoc());
            }
            return voidType;
        }

        // [SLocalCall]
        // [SGlobalCall]
        if (statement instanceof MethodCallStmt) {
            MethodCallStmt methodCallStmt = (MethodCallStmt) statement;
            return checkMethodCall(methodCallStmt.getAtom(), methodCallStmt.getExpressionList());
        }

        // [Ret-T]
        // [Ret-Void]
        if (statement instanceof ReturnStmt) {
            ReturnStmt returnStmt = (ReturnStmt) statement;
            if (returnStmt.hasReturnValue()) {
                Expression returnVal = returnStmt.getReturnValue();
                Type returnType = checkExpression(returnVal);
                SType lookedUpType = env.lookupVar("Ret", null);
                if (!lookedUpType.equals(returnType)) {
                    throw new TypeError("Return value is of wrong type!", returnStmt.getLoc());
                }
                return lookedUpType;
            } else {
                SType lookedUpType = env.lookupVar("Ret", null);
                if (!(lookedUpType.equals(voidType))) {
                    throw new TypeError("Return statement does not have return value, " +
                            "but declared return type of method is not Void!", returnStmt.getLoc());
                }
                return voidType;
            }
        }

        throw new SemanticError("This type of statement is not recognized!", statement.getLoc());
    }

    // methodCallLhs will be checked into a FuncType, thus not calling checkAtom on it
    private SType checkMethodCall(Atom methodCallLhs, List<Expression> argList) throws SemanticError {
        // look up the method signature : funcType
        FuncType funcType = null;
        // lhs of method call can be either just id or (possibly chained) Access
        if (methodCallLhs instanceof Identifier) {
            Identifier methodCallLhsId = (Identifier) methodCallLhs;
            funcType = env.lookupMethod(methodCallLhsId.getName(), methodCallLhs.getLoc());
        } else if (methodCallLhs instanceof Access) {
            Access methodCallLhsAccess = (Access) methodCallLhs;
            Atom accessLhs = methodCallLhsAccess.getAtom(); // can be id or other kinds of Atom
            SType accessLhsType = checkAtom(accessLhs);
            String accessLhsClassName = accessLhsType.getName();
            String accessRhsName = methodCallLhsAccess.getId().getName();
            if (!classDescriptor.classContainsMethod(accessLhsClassName, accessRhsName)) {
                throw new NameNotFoundError("The name \"" + accessRhsName + "\" is not found in class \""
                        + accessLhsClassName + "\".", accessLhs.getLoc());
            }
            Map<String, FuncType> methods = classDescriptor.getMethodsForClass(accessLhsClassName, null);
            // confirm can find the wanted method
            funcType = methods.get(accessRhsName);
        }
        // Now, check whether the actual argument types follow the looked up funcType
        List<SType> paraTypes = funcType.getParaTypes();
        for (int i = 0; i < paraTypes.size(); i++) {
            // type of formal parameter
            SType paraType = paraTypes.get(i);
            // type of actual argument
            Expression arg = argList.get(i);
            Type argType;
            if (arg instanceof Null) {
                // SPECIAL: actual argument is null
                if (paraType.isNullable()) {
                    argType = paraType;
                    arg.checkedType = paraType;
                } else {
                    throw new TypeError(paraType.getName() + " type cannot be null!", arg.getLoc());
                }
            } else {
                argType = checkExpression(arg);
            }

            if (!paraType.equals(argType)) {
                throw new TypeError("Method call has argument with wrong type!", arg.getLoc());
            }
        }
        return funcType.getReturnType();
    }

    // This method saves the checked type
    private SType checkAtom(Atom atom) throws SemanticError {
        SType result = null;

        // type of a Null atom cannot be inferred from it alone
        // it has to be determined from the context
        // Thus, the context should set the Null atom's checkedType
        // Contexts (also called the allowable places) to consider:
        // (1) Null as argument to method call
        // (2) Null as argument to println
        // (3) Null as the RHS of assignment
        // (4) Null as operand to +, where Null is of String Type

        // To handle null, I make the allowable places to handle null by itself,
        // and the unallowable places to call the normal Expresson/Atom routine,
        // with the normal routine for null to signal error
        if (atom instanceof Null) {
            throw new TypeError("Null is not allowed in this context!", atom.getLoc());
        }

        if (atom instanceof ParenthesizedExp) {
            ParenthesizedExp parenthesizedExp = (ParenthesizedExp) atom;
            Expression insideExpression = parenthesizedExp.getExpression();
            result = checkExpression(insideExpression);
        }

        // [New]
        if (atom instanceof New) {
            New n = (New) atom;
            String cname = n.getClassName();
            if (!classDescriptor.containsClass(cname)) {
                throw new NameNotFoundError("The declaration of class name \"" + cname + "\" is not found.",
                        n.getLoc());
            }
            result = new SType(cname);
        }

        // [Id]
        if (atom instanceof Identifier) {
            // if Identifier is for a method, it is consider at other places instead of here
            Identifier id = (Identifier) atom;
            result = env.lookupVar(id.getName(), id.getLoc());
        }

        // [Id]
        if (atom instanceof This) {
            result = env.lookupVar("this", atom.getLoc());
        }

        // [LocalCall]
        // [GlobalCall]
        if (atom instanceof MethodCall) {
            MethodCall methodCall = (MethodCall) atom;
            result = checkMethodCall(methodCall.getAtom(), methodCall.getExpressionList());
        }

        // [Field]
        // object.method is not considered, since it is considered in the method call case
        if (atom instanceof Access) {
            Access access = (Access) atom;
            SType lType = checkAtom(access.getAtom());
            String cname = lType.getName();
            String rhsName = access.getId().getName();
            // rhs of Access can only possibly be a field here
            if (classDescriptor.classContainsField(cname, rhsName)) {
                Map<String, SType> fields = classDescriptor.getFieldsForClass(cname, null);
                result = fields.get(rhsName);
            } else {
                // rhs is not a field (not found)
                throw new NameNotFoundError("The name \"" + rhsName + "\" is not found in class \"" + cname + "\".",
                        access.getId().getLoc());
            }
        }

        // save checked type
        atom.checkedType = result;
        return result;
    }

    // This method saves the checked type
    private SType checkExpression(Expression expression) throws SemanticError {
        SType result = null;

        if (expression instanceof Atom) {
            result = checkAtom((Atom) expression);
        }

        // [Integers]
        if (expression instanceof IntegerLiteral) {
            result = intType;
        }

        // [Booleans]
        if (expression instanceof BooleanLiteral) {
            result = boolType;
        }

        if (expression instanceof StringLiteral) {
            result = stringType;
        }

        // [Arith]
        // [String]
        if (expression instanceof BinaryOpArithmetic) {
            BinaryOpArithmetic arith = (BinaryOpArithmetic) expression;
            Operator operator = arith.getOperator();
            Expression left = arith.getLeftOperand();
            Expression right = arith.getRightOperand();
            Type leftType;
            Type rightType;
            // Consider the null SPECIAL case first
            if (left instanceof Null) {
                leftType = stringType;
                left.checkedType = stringType;
            } else {
                leftType = checkExpression(left);
            }
            if (right instanceof Null) {
                rightType = stringType;
                right.checkedType = stringType;
            } else {
                rightType = checkExpression(right);
            }
            // Now consider + and other operators separately
            if (operator == Operator.PLUS) {
                // either allow (1) both operands are Int type; (2) both operands are String type
                if (intType.equals(leftType) && intType.equals(rightType)) {
                    result = intType;
                } else if (stringType.equals(leftType) && stringType.equals(rightType)) {
                    result = stringType;
                } else { // invalid operand types
                    throw new TypeError("Invalid operand types for the operator +", arith.getLoc());
                }
            } else {
                // do the normal [Arith] check
                if (!(intType.equals(leftType)) || !(intType.equals(rightType))) {
                    throw new TypeError("Both operands of arithmetic expression need to be Int type!", arith.getLoc());
                }
                result = intType;
            }
        }

        // [Negation]
        if (expression instanceof UnaryOpArithmetic) {
            UnaryOpArithmetic arith = (UnaryOpArithmetic) expression;
            Expression operand = arith.getOperand();
            Type operandType = checkExpression(operand);
            if (!(intType.equals(operandType))) {
                throw new TypeError("Arithmetic negation need to have operand of Int type!", arith.getLoc());
            }
            result = intType;
        }

        // [Rel]
        if (expression instanceof BinaryOpComp) {
            BinaryOpComp comp = (BinaryOpComp) expression;
            Expression left = comp.getLeftOperand();
            Type leftType = checkExpression(left);
            Expression right = comp.getRightOperand();
            Type rightType = checkExpression(right);
            if (!(intType.equals(leftType)) || !(intType.equals(rightType))) {
                throw new TypeError("Both operands of relational expression need to be Int type!", comp.getLoc());
            }
            result = boolType;
        }

        // [Bool]
        if (expression instanceof BinaryOpLogical) {
            BinaryOpLogical logical = (BinaryOpLogical) expression;
            Expression left = logical.getLeftOperand();
            Type leftType = checkExpression(left);
            Expression right = logical.getRightOperand();
            Type rightType = checkExpression(right);
            if (!(boolType.equals(leftType)) || !(boolType.equals(rightType))) {
                throw new TypeError("Both operands of boolean expression need to be Bool type!", logical.getLoc());
            }
            result = boolType;
        }

        // [Complement]
        if (expression instanceof UnaryOpLogical) {
            UnaryOpLogical logical = (UnaryOpLogical) expression;
            Expression operand = logical.getOperand();
            Type operandType = checkExpression(operand);
            if (!(boolType.equals(operandType))) {
                throw new TypeError("Logical complement need to have operand of Bool type!", logical.getLoc());
            }
            result = boolType;
        }

        // save checked type
        expression.checkedType = result;
        return result;
    }
}
