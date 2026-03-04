package fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError;

public class ReAssignementError extends InterpreterError{
    @Override
    public String toString() {
        return "Can not reassign already assigned constant at line :" + lineNumber + ", column: " + columnNumber;
    }

    public ReAssignementError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }

}
