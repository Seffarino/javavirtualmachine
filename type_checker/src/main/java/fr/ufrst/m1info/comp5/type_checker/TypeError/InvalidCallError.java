package fr.ufrst.m1info.comp5.type_checker.TypeError;

public class InvalidCallError extends TypeCheckerError {
    @Override
    public String toString() {
        return "Error in calling function '"+msg+"' at line: " + lineNumber + ", column: " + columnNumber;
    }
    public InvalidCallError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }
    public InvalidCallError(int lineNumber,int columnNumber,String msg){
        super(lineNumber,columnNumber,msg);
    }

}
