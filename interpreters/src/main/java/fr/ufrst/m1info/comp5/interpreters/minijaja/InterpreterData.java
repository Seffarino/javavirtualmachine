package fr.ufrst.m1info.comp5.interpreters.minijaja;

import java.util.LinkedList;

public class InterpreterData {
    private LinkedList<String> scopeStack;
    private InterpreterMode interpreter_mode;
    public InterpreterData(String initialScope, InterpreterMode interpreter_mode) {
        this.scopeStack = new LinkedList<>();
        this.scopeStack.add(initialScope); // Start with the initial scope
        this.interpreter_mode = interpreter_mode;
    }

    public InterpreterData(InterpreterData other) {
        this.scopeStack = deepCopyScopeStack(other.getScopeStack());
        this.interpreter_mode = other.interpreter_mode; // Assuming this is immutable or a primitive
    }

    private LinkedList<String> deepCopyScopeStack(LinkedList<String> original) {
        LinkedList<String> copy = new LinkedList<>();
        for (String item : original) {
            copy.addLast(item);
        }
        return copy;
    }

    // Getters et setters
    // Enter a new scope by pushing it onto the stack
    public void enterScope(String newScope) {
        scopeStack.addLast(newScope);
    }

    // Exit the current scope by popping it from the stack
    public void exitScope() {
        if (!scopeStack.isEmpty()) {
            scopeStack.removeLast();
        }
    }

    // Get the current (innermost) scope
    public String getCurrentScope() {
        return scopeStack.peekLast();
    }
    // Get the full scope as a string, useful for debugging or error messages
    public String getFullScopeString() {
        return String.join("@", scopeStack);
    }

    // Getters and setters
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

    public InterpreterMode getInterpreterMode() {
        return interpreter_mode;
    }

    public void setInterpreterMode(InterpreterMode mode) {
        this.interpreter_mode = mode;
    }
}
