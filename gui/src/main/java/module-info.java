module gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires Memory;
    requires type_checker;
    requires interpreters;
    requires compiler;
    requires lexer.parser;


    opens fr.ufrst.m1info.comp5.gui to javafx.fxml;
    exports fr.ufrst.m1info.comp5.gui;
}