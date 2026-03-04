module type_checker {
    requires Memory;
    requires lexer.parser;

    exports fr.ufrst.m1info.comp5.type_checker;
    exports fr.ufrst.m1info.comp5.type_checker.TypeError;
}