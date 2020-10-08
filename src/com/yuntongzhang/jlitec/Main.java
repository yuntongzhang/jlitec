package com.yuntongzhang.jlitec;

import java.io.IOException;

import com.yuntongzhang.jlitec.ast.*;
import com.yuntongzhang.jlitec.check.DistinctNameChecker;
import com.yuntongzhang.jlitec.exceptions.NonDistinctNameError;

import java_cup.runtime.ComplexSymbolFactory;

public class Main {

    /**
     * Runs the parser on an input file.
     *
     * @param argv the command line, argv[0] is the filename to run the parser on.
     */
    public static void main(String argv[]) throws Exception {
        Lexer scanner = null;
        ComplexSymbolFactory csf = new ComplexSymbolFactory();
        try {
            scanner = new Lexer(new java.io.FileReader(argv[0]), csf);
        } catch (java.io.FileNotFoundException e) {
            System.err.println("File not found : \"" + argv[0] + "\"");
            System.exit(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Usage : java Main <input-file>");
            System.exit(1);
        }

        try {
            Parser p = new Parser(scanner, csf);
            Program result = (Program) p.parse().value;
            new DistinctNameChecker(result).check();
            result.prettyPrint(0);
        } catch (IOException e) {
            System.err.println("An I/O error occurred while parsing : \n" + e);
            System.exit(1);
        } catch (NonDistinctNameError e) {
            System.err.println("Semantic error in program : \n" + e);
            System.exit(1);
        }
    }
}
