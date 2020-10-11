package com.yuntongzhang.jlitec.ir3;

import java.util.List;
import java.util.stream.Collectors;

import com.yuntongzhang.jlitec.ast.PrettyPrintable;

public class CMtd3 implements PrettyPrintable {
    private Type3 returnType;
    private Id3 methodName;
    // the first item in fmlList is "this" with the class type
    private List<FmlItem3> fmlList;
    private MdBody3 methodBody;

    public CMtd3(Type3 returnType, Id3 methodName, List<FmlItem3> fmlList, MdBody3 methodBody) {
        this.returnType = returnType;
        this.methodName = methodName;
        this.fmlList = fmlList;
        this.methodBody = methodBody;
    }

    @Override
    public void prettyPrint(int indentation) {
        String fmlListString = fmlList.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining(","));
        String open = (returnType.toString() + " " + methodName.toString() + "(" + fmlListString + "){")
                .indent(indentation);
        String close = "}".indent(indentation);

        System.out.print(open);
        methodBody.prettyPrint(indentation + 2);
        System.out.print(close);
        System.out.println();
    }
}
