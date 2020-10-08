package com.yuntongzhang.jlitec.check;

import java.util.HashMap;
import java.util.Map;

import com.yuntongzhang.jlitec.ast.ClassDeclaration;
import com.yuntongzhang.jlitec.ast.FuncType;
import com.yuntongzhang.jlitec.ast.Identifier;
import com.yuntongzhang.jlitec.ast.Node;
import com.yuntongzhang.jlitec.ast.SType;
import com.yuntongzhang.jlitec.ast.This;
import com.yuntongzhang.jlitec.ast.Type;
import com.yuntongzhang.jlitec.exceptions.NameNotFoundError;

public class LocalEnvironment {
    // for static scoping
    private LocalEnvironment parentEnv;
    // Note: A class can have a field and a method with the same name.
    private Map<String, SType> varStore;
    private Map<String, FuncType> methodStore;

    // The top-most environment
    public LocalEnvironment() {
        this.parentEnv = null;
        this.varStore = new HashMap<>();
        this.methodStore = new HashMap<>();
    }

    // A new extending environment
    public LocalEnvironment(LocalEnvironment oldEnv, Map<String, SType> varStore) {
        this.parentEnv = oldEnv;
        this.varStore = varStore;
        this.methodStore = new HashMap<>();
    }

    public void setToClass(ClassDeclaration classDeclaration, ClassDescriptor classDescriptor)
            throws NameNotFoundError{
        varStore.clear();
        methodStore.clear();
        varStore.put("this", classDeclaration.getType());
        String cname = classDeclaration.getCname();
        Map<String, SType> fields = classDescriptor.getFieldsForClass(cname, null);
        varStore.putAll(fields);
        Map<String, FuncType> methods = classDescriptor.getMethodsForClass(cname, null);
        methodStore.putAll(methods);
    }

    public FuncType lookupMethod(String name, Node.Location errorLoc) throws NameNotFoundError {
        LocalEnvironment currentEnv = this;
        while (currentEnv != null) {
            FuncType tempResult = currentEnv.methodStore.get(name);
            if (tempResult != null) {
                return tempResult;
            }
            currentEnv = currentEnv.parentEnv;
        }
        // already searched the top-most environment
        if (errorLoc == null) { // no location info
            throw new NameNotFoundError("Declaration of name \"" + name + "\" not found.");
        } else {
            throw new NameNotFoundError("Declaration of name \"" + name + "\" not found.", errorLoc);
        }
    }

    public SType lookupVar(String name, Node.Location errorLoc) throws NameNotFoundError {
        LocalEnvironment currentEnv = this;
        while (currentEnv != null) {
            SType tempResult = currentEnv.varStore.get(name);
            if (tempResult != null) {
                return tempResult;
            }
            currentEnv = currentEnv.parentEnv;
        }
        // already searched the top-most environment
        if (errorLoc == null) { // no location info
            throw new NameNotFoundError("Declaration of name \"" + name + "\" not found.");
        } else {
            throw new NameNotFoundError("Declaration of name \"" + name + "\" not found.", errorLoc);
        }
    }

    public LocalEnvironment extend(Map<String, SType> newVarStore) {
        LocalEnvironment newEnv = new LocalEnvironment(this, newVarStore);
        return newEnv;
    }

    public LocalEnvironment popEnv() {
        return this.parentEnv;
    }
}
