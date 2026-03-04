package fr.ufrst.m1info.comp5.type_checker.TypeError;

public class VoidTypeError extends TypeCheckerError {
    @Override
    public String toString() {
        return "A variable can not be a Void type at line :" + lineNumber + ", column: " + columnNumber;

    }

    public VoidTypeError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }
}
