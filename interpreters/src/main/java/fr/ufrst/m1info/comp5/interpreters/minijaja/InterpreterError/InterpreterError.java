package fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError;

public abstract class InterpreterError {
    int lineNumber;
    int columnNumber;
    String msg;


    @Override
    public abstract String toString();


    public InterpreterError(int lineNumber,int columnNumber){

        this.lineNumber= lineNumber;
        this.columnNumber=columnNumber;
    }

    public InterpreterError(int lineNumber,int columnNumber,String msg){

        this.lineNumber= lineNumber;
        this.columnNumber=columnNumber;
        this.msg=msg;
    }
}
