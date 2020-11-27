package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.yuntongzhang.jlitec.ast.Identifier;

public class Id3 extends Idc3 {
    private static int counter = 0;
    private String name;

    // for "this"
    public Id3() {
        this.name = "this";
    }

    // for ast Identifier
    public Id3(Identifier id) {
        this.name = id.getName();
    }

    // for temporary
    // OR for directly construct from a String
    public Id3(String name) {
        this.name = name;
    }

    // for temporary
    public static Id3 genNewTemp() {
        String name = "_t" + counter;
        counter++;
        return new Id3(name);
    }

    @Override
    public Set<Id3> getAllVariables() {
        Set<Id3> result = new HashSet<>();
        result.add(this);
        return result;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Id3 id3 = (Id3) o;
        return name.equals(id3.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
