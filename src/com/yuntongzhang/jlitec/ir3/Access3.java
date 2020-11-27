package com.yuntongzhang.jlitec.ir3;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Access3 extends Exp3 {
    private Id3 lhs;
    private Id3 rhs;

    public Access3(Id3 lhs, Id3 rhs) {
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public Id3 getLhs() {
        return lhs;
    }

    public Id3 getRhs() {
        return rhs;
    }

    public void replaceVars(Map<Id3, Id3> replacementScheme) {
        if (replacementScheme.containsKey(lhs)) this.lhs = replacementScheme.get(lhs);
    }


    @Override
    public Set<Id3> getAllVariables() {
        Set<Id3> result = new HashSet<>();
        result.add(lhs);
        // Note: rhs is not added since it is not a variable
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Access3 access3 = (Access3) o;
        return lhs.equals(access3.lhs) &&
                rhs.equals(access3.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lhs, rhs);
    }

    @Override
    public String toString() {
        return lhs.toString() + "." + rhs.toString();
    }
}
