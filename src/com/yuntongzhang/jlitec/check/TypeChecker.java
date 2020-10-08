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
    // TODO: handle cases for NULL
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
            // TODO: throw exception
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
                Type accessLhsType = checkAtom(accessLhs);
                if (!(accessLhsType instanceof SType)) { // method.__ is not allowed
                    throw new TypeError("LHS of Access cannot be a method!", accessLhs.getLoc());
                }
                String accessLhsClassName = ((SType) accessLhsType).getName();
                String accessRhsName = assignLhsAccess.getId().getName();
                if (!classDescriptor.classContainsField(accessLhsClassName, accessRhsName)) {
                    throw new NameNotFoundError("The name \"" + accessRhsName + "\" is not found in class \""
                            + accessLhsClassName + "\".", accessLhs.getLoc());
                }
                Map<String, SType> fields = classDescriptor.getFieldsForClass(accessLhsClassName, null);
                // confirm can find the wanted field
                lhsType = fields.get(accessRhsName);
            }
            // Now, get RHS type and compare with LHS type
            Type rhsType = checkExpression(assignStmt.getRhs());
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
            Type argType = checkExpression(printlnStmt.getArg());
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
            Type accessLhsType = checkAtom(accessLhs);
            if (!(accessLhsType instanceof SType)) { // method.__ is not allowed
                throw new TypeError("LHS of Access cannot be a method!", accessLhs.getLoc());
            }
            String accessLhsClassName = ((SType) accessLhsType).getName();
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
            Expression arg = argList.get(i);
            Type argType = checkExpression(arg);
            if (!(argType instanceof SType)) {
                throw new TypeError("Argument in method call cannot be function type!", arg.getLoc());
            }
            SType paraType = paraTypes.get(i);
            if (!paraType.equals((SType) argType)) {
                throw new TypeError("Method call has argument with wrong type!", arg.getLoc());
            }
        }
        return funcType.getReturnType();
    }

    private Type checkAtom(Atom atom) throws SemanticError {
        // [Id]
        if (atom instanceof Identifier) {
            // TODO: might need to consider that Identifier can be for method
            Identifier id = (Identifier) atom;
            return env.lookupVar(id.getName(), id.getLoc());
        }

        // [Id]
        if (atom instanceof This) {
            return env.lookupVar("this", atom.getLoc());
        }

        // [Field]
        if (atom instanceof Access) {
            Access access = (Access) atom;
            Type checkedLType = checkAtom(access.getAtom());
            // in Access, lhs cannot be a method name (lhs cannot be FuncType)
            if (!(checkedLType instanceof SType)) {
                throw new TypeError("LHS of Access cannot be a method!", access.getLoc());
            }
            SType lType = (SType) checkedLType;
            String cname = lType.getName();
            // TODO: remove comment below
            // rhs of Access can be a field OR method
            String rhsName = access.getId().getName();
            if (classDescriptor.classContainsField(cname, rhsName)) {
                // rhs of Access is a field
                Map<String, SType> fields = classDescriptor.getFieldsForClass(cname, null);
                return fields.get(rhsName);
//            } else if (classDescriptor.classContainsMethod(cname, rhsName)) {
//                // rhs of Access is a method
//                Map<String, FuncType> methods = classDescriptor.getMethodsForClass(cname, null);
//                return methods.get(rhsName);
            } else {
                // TODO: fix comment below
                // rhs is neither field or method, throw Error
                throw new NameNotFoundError("The name \"" + rhsName + "\" is not found in class \"" + cname + "\".",
                        access.getId().getLoc());
            }
        }

        // [New]
        if (atom instanceof New) {
            New n = (New) atom;
            String cname = n.getClassName();
            if (!classDescriptor.containsClass(cname)) {
                throw new NameNotFoundError("The declaration of class name \"" + cname + "\" is not found.",
                        n.getLoc());
            }
            return new SType(cname);
        }

        // [LocalCall]
        // [GlobalCall]
        if (atom instanceof MethodCall) {
            MethodCall methodCall = (MethodCall) atom;
            return checkMethodCall(methodCall.getAtom(), methodCall.getExpressionList());
        }

        throw new SemanticError("This type of atom is not recognized!", atom.getLoc());
    }

    private Type checkExpression(Expression expression) throws SemanticError {
        if (expression instanceof Atom) {
            return checkAtom((Atom) expression);
        }

        // [Integers]
        if (expression instanceof IntegerLiteral) {
            return intType;
        }

        // [Booleans]
        if (expression instanceof BooleanLiteral) {
            return boolType;
        }

        // [Arith]
        if (expression instanceof BinaryOpArithmetic) {
            // TODO: remove the partial type checks in parser
            BinaryOpArithmetic arith = (BinaryOpArithmetic) expression;
            Expression left = arith.getLeftOperand();
            Type leftType = checkExpression(left);
            Expression right = arith.getRightOperand();
            Type rightType = checkExpression(right);
            if (!(intType.equals(leftType)) || !(intType.equals(rightType))) {
                throw new TypeError("Both operands of arithmetic expression need to be Int type!", arith.getLoc());
            }
            return intType;
        }

        // [Negation]
        if (expression instanceof UnaryOpArithmetic) {
            UnaryOpArithmetic arith = (UnaryOpArithmetic) expression;
            Expression operand = arith.getOperand();
            Type operandType = checkExpression(operand);
            if (!(intType.equals(operandType))) {
                throw new TypeError("Arithmetic negation need to have operand of Int type!", arith.getLoc());
            }
            return intType;
        }

        // [String]
        if (expression instanceof BinaryOpString) {
            BinaryOpString strOp = (BinaryOpString) expression;
            Expression left = strOp.getLeftOperand();
            Type leftType = checkExpression(left);
            Expression right = strOp.getRightOperand();
            Type rightType = checkExpression(right);
            if (!(stringType.equals(leftType)) || !(stringType.equals(rightType))) {
                throw new TypeError("Both operands of string concatenation need to be String type!", strOp.getLoc());
            }
            return stringType;
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
            return boolType;
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
            return boolType;
        }

        // [Complement]
        if (expression instanceof UnaryOpLogical) {
            UnaryOpLogical logical = (UnaryOpLogical) expression;
            Expression operand = logical.getOperand();
            Type operandType = checkExpression(operand);
            if (!(boolType.equals(operandType))) {
                throw new TypeError("Logical complement need to have operand of Bool type!", logical.getLoc());
            }
            return boolType;
        }

        throw new SemanticError("This type of expression is not recognized!", expression.getLoc());
    }
}
