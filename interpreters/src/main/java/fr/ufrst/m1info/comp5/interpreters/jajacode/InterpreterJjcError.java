package fr.ufrst.m1info.comp5.interpreters.jajacode;

import fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError.InterpreterError;

public class InterpreterJjcError extends InterpreterError {
    String msg;
    int lineNumber;
    int columnNumber;
    public InterpreterJjcError(int lineNumber,int columnNumber,String msg){
        super(lineNumber,columnNumber,msg);
        this.msg=msg;
        this.lineNumber=lineNumber;
        this.columnNumber=columnNumber;
    }
    @Override
    public String toString() {
        return msg+" at line: " + lineNumber + ", column: " + columnNumber;
    }


}

