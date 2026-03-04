package fr.ufrst.m1info.comp5.memory;

public class SymbolTableException {
    public static class ExistingSymbolException extends Exception{
        public ExistingSymbolException(){
            super("The symbol is already in the table.");
        }
    }
    public static class UnknownSymbolException extends Exception{
        public UnknownSymbolException(){
            super("The symbol is not in the table");
        }
    }
    public static class StackOverflowException extends Exception{
        public StackOverflowException(){
            super("You have reached the end of the stack");
        }
    }
}
