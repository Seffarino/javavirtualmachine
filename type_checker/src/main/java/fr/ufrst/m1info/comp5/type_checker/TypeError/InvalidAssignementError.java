package fr.ufrst.m1info.comp5.type_checker.TypeError;

public class InvalidAssignementError extends TypeCheckerError {

    public InvalidAssignementError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }
    public InvalidAssignementError(int lineNumber,int columnNumber,String msg){
        super(lineNumber,columnNumber,msg);
    }
    @Override
    public String toString() {
        return "Cannot assign " +msg+ " at line: " + lineNumber + ", column: " + columnNumber;}

}