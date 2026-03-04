package fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError;

public class ArithmeticError extends InterpreterError {
    @Override
    public String toString() {
        return msg+" at line: " + lineNumber + ", column: " + columnNumber;
    }
    public ArithmeticError(int lineNumber,int columnNumber,String msg){
        super(lineNumber,columnNumber,msg);
    }

}
