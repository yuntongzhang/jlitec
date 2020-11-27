package com.yuntongzhang.jlitec.ir3;

import com.yuntongzhang.jlitec.ast.FmlItem;

public class FmlItem3 {
    private Type3 type;
    private Id3 id;

    // id is the same as the ast id
    public FmlItem3(FmlItem astItem) {
        this.type = new Type3(astItem.getType());
        this.id = new Id3(astItem.getId());
    }

    // construct "this" as an FmlItem3
    public FmlItem3(String cname) {
        this.type = new Type3(cname);
        this.id = new Id3(); // "this"
    }

    public Type3 getType() {
        return type;
    }

    public Id3 getId() {
        return id;
    }

    @Override
    public String toString() {
        return type.toString() + " " + id.toString();
    }
}
