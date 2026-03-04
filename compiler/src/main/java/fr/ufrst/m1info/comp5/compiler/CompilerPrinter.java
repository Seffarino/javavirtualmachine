package fr.ufrst.m1info.comp5.compiler;

import java.util.LinkedList;

public class CompilerPrinter {
    private LinkedList<String> jjcStack;
    public CompilerPrinter(LinkedList<String>jjcStack){
        this.jjcStack=jjcStack;
    }
    public String printJjcStack() {
        StringBuilder res = new StringBuilder();
        for (int i = 1; i < jjcStack.size(); i++) {
            String element = jjcStack.get(i);
            res.append(i).append(" ").append(element);
            if (i < jjcStack.size() - 1) {
                res.append("\n");
            }
        }
        return res.toString();
    }
}
