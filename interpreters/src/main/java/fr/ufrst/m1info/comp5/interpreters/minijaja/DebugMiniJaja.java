package fr.ufrst.m1info.comp5.interpreters.minijaja;

import fr.ufrst.m1info.comp5.compiler.CompilerPrinter;
import fr.ufrst.m1info.comp5.interpreters.MemoryAtMoment;
import fr.ufrst.m1info.comp5.interpreters.jajacode.InterpreterJjcVisitor;
import fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError.InterpreterError;
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

public class DebugMiniJaja extends Thread {

    private String miniJajaCode;

    private List<InterpreterError> errors;
    private Memory memory;
    private List<TypeCheckerError> errorsT;
    private List<InterpreterError> errorsI;

    InterpreterVisitor a;
    private LinkedList<String> jjcStack;

    private ArrayList<Integer> breakpoint = null;

    private boolean paused = false;


    public DebugMiniJaja() {
        try {
            memory = new Memory();
        } catch (TasException e) {
            throw new RuntimeException(e);
        }
        errors = new ArrayList<InterpreterError>();
        errorsT = new ArrayList<>();
        errorsI = new ArrayList<>();
        a = new InterpreterVisitor(memory, errors, true, breakpoint);
        jjcStack = new LinkedList<>();
    }

    public void init() {
        a = new InterpreterVisitor(memory, errors, true, breakpoint);
    }
    public int getLine() {
        return a.getLine();
    }
    public void setInput(String str) {
        miniJajaCode = str;
    }

    public void setBreakpoint(ArrayList<Integer> breakpoint) {
        a.setBreakpoint(breakpoint);
    }

    public void setJumpBreakpoint(boolean b) {
        a.setJumpBreakpoint(b);
    }
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

                ASTMjjStart ast = MiniJajaAnalyser.parseFromString(miniJajaCode);
                ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.FIRST_PASS, memory, errorsT), null);
                ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.SECOND_PASS, memory, errorsT), null);
                if(!errorsT.isEmpty()){
                    throw new RuntimeException("ErrorT");

                }
                ast.jjtAccept(a, null);

                CompilerPrinter printer = new CompilerPrinter(jjcStack);
                printer.printJjcStack(); //ici on a tout le jajacode

                ASTJJCStart astJJc = JajaCodeAnalyser.parseFromString(printer.printJjcStack());

                astJJc.jjtAccept(new InterpreterJjcVisitor(new Memory(), errors, false,null), null);
                if (!errors.isEmpty()) {
                    throw new RuntimeException("ErrorJJC");

                }
            } catch (Exception exception) {
                System.out.println("Wait failed");
            }
        }
    }


}
