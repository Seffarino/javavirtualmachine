module interpreters {
    exports fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError;
    exports fr.ufrst.m1info.comp5.interpreters.minijaja;
    exports fr.ufrst.m1info.comp5.interpreters.jajacode;
    exports fr.ufrst.m1info.comp5.interpreters;

    requires Memory;
    requires lexer.parser;
    requires type_checker;
    requires compiler;
}