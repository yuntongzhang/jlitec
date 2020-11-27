package com.yuntongzhang.jlitec.ir3;

import java.util.List;

import com.yuntongzhang.jlitec.ast.PrettyPrintable;

public class Program3 implements PrettyPrintable {
    private List<CData3> dataList;
    private List<CMtd3> methodList;

    public Program3(List<CData3> dataList, List<CMtd3> methodList) {
        this.dataList = dataList;
        this.methodList = methodList;
    }

    public List<CData3> getDataList() {
        return dataList;
    }

    public List<CMtd3> getMethodList() {
        return methodList;
    }

    @Override
    public void prettyPrint(int indentation) {
        System.out.println("======================== CData3 ========================");
        System.out.println();
        for (CData3 cData3 : dataList) {
            cData3.prettyPrint(indentation);
        }
        System.out.println();
        System.out.println("======================== CMtd3 =========================");
        System.out.println();
        for (CMtd3 cMtd3 : methodList) {
            cMtd3.prettyPrint(indentation);
        }
        System.out.println();
        System.out.println("================== End of IR3 Program ==================");
    }
}
