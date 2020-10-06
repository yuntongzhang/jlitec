package com.yuntongzhang.jlitec.ast;

import java.util.List;

/**
 * Implemented by constructs which spam multiple lines (tricky to just use toString).
 * These typically include those with "{}" in them.
 * Stmts are treated as PrettyPrintable for convenience in dealing with list of them.
 **/
public interface PrettyPrintable {
    void prettyPrint(int indentation);

    default void printList(List<?> lst, String delimiter, int indentation) {

    }
}
