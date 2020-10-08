package com.yuntongzhang.jlitec.check;

import java.util.HashMap;
import java.util.Map;

import javax.naming.Name;

import com.yuntongzhang.jlitec.ast.*;
import com.yuntongzhang.jlitec.exceptions.NameNotFoundError;
import com.yuntongzhang.jlitec.exceptions.SemanticError;

public class ClassDescriptor {
    private Map<String, Description> classes;

    public ClassDescriptor(Program program) {
        this.classes = new HashMap<>();
        initialize(program);
    }

    private void initialize(Program program) {
        // TODO: how about main class?
        for (ClassDeclaration classDeclaration : program.getClassDeclarations()) {
            String cname = classDeclaration.getCname();
            Map<String, SType> fields = new HashMap<>();
            Map<String, FuncType> methods = new HashMap<>();
            for (VarDeclaration varDeclaration : classDeclaration.getVarDeclarations()) {
                String name = varDeclaration.getId().getName();
                SType stype = varDeclaration.getType();
                fields.put(name, stype);
            }
            for (MethodDeclaration methodDeclaration : classDeclaration.getMethodDeclarations()) {
                String name = methodDeclaration.getId().getName();
                FuncType funcType = methodDeclaration.getFuncType();
                methods.put(name, funcType);
            }
            Description description = new Description(cname, fields, methods);
            this.classes.put(cname, description);
        }
    }

    public boolean containsClass(String cname) {
        return classes.containsKey(cname);
    }

    public boolean classContainsField(String cname, String fname) throws NameNotFoundError {
        Description description = getDescription(cname, null);
        return description.fields.containsKey(fname);
    }

    public boolean classContainsMethod(String cname, String mname) throws NameNotFoundError {
        Description description = getDescription(cname, null);
        return description.methods.containsKey(mname);
    }

    // if no location info can be obtained when calling this method, pass in null.
    public Map<String, SType> getFieldsForClass(String cname, Node.Location errorLoc) throws NameNotFoundError {
        Description description = getDescription(cname, errorLoc);
        return description.fields;
    }

    // if no location info can be obtained when calling this method, pass in null.
    public Map<String, FuncType> getMethodsForClass(String cname, Node.Location errorLoc) throws NameNotFoundError {
        Description description = getDescription(cname, errorLoc);
        return description.methods;
    }

    private Description getDescription(String cname, Node.Location errorLoc) throws NameNotFoundError {
        Description description = classes.get(cname);
        if (description == null) { // throw error
            if (errorLoc == null) { // no location info
                throw new NameNotFoundError("The class name \"" + cname + "\" is not found in any class declarations.");
            } else {
                throw new NameNotFoundError("The class name \"" + cname + "\" is not found in any class declarations.",
                        errorLoc);
            }
        }
        return description;
    }

    class Description {
        String cname;
        Map<String, SType> fields;
        Map<String, FuncType> methods;

        public Description(String cname, Map<String, SType> fields, Map<String, FuncType> methods) {
            this.cname = cname;
            this.fields = fields;
            this.methods = methods;
        }
    }
}
