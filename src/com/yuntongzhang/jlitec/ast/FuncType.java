package com.yuntongzhang.jlitec.ast;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FuncType extends Type {
    // need to preserve the order of parameters declared
    private List<SType> paraTypes;
    private SType returnType;

    public FuncType(List<FmlItem> fmlList, SType returnType) {
        List<SType> paraTypes = fmlList.stream().map(f -> f.getType()).collect(Collectors.toList());
        this.paraTypes = paraTypes;
        this.returnType = returnType;
    }

    public List<SType> getParaTypes() {
        return paraTypes;
    }

    public SType getReturnType() {
        return returnType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof FuncType)) {
            return false;
        }
        FuncType other = (FuncType) obj;
        if (this.paraTypes.size() != other.paraTypes.size()) {
            return false;
        }
        boolean result = this.returnType.equals(other.returnType);
        for (int i = 0; i < this.paraTypes.size(); i++) {
            result = result && (this.paraTypes.get(i).equals(other.paraTypes.get(i)));
        }
        return result;
    }
}
