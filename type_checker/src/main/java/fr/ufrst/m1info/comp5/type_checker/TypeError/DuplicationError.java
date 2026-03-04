package fr.ufrst.m1info.comp5.type_checker.TypeError;

public class DuplicationError extends TypeCheckerError {
    @Override
    public String toString() {
        return "Duplicated symbol '"+ msg +"' at line : " + lineNumber + " column : " + columnNumber +".";

    }


    public DuplicationError(int lineNumber,int columnNumber){
        super(lineNumber,columnNumber);
    }
    public DuplicationError(int lineNumber,int columnNumber,String msg){
        super(lineNumber,columnNumber,msg);
    }

}
