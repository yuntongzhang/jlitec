package com.yuntongzhang.jlitec.analysis;

import java.util.ArrayList;
import java.util.List;

import com.yuntongzhang.jlitec.ir3.Stmt3;

public class BasicBlock {
    private List<Stmt3> insList;
    private List<BasicBlock> successors;
    private List<BasicBlock> predecessors;

    public BasicBlock() {
        this.insList = new ArrayList<>();
        this.successors = new ArrayList<>();
        this.predecessors = new ArrayList<>();
    }

    public void appendIns(Stmt3 ins) {
        this.insList.add(ins);
    }

    public void insertInsAfter(Stmt3 toInsert, Stmt3 insertionPoint) {
        int index = insList.indexOf(insertionPoint);
        if (index == -1) return;
        insList.add(index + 1, toInsert);
    }

    public void insertInsBefore(Stmt3 toInsert, Stmt3 insertionPoint) {
        int index = insList.indexOf(insertionPoint);
        if (index == -1) return;
        insList.add(index, toInsert);
    }

    // add edge in both directions to facilitate data-flow analysis later on
    public void addEdgeTo(BasicBlock next) {
        this.successors.add(next);
        next.predecessors.add(this);
    }

    public Stmt3 getLastIns() {
        if (insList.isEmpty()) {
            return null;
        }
        return insList.get(insList.size() - 1);
    }

    public Stmt3 getFirstIns() {
        if (insList.isEmpty()) {
            return null;
        }
        return insList.get(0);
    }

    public List<Stmt3> getInsList() {
        return insList;
    }

    public List<BasicBlock> getSuccessors() {
        return successors;
    }

    public List<BasicBlock> getPredecessors() {
        return predecessors;
    }

    public boolean containsIns(Stmt3 ins) {
        return insList.contains(ins);
    }

    // get the ins before this one in the basic block
    // pre-condition: the argument should not be the first ins in this bb
    public Stmt3 getPreviousIns(Stmt3 ins) {
        int currentIndex = insList.indexOf(ins);
        return insList.get(currentIndex - 1);
    }

    public Stmt3 getNextIns(Stmt3 ins) {
        int currentIndex = insList.indexOf(ins);
        return insList.get(currentIndex + 1);
    }
}
