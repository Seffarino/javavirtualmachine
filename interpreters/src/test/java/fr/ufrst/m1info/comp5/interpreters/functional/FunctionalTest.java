package fr.ufrst.m1info.comp5.interpreters.functional;

import fr.ufrst.m1info.comp5.jajacode.ASTJJCStart;
import fr.ufrst.m1info.comp5.jajacode.JajaCodeAnalyser;
import fr.ufrst.m1info.comp5.lexer_parser.minijaja.utils.ASTMjjPrinter;
import fr.ufrst.m1info.comp5.memory.TasException;
import fr.ufrst.m1info.comp5.lexer_parser.jajacode.utils.*;
import fr.ufrst.m1info.comp5.minijaja.ASTMjjStart;
import fr.ufrst.m1info.comp5.minijaja.MiniJajaAnalyser;
import fr.ufrst.m1info.comp5.type_checker.TypeCheckerVisitor;
import fr.ufrst.m1info.comp5.type_checker.TypeError.TypeCheckerError;
import fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError.InterpreterError;
import fr.ufrst.m1info.comp5.interpreters.jajacode.InterpreterJjcVisitor;
import fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterVisitor;
import fr.ufrst.m1info.comp5.memory.Memory;
import fr.ufrst.m1info.comp5.memory.SymbolTable;
import fr.ufrst.m1info.comp5.compiler.CompilerPrinter;
import fr.ufrst.m1info.comp5.compiler.CompilerVisitor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FunctionalTest {
    private Memory memory;
    private List<InterpreterError> errors;
    private SymbolTable s;
    private List<InterpreterError> errorsI;
    private List<TypeCheckerError> errorsT;
    private LinkedList<String> jjcStack;
    @Before
    public void setUp() throws TasException {
        memory=new Memory();
        errors = new ArrayList<>();
        s = memory.getSymbolTable();
        errorsI = new ArrayList<>();
        errorsT = new ArrayList<>();
        jjcStack = new LinkedList<>();
    }

    public void functionalTest(String fileName) throws TasException {
        setUp();
        try {
            ASTMjjStart ast = MiniJajaAnalyser.parseFromFile("src/test/functionalTest/"+fileName+".mjj");
            ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.FIRST_PASS,memory, errorsT),null);
            ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.SECOND_PASS,memory, errorsT),null);
            if(!errorsT.isEmpty()){
                throw new RuntimeException("ErrorT");

            }
            System.out.println("mjj:");
            ast.jjtAccept(new InterpreterVisitor(memory, errorsI, false, null),null);
            if(!errorsI.isEmpty()){
                throw new RuntimeException("ErrorI");

            }
            ast.jjtAccept(new CompilerVisitor(jjcStack),null);
            CompilerPrinter printer = new CompilerPrinter(jjcStack);
            System.out.println(printer.printJjcStack());
            ASTJJCStart astJJc = JajaCodeAnalyser.parseFromString(printer.printJjcStack());
            System.out.println("jjc:");
            astJJc.jjtAccept(new InterpreterJjcVisitor(new Memory(), errors,false, null),null);
            if(!errors.isEmpty()){
                throw new RuntimeException("ErrorJJC");

            }
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            Assert.fail();
        }
    }

    @Test
    public void Test() throws TasException {
        functionalTest("test");
        Assert.assertTrue(true);
    }

    @Test
    public void Test2() throws TasException {
        functionalTest("test2");
        Assert.assertTrue(true);
    }

}
