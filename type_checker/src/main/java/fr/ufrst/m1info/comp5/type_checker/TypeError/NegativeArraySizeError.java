package fr.ufrst.m1info.comp5.type_checker.TypeError;

public class NegativeArraySizeError extends TypeCheckerError{
    @Override
    public String toString() {
        return "Negative array size at line " + lineNumber + ", column: " + columnNumber;
    }

    public NegativeArraySizeError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }

}
