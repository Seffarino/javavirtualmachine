package fr.ufrst.m1info.comp5.type_checker.TypeError;

public class TypeMismatchError extends TypeCheckerError{

    @Override
    public String toString() {
        return "TypeMismatchError :"+msg+" at line: " + lineNumber + ", column: " + columnNumber;
    }
    public TypeMismatchError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }
    public TypeMismatchError(int lineNumber,int columnNumber,String msg){
        super(lineNumber,columnNumber,msg);
    }

}
