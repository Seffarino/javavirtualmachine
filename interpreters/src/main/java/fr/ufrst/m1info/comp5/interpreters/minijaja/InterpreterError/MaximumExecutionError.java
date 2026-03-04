package fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError;

public class MaximumExecutionError extends InterpreterError {
    @Override
    public String toString() {
        return msg+" at line: " + lineNumber + ", column: " + columnNumber;
    }
    public MaximumExecutionError(int lineNumber,int columnNumber,String msg){
        super(lineNumber,columnNumber,msg);
    }

}
