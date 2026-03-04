package fr.ufrst.m1info.comp5.type_checker.TypeError;

public abstract class TypeCheckerError {
    int lineNumber;
    int columnNumber;
    String msg;


    @Override
    public abstract String toString();


    public TypeCheckerError(int lineNumber,int columnNumber){

        this.lineNumber= lineNumber;
        this.columnNumber=columnNumber;
    }

    public TypeCheckerError(int lineNumber,int columnNumber,String msg){

        this.lineNumber= lineNumber;
        this.columnNumber=columnNumber;
        this.msg=msg;
    }


}
