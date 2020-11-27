package com.yuntongzhang.jlitec.analysis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.yuntongzhang.jlitec.ir3.Goto3;
import com.yuntongzhang.jlitec.ir3.IfGoto3;
import com.yuntongzhang.jlitec.ir3.Label3;
import com.yuntongzhang.jlitec.ir3.Stmt3;

/**
 * A FlowGraph is for one single method.
 */
public class FlowGraph {
    private BasicBlock entry = new BasicBlock();
    private BasicBlock exit = new BasicBlock();
    private Set<BasicBlock> allBbs = new HashSet<>(); // all bbs except entry and exit
    private Set<Stmt3> allIns = new HashSet<>();

    public FlowGraph(List<Stmt3> stmts) {
        Set<Stmt3> leaders = populateLeaders(stmts);
        List<BasicBlock> bbs = constructBbs(stmts, leaders);
        this.allBbs.addAll(bbs);
        addEdgesBetweenBbs(bbs);
        this.allIns.addAll(stmts);
    }

    public Set<Stmt3> getAllIns() {
        return this.allIns;
    }

    public Set<BasicBlock> getAllBbs() {
        return allBbs;
    }

    public Set<Stmt3> getLeaders() {
        Set<Stmt3> result = new HashSet<>();
        for (BasicBlock bb : allBbs) {
            Stmt3 firstIns = bb.getFirstIns();
            if (firstIns != null) result.add(firstIns);
        }
        return result;
    }

    // a back leader is the last instruction in a basic block
    public Set<Stmt3> getBackLeaders() {
        Set<Stmt3> result = new HashSet<>();
        for (BasicBlock bb : allBbs) {
            Stmt3 lastIns = bb.getLastIns();
            if (lastIns != null) result.add(lastIns);
        }
        return result;
    }

    // pre-condition: ins should be in this graph
    public BasicBlock findBlockWithIns(Stmt3 ins) {
        for (BasicBlock bb : allBbs) {
            if (bb.containsIns(ins)) return bb;
        }
        return null;
    }

    // in our flow graph, entry and exit are imaginary and only connected to one block respectively
    public Stmt3 getFirstIns() {
        return entry.getSuccessors().get(0).getFirstIns();
    }
    public Stmt3 getLastIns() {
        return exit.getPredecessors().get(0).getLastIns();
    }

    private Set<Stmt3> populateLeaders(List<Stmt3> stmts) {
        Set<Stmt3> leaders = new HashSet<>();
        for (int i = 0; i < stmts.size(); i++) {
            Stmt3 stmt = stmts.get(i);
            if (i == 0) { // first ins in a procedure
                leaders.add(stmt);
            }
            if (stmt instanceof Label3) { // jump target
                leaders.add(stmt);
            }
            if (stmt instanceof IfGoto3 || stmt instanceof Goto3) { // ins following jump
                if (i < stmts.size() - 1) leaders.add(stmts.get(i+1));
            }
        }
        return leaders;
    }

    // return a list of basic blocks according to the original ir3 code sequence
    private List<BasicBlock> constructBbs(List<Stmt3> stmts, Set<Stmt3> leaders) {
        List<BasicBlock> result = new ArrayList<>();
        BasicBlock newBB = null;
        for (int i = 0; i < stmts.size(); i++) {
            Stmt3 stmt = stmts.get(i);
            if (leaders.contains(stmt)) { // encounter a leader
                newBB = new BasicBlock();
                result.add(newBB);
            }
            newBB.appendIns(stmt);
        }
        return result;
    }

    private void addEdgesBetweenBbs(List<BasicBlock> bbs) {
        int numOfBbs = bbs.size();
        if (numOfBbs < 1) {
            this.entry.addEdgeTo(this.exit);
            return;
        }
        // link entry and start
        this.entry.addEdgeTo(bbs.get(0));
        // link in between
        for (int i = 0; i < numOfBbs; i++) {
            BasicBlock currentBb = bbs.get(i);
            Stmt3 lastIns = currentBb.getLastIns();
            if (lastIns instanceof Goto3) {
                // only have an edge to the goto target
                Label3 targetLabel = ((Goto3) lastIns).getLabel();
                BasicBlock targetBb = findBbWithLeader(bbs, targetLabel);
                currentBb.addEdgeTo(targetBb);
                continue;
            }
            if (lastIns instanceof IfGoto3) {
                // have two edges: goto target + next bb
                // handle next bb in common case
                Label3 targetLabel = ((IfGoto3) lastIns).getLabel();
                BasicBlock targetBb = findBbWithLeader(bbs, targetLabel);
                currentBb.addEdgeTo(targetBb);
            }
            // common case
            if (i == numOfBbs - 1) { // last bb
                currentBb.addEdgeTo(this.exit);
            } else {
                BasicBlock nextBb = bbs.get(i+1);
                currentBb.addEdgeTo(nextBb);
            }
        }
    }

    // if program is correctly constructed, should never return null
    private BasicBlock findBbWithLeader(List<BasicBlock> bbs, Stmt3 leader) {
        for (BasicBlock bb : bbs) {
            if (bb.getFirstIns() == leader) return bb;
        }
        return null;
    }

}
