package com.yuntongzhang.jlitec.ast;

/**
 * Implemented by constructs which spam multiple lines (tricky to just use toString).
 * These typically include those with "{}" in them.
 * Stmts are treated as PrettyPrintable for convenience in dealing with list of them.
 **/
public interface PrettyPrintable {
    void prettyPrint(int indentation);
}
