package com.yuntongzhang.jlitec.codegen;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuntongzhang.jlitec.asm.DataSection;
import com.yuntongzhang.jlitec.asm.ProgramArm;
import com.yuntongzhang.jlitec.asm.MethodBlock;
import com.yuntongzhang.jlitec.asm.TextSection;
import com.yuntongzhang.jlitec.ir3.CData3;
import com.yuntongzhang.jlitec.ir3.CMtd3;
import com.yuntongzhang.jlitec.ir3.Program3;
import com.yuntongzhang.jlitec.ir3.VarDecl3;

/**
 * Transforms Program3 to ProgramAsm
 */
public class CodeGen {
    private List<CData3> dataList;
    private List<CMtd3> methodList;
    // maps class name to another map,
    // which maps field names to offsets in this class struct
    private Map<String, Map<String, Integer>> classMap;
    // maps class name to another map,
    // which maps field names to the field type
    private Map<String, Map<String, String>> classTypeMap;

    private DataSection dataSection = new DataSection();
    private TextSection textSection = new TextSection();

    public CodeGen(Program3 program3) {
        this.dataList = program3.getDataList();
        this.methodList = program3.getMethodList();
        this.classMap = constructClassMap(this.dataList);
        this.classTypeMap = constructClassTypeMap(this.dataList);
    }

    public ProgramArm gen() {
        // translate each method into MethodBlock
        // dataSection also populated along the way
        for (CMtd3 cMtd3 : methodList) {
            MethodGen methodGen = new MethodGen(cMtd3, dataSection, classMap, classTypeMap);
            MethodBlock methodBlock = methodGen.gen();
            textSection.addMethod(methodBlock);
        }
        return new ProgramArm(dataSection, textSection);
    }

    // populate the `classMap` field
    private static Map<String, Map<String, Integer>> constructClassMap(List<CData3> datalist) {
        Map<String, Map<String, Integer>> outerMap = new HashMap<>();
        for (CData3 cData3 : datalist) {
            String cname = cData3.getCname();
            Map<String, Integer> innerMap = new HashMap<>();
            outerMap.put(cname, innerMap);
            List<VarDecl3> varDecl3List = cData3.getVarDecl3List();
            int offset = 0;
            for (VarDecl3 varDecl3 : varDecl3List) {
                innerMap.put(varDecl3.getId().toString(), offset);
                offset += 4;
            }
        }
        return outerMap;
    }

    // populate the `classTypeMap` field
    private static Map<String, Map<String, String>> constructClassTypeMap(List<CData3> dataList) {
        Map<String, Map<String, String>> outerMap = new HashMap<>();
        for (CData3 cData3 : dataList) {
            String cname = cData3.getCname();
            Map<String, String> innerMap = new HashMap<>();
            outerMap.put(cname, innerMap);
            List<VarDecl3> varDecl3List = cData3.getVarDecl3List();
            for (VarDecl3 varDecl3 : varDecl3List) {
                innerMap.put(varDecl3.getId().toString(), varDecl3.getTypeInString());
            }
        }
        return outerMap;
    }
}
