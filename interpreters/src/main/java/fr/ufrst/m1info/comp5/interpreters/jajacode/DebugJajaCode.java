package fr.ufrst.m1info.comp5.interpreters.jajacode;

import fr.ufrst.m1info.comp5.compiler.CompilerPrinter;
import fr.ufrst.m1info.comp5.interpreters.MemoryAtMoment;
import fr.ufrst.m1info.comp5.interpreters.jajacode.InterpreterJjcVisitor;
import fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError.InterpreterError;
import fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterVisitor;
import fr.ufrst.m1info.comp5.jajacode.ASTJJCStart;
import fr.ufrst.m1info.comp5.jajacode.JajaCodeAnalyser;
import fr.ufrst.m1info.comp5.memory.Memory;
import fr.ufrst.m1info.comp5.memory.SymbolTable;
import fr.ufrst.m1info.comp5.memory.TasException;
import fr.ufrst.m1info.comp5.minijaja.ASTMjjStart;
import fr.ufrst.m1info.comp5.minijaja.MiniJajaAnalyser;
import fr.ufrst.m1info.comp5.type_checker.TypeCheckerVisitor;
import fr.ufrst.m1info.comp5.type_checker.TypeError.TypeCheckerError;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class DebugJajaCode extends Thread {

    private String JajaCode;

    private List<InterpreterError> errors;
    private SymbolTable symbolTable;
    private List<TypeCheckerError> errorsT;
    private List<InterpreterError> errorsI;

    InterpreterJjcVisitor a;
    private LinkedList<String> jjcStack;

    private Memory memory;
    private ArrayList<Integer> breakpoint = null;
    private boolean jumpToNextBreakpoint;
    public void setBreakpoint(ArrayList<Integer> breakpoint) {
        a.setBreakpoint(breakpoint);
    }
    private boolean paused = false;
    private int line ;
    public void setJumpBreakpoint(boolean b){
            a.setJumpBreakpoint(b);
    }
    public boolean getJumpBreakpoint(){
        return jumpToNextBreakpoint;
    }
    public DebugJajaCode() {
        symbolTable = new SymbolTable();
        errors = new ArrayList<InterpreterError>();
        errorsT = new ArrayList<>();
        errorsI = new ArrayList<>();
        try {
            memory = new Memory();
        } catch (TasException e) {
            throw new RuntimeException(e);
        }

        jjcStack = new LinkedList<>();

        a = new InterpreterJjcVisitor(memory, errors, true,breakpoint);
    }

    public void init() {
        a = new InterpreterJjcVisitor(memory, errors, true, breakpoint);
    }

    public void setInput(String str) {
        JajaCode = str;
    }
    public void setLine(int line) { this.line = line;}
    public int getLine(){ return a.getLine(); }
    public void pauseThread() {
        synchronized (a) {
            paused = true;
        }
    }

    public MemoryAtMoment resumeThread() {
        synchronized (a) {
            paused = false;
            MemoryAtMoment m = new MemoryAtMoment(memory.getSymbolTable().toString(), memory.getTas().toString());
            a.notify();
            return m;
        }
    }
    public void run() {
        synchronized (a) {
            try {

                ASTJJCStart astJJc = JajaCodeAnalyser.parseFromString(JajaCode);

                astJJc.jjtAccept(a, null);
                if (!errors.isEmpty()) {
                    throw new RuntimeException("ErrorJJC");

                }
            } catch (Exception exception) {
                System.out.println("Wait failed");
            }
        }
    }
}
