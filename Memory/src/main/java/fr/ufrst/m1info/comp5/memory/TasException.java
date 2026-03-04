package fr.ufrst.m1info.comp5.memory;

public class TasException extends Exception {
    public TasException(String errorMessage){
        super("The following error occured : " + errorMessage);
    }
}
