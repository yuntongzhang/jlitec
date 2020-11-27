package com.yuntongzhang.jlitec.asm;

import java.util.HashMap;
import java.util.Map;

public class DataSection {
    private Map<LabelArm, String> dataList;

    public DataSection() {
        this.dataList = new HashMap<>();
    }

    public LabelArm addNewData(String data) {
        for (Map.Entry<LabelArm, String> entry : dataList.entrySet()) {
            if (entry.getValue().equals(data)) { // this data was added before
                return entry.getKey();
            }
        }
        // really new data, create new entry
        LabelArm label = LabelArm.genNewDataLabel();
        dataList.put(label, data);
        return label;
    }

    public void prettyPrint() {
        System.out.println(".data");
        for (Map.Entry<LabelArm, String> entry : dataList.entrySet()) {
            System.out.println(entry.getKey());
            System.out.println(".asciz \"" + entry.getValue() + "\"");
        }
    }
}
