package fr.ufrst.m1info.comp5.type_checker;

import java.util.LinkedList;

public class visitData {
    private LinkedList<String> scopeStack;
    private CheckMode check_mode;
    public visitData(String initialScope, CheckMode check_mode) {
        this.scopeStack = new LinkedList<>();
        this.scopeStack.add(initialScope); // Start with the initial scope
        this.check_mode = check_mode;
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

    public CheckMode getCheckMode() {
        return check_mode;
    }

    public void setCheckMode(CheckMode mode) {
        this.check_mode = mode;
    }
}
