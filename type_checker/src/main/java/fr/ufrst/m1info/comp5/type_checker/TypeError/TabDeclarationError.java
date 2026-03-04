package fr.ufrst.m1info.comp5.type_checker.TypeError;

public class TabDeclarationError extends TypeCheckerError{
    @Override
    public String toString() {
        return "Table must be declared with an Integer at line :" + lineNumber + ", column: " + columnNumber;
    }

    public TabDeclarationError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }

}
