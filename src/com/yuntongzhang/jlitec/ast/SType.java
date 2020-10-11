package com.yuntongzhang.jlitec.ast;

/**
 * Represent non-function types
 * Primitive + Class types
 */
public class SType extends Type {
    private String name;

    public SType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isNullable() {
        return (!name.equals("Int") && !name.equals("Bool") && !name.equals("Void"));
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SType)) {
            return false;
        }
        SType other = (SType) obj;
        return this.name.equals(other.name);
    }
}
