package fr.ufrst.m1info.comp5.type_checker.TypeError;

public class MissingReturnError extends TypeCheckerError {
    @Override
    public String toString() {
        return "Missing return at line: " + lineNumber + ", column: " + columnNumber;
    }
    public MissingReturnError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }

}
