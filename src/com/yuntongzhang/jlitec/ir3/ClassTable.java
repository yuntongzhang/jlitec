package com.yuntongzhang.jlitec.ir3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.yuntongzhang.jlitec.ast.ClassDeclaration;
import com.yuntongzhang.jlitec.ast.MethodDeclaration;
import com.yuntongzhang.jlitec.ast.Program;

// facilitate translating method calls in the form of object.method(...)
// also facilitate translating use of class field in method body
public class ClassTable {
    // class name -> [<original name, translated name>, ...]
    private Map<String, List<MethodPair>> classToMethod;
    private Map<String, List<String>> classToField;

    private ClassTable(Map<String, List<MethodPair>> classToMethod, Map<String, List<String>> classToField) {
        this.classToMethod = classToMethod;
        this.classToField = classToField;
    }

    // static generation (serve as constructor)
    public static ClassTable genClasssTable(Program program) {
        // methods
        Map<String, List<MethodPair>> methodResult = new HashMap<>();
        // MainClass only have one method
        MethodPair mainMethod = new MethodPair("main", "main");
        List<MethodPair> mainClassMethodList = new ArrayList<>();
        mainClassMethodList.add(mainMethod);
        methodResult.put(program.getMainClass().getCname(), mainClassMethodList);
        // handle other classes
        List<ClassDeclaration> classDeclarations = program.getClassDeclarations();
        for (ClassDeclaration classDeclaration : classDeclarations) {
            String cname = classDeclaration.getCname();
            List<MethodPair> methodList = new ArrayList<>();
            int counter = 0;
            for (MethodDeclaration methodDeclaration : classDeclaration.getMethodDeclarations()) {
                String methodName = methodDeclaration.getId().getName();
                String translatedMethodName = "%" + cname + "_" + counter;
                methodList.add(new MethodPair(methodName, translatedMethodName));
                counter++;
            }
            methodResult.put(cname, methodList);
        }

        // fields (only need to record what field names are valid
        Map <String, List<String>> fieldResult = new HashMap<>();
        // MainClass has no field
        fieldResult.put(program.getMainClass().getCname(), new ArrayList<>());
        // handle other classes
        for (ClassDeclaration classDeclaration : classDeclarations) {
            String cname = classDeclaration.getCname();
            List<String> fieldList = classDeclaration.getVarDeclarations().stream()
                    .map(e -> e.getId().getName())
                    .collect(Collectors.toList());
            fieldResult.put(cname, fieldList);
        }

        return new ClassTable(methodResult, fieldResult);
    }

    public Id3 getTranslatedMethodName(String cname, String originalMethodName) {
        List<MethodPair> methodList = classToMethod.get(cname);
        for (MethodPair methodPair : methodList) {
            if (methodPair.originalName.equals(originalMethodName)) {
                return methodPair.translatedName;
            }
        }
        // should never happen
        return null;
    }

    public boolean isAFieldForClass(String cname, String idName) {
        List<String> fieldsForClass = classToField.get(cname);
        return fieldsForClass.contains(idName);
    }


    private static class MethodPair {
        String originalName;
        Id3 translatedName;

        public MethodPair(String originalName, String translatedName) {
            this.originalName = originalName;
            this.translatedName = new Id3(translatedName);
        }
    }
}
