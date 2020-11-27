package com.yuntongzhang.jlitec;

import java.io.IOException;

import com.yuntongzhang.jlitec.asm.ProgramArm;
import com.yuntongzhang.jlitec.ast.*;
import com.yuntongzhang.jlitec.check.DistinctNameChecker;
import com.yuntongzhang.jlitec.check.TypeChecker;
import com.yuntongzhang.jlitec.codegen.CodeGen;
import com.yuntongzhang.jlitec.codegen.IrOptimizer;
import com.yuntongzhang.jlitec.exceptions.SemanticError;
import com.yuntongzhang.jlitec.ir3.Ir3Gen;
import com.yuntongzhang.jlitec.ir3.Program3;

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
        boolean optimize = false;
        int filenameIndex = 0;

        try {
            if (argv[0].equals("-O")) {
                optimize = true;
                filenameIndex = 1;
            }
            scanner = new Lexer(new java.io.FileReader(argv[filenameIndex]), csf);
        } catch (java.io.FileNotFoundException e) {
            System.err.println("File not found : \"" + argv[filenameIndex] + "\"");
            System.exit(1);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Usage : java Main [-O] <input-file>");
            System.exit(1);
        }

        try {
            Parser p = new Parser(scanner, csf);
            Program result = (Program) p.parse().value;
            new DistinctNameChecker(result).check();
            new TypeChecker(result).check();

            Program3 program3 = new Ir3Gen(result).gen();

            if (optimize) {
                IrOptimizer irOptimizer = new IrOptimizer(program3);
                irOptimizer.run();
            }

            ProgramArm programArm = new CodeGen(program3).gen();
            programArm.prettyPrint();
        } catch (IOException e) {
            System.err.println("An I/O error occurred while parsing : \n" + e);
            System.exit(1);
        } catch (SemanticError e) {
            System.err.println("Semantic error in program : \n" + e);
            System.exit(1);
        }
        catch (Exception e) {
            System.err.println("Error happened when processing the input program : \n" + e);
            System.exit(1);
        }
    }
}
