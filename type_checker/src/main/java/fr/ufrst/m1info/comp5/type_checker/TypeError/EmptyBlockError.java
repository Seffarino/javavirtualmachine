package fr.ufrst.m1info.comp5.type_checker.TypeError;

public class EmptyBlockError extends TypeCheckerError {
    @Override
    public String toString() {
        return "A block with no statements detected at line: " + lineNumber + ", column: " + columnNumber;}
    public EmptyBlockError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }
}
