package com.yuntongzhang.jlitec.check;

import com.yuntongzhang.jlitec.ast.Program;
import com.yuntongzhang.jlitec.exceptions.NonDistinctNameError;

public class DistinctNameChecker {
    Program program;

    public DistinctNameChecker(Program program) {
        this.program = program;
    }

    public void check() throws NonDistinctNameError {
        this.program.distinctNameCheck();
    }
}
