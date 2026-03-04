package fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError;

public class OutOfBoundError extends InterpreterError {
    @Override
    public String toString() {
        return "Out of bound at line: " + lineNumber + ", column: " + columnNumber;
    }
    public OutOfBoundError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }

}
