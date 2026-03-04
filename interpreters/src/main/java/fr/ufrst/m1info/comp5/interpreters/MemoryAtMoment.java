package fr.ufrst.m1info.comp5.interpreters;

public class MemoryAtMoment {

    String symbolTable;
    String tas;

    public MemoryAtMoment(String s, String t) {
        symbolTable = s;
        tas = t;
    }

    public String getSymbolTable() {
        return symbolTable;
    }

    public String getTas() {
        return tas;
    }
}
