package fr.ufrst.m1info.comp5.type_checker.TypeError;

public class InvalidReturnError extends TypeCheckerError {
    @Override
    public String toString() {
        return "InvalidReturnError :"+msg+" at line: " + lineNumber + ", column: " + columnNumber;
    }
    public InvalidReturnError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }
    public InvalidReturnError(int lineNumber,int columnNumber,String msg){
        super(lineNumber,columnNumber,msg);
    }

}

