package com.yuntongzhang.jlitec.ast;

/**
 * Represent an AST node.
 * The general fields and methods for nodes reside here.
 */
public abstract class Node {
    protected Location loc;

    public Node(Location loc) {
        this.loc = loc;
    }

    public Location getLoc() {
        return loc;
    }

    public static class Location {
        private int line;
        private int column;

        public Location(int line, int column) {
            this.line = line;
            this.column = column;
        }

        public int getLine() {
            return line;
        }

        public int getColumn() {
            return column;
        }
    }
}
