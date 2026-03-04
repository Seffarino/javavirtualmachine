package fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError;

public class InterpreterException extends InterpreterError {
    @Override
    public String toString() {
        return msg+" at line: " + lineNumber + ", column: " + columnNumber;
    }
    public InterpreterException(int lineNumber,int columnNumber,String msg){
        super(lineNumber,columnNumber,msg);
    }

}
