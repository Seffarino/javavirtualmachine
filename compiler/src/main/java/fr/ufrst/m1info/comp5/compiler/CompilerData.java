package fr.ufrst.m1info.comp5.compiler;

import java.util.LinkedList;

public class CompilerData {
    private CompilerMode compiler_mode;
    private int n;
    private int nbEntetes;
    private LinkedList<String> scopeStack;


    public CompilerData(LinkedList<String> scopeStack,CompilerMode compiler_mode,int n){
        this.scopeStack= scopeStack;
        this.compiler_mode=compiler_mode;
        this.n=n;
    }

    public CompilerData(LinkedList<String> scopeStack,CompilerMode compiler_mode,int n,int nbEntetes){
        this.scopeStack= scopeStack;
        this.compiler_mode=compiler_mode;
        this.n=n;
        this.nbEntetes=nbEntetes;
    }

    public int getN() {
        return n;
    }
    public CompilerMode getCompilerMode() {
        return compiler_mode;
    }

    public int getNbEntetes(){
        return nbEntetes;
    }


    public void enterScope(String newScope) {
        scopeStack.addLast(newScope);
    }
    public void exitScope() {
        if (!scopeStack.isEmpty()) {
            scopeStack.removeLast();
        }
    }
    public String getFullScopeString() {
        return String.join("@", scopeStack);
    }
    public LinkedList<String> getScopeStack() {
        return scopeStack;
    }

    public String getEnclosingScopeString() {
        if (scopeStack.size() <= 1) {
            return null;
        } else {
            return scopeStack.get(scopeStack.size() - 2);
        }
    }
}
