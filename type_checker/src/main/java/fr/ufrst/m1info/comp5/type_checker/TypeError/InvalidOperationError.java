package fr.ufrst.m1info.comp5.type_checker.TypeError;

public class InvalidOperationError extends TypeCheckerError {
    @Override
    public String toString() {
        return msg+" at line: " + lineNumber + ", column: " + columnNumber;
    }
    public InvalidOperationError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }
    public InvalidOperationError(int lineNumber,int columnNumber,String msg){
        super(lineNumber,columnNumber,msg);
    }

}
