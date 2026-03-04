package fr.ufrst.m1info.comp5.interpreters.jajacode;

import fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError.InterpreterError;
import fr.ufrst.m1info.comp5.jajacode.*;
import fr.ufrst.m1info.comp5.memory.Memory;
import fr.ufrst.m1info.comp5.lexer_parser.jajacode.utils.ASTJJCPrinter;
import fr.ufrst.m1info.comp5.memory.TasException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class InterpreterJjcTest {
    private Memory memory;
    private List<InterpreterError> errors;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    @Before
    public void setUp() throws TasException {
        memory=new Memory();
        errors = new ArrayList<>();
    }

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    public void testFromStrShouldWork(){
        String str = "1 push(0);2 jcstop;";
        try {
            ASTJJCStart ast = JajaCodeAnalyser.parseFromString(str);
            System.out.println(ast.jjtAccept(new ASTJJCPrinter(), null));
        } catch (Exception e) {
            Assert.fail("Should not throw AssertionError");
        }
    }

    public void jajaCodeParserTestValid(String fileName) {
        try {
            JajaCodeAnalyser.parseFromFile("src/test/interpreterJJCTestFile/"+fileName+".jjc");
        }catch (FileNotFoundException e){
            Assert.fail("File not found");
        }catch (Exception e) {
            Assert.fail("Should not throw AssertionError");
        }
    }
    public void jajaCodeInterpreter(String fileName) throws TasException {
        setUp();
        try {
            ASTJJCStart ast = JajaCodeAnalyser.parseFromFile("src/test/interpreterJJCTestFile/"+fileName+".jjc");
            ast.jjtAccept(new InterpreterJjcVisitor(memory, errors, false,null),null);
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            Assert.fail();
        }
    }

    public void functionalTest(String fileName) throws TasException {
        setUp();
        try {
            ASTJJCStart ast = JajaCodeAnalyser.parseFromFile("src/test/interpreterJJCTestFile/"+fileName+".jjc");
            ast.jjtAccept(new InterpreterJjcVisitor(memory, errors, false,null),null);
        }catch (Exception exception){
            System.out.println(exception.getMessage());
            Assert.fail();
        }
    }
    @Test
    public void classAddToStack(){
        ASTJJCStart nodeStart = new ASTJJCStart(0);
        ASTJJCinit nodeInit = new ASTJJCinit(1);
        nodeStart.jjtAddChild(nodeInit,0);
        nodeStart.jjtAccept(new InterpreterJjcVisitor(memory,errors, false,null),null);
        Assert.assertTrue(true);
    }

    @Test
    public void operatorTest() throws TasException {
        jajaCodeParserTestValid("op");
        jajaCodeInterpreter("op");
        Assert.assertEquals("3\n" +
                "1\n" +
                "2\n" +
                "4\n" +
                "false\n" +
                "true\n" +
                "-1\n" +
                "false\n" +
                "true\n" +
                "false\n",outContent.toString());
    }

    @Test
    public void Test() throws TasException {
        jajaCodeParserTestValid("test");
        jajaCodeInterpreter("test");
        Assert.assertEquals("2\n",outContent.toString());
    }

    @Test
    public void testAffect() throws TasException {
        jajaCodeParserTestValid("testAffect");
        jajaCodeInterpreter("testAffect");
        Assert.assertEquals("5\n",outContent.toString());

    }

    @Test
    public void declTableau() throws TasException {
        jajaCodeParserTestValid("declTableau");
        jajaCodeInterpreter("declTableau");
        Assert.assertEquals("5",outContent.toString());
    }

    @Test
    public void affectTab() throws TasException {
        jajaCodeParserTestValid("affectTab");
        jajaCodeInterpreter("affectTab");
        Assert.assertEquals("""
                0
                1
                2
                3
                4
                """,outContent.toString());
    }

    @Test
    public void sommeTab() throws TasException {
        jajaCodeParserTestValid("sommeTab");
        jajaCodeInterpreter("sommeTab");
        Assert.assertEquals("""
                2
                3
                4
                5
                6
                """,outContent.toString());
    }

    @Test
    public void incrementTab() throws TasException {
        jajaCodeParserTestValid("incrementTab");
        jajaCodeInterpreter("incrementTab");
        Assert.assertEquals("""
                1
                2
                3
                4
                5
                """,outContent.toString());
    }

    @Test
    public void affectTtoT() throws TasException {
        jajaCodeParserTestValid("affectTtoT");
        jajaCodeInterpreter("affectTtoT");
        Assert.assertEquals("""
                0
                1
                2
                3
                4
                """,outContent.toString());
    }

    @Test
    public void writeTab() throws TasException {
        jajaCodeParserTestValid("writeTab");
        jajaCodeInterpreter("writeTab");
        Assert.assertEquals("""
                1
                2
                """,outContent.toString());
    }

    @Test
    public void AppelECodeAfterReturn() throws TasException {
        jajaCodeParserTestValid("AppelECodeAfterReturn");
        jajaCodeInterpreter("AppelECodeAfterReturn");
        Assert.assertEquals("9",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());

    }

    @Test
    public void declVarInt() throws TasException {
        jajaCodeParserTestValid("declVarInt");
        jajaCodeInterpreter("declVarInt");
        Assert.assertEquals("""
                4
                -5
                455555
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());

    }

    @Test
    public void declVarBool() throws TasException {
        jajaCodeParserTestValid("declVarBool");
        jajaCodeInterpreter("declVarBool");
        Assert.assertEquals("""
                true
                false
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());


    }

    @Test
    public void declVarOpe() throws TasException {
        jajaCodeParserTestValid("declVarOpe");
        jajaCodeInterpreter("declVarOpe");
        Assert.assertEquals("""
                5
                -9
                24
                2
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());

    }

    @Test
    public void AssignementVcst() throws  TasException {
        jajaCodeParserTestValid("AssignementVcst");
        jajaCodeInterpreter("AssignementVcst");
        Assert.assertEquals("1",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());

    }

    @Test
    public void declVarOpeBool() throws TasException {
        jajaCodeParserTestValid("declVarOpeBool");
        jajaCodeInterpreter("declVarOpeBool");
        Assert.assertEquals("true\",\"false\",\"true\",\"false\",\"true\",\"true\",\"false\",\"true\",\"false\",\"false\",\"true\",\"false",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());

    }

    @Test
    public void declCstInt() throws TasException {
        jajaCodeParserTestValid("declCstInt");
        jajaCodeInterpreter("declCstInt");
        Assert.assertEquals("""
                4
                -5
                455555
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void declVarWithIdentInt() throws  TasException {
        jajaCodeParserTestValid("declVarWithIdentInt");
        jajaCodeInterpreter("declVarWithIdentInt");
        Assert.assertEquals("""
                4
                4
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void declVarWithIdentBool() throws TasException {
        jajaCodeParserTestValid("declVarWithIdentBool");
        jajaCodeInterpreter("declVarWithIdentBool");
        Assert.assertEquals("""
                true
                true
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());

    }

    @Test
    public void declVarOpeIdent() throws  TasException {
        jajaCodeParserTestValid("declVarOpeIdent");
        jajaCodeInterpreter("declVarOpeIdent");
        Assert.assertEquals("true\",\"false\",\"true\",\"false\",\"true\",\"true\",\"false\",\"true\",\"false\",\"false\",\"true\",\"false",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void mainVarsInt() throws TasException {
        jajaCodeParserTestValid("mainVarsInt");
        jajaCodeInterpreter("mainVarsInt");
        Assert.assertEquals("""
                4
                -5
                455555
                4
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }
    @Test
    public void declInClassAndMAin() throws TasException {
        jajaCodeParserTestValid("declInClassAndMAin");
        jajaCodeInterpreter("declInClassAndMAin");
        Assert.assertEquals("""
                1
                0
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void mainVarsBool() throws TasException {
        jajaCodeParserTestValid("mainVarsBool");
        jajaCodeInterpreter("mainVarsBool");
        Assert.assertEquals("""
                true
                true
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }
    @Test
    public void mainAffectationInt() throws  TasException {
        jajaCodeParserTestValid("mainAffectationInt");
        jajaCodeInterpreter("mainAffectationInt");
        Assert.assertEquals("""
                5
                6
                13
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void mainAffectionIntScope() throws TasException {
        jajaCodeParserTestValid("mainAffectionIntScope");
        jajaCodeInterpreter("mainAffectionIntScope");
        Assert.assertEquals("""
                5
                2
                25
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());

    }

    @Test
    public void mainIncrementIntScope() throws  TasException {
        jajaCodeParserTestValid("mainIncrementIntScope");
        jajaCodeInterpreter("mainIncrementIntScope");
        Assert.assertEquals("""
                4
                13
                24
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void mainSumScope() throws TasException {
        jajaCodeParserTestValid("mainSumScope");
        jajaCodeInterpreter("mainSumScope");
        Assert.assertEquals("""
                4
                16
                24
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void ifExpTrueWithoutElse() throws  TasException {
        jajaCodeParserTestValid("ifExpTrueWithoutElse");
        jajaCodeInterpreter("ifExpTrueWithoutElse");
        Assert.assertEquals("1",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void ifExpFalseWithoutElse() throws TasException {
        jajaCodeParserTestValid("ifExpFalseWithoutElse");
        jajaCodeInterpreter("ifExpFalseWithoutElse");
        Assert.assertEquals("0",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void ifWithElse() throws TasException {
        jajaCodeParserTestValid("ifWithElse");
        jajaCodeInterpreter("ifWithElse");
        Assert.assertEquals("2",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void whileSuccess() throws TasException {
        jajaCodeParserTestValid("whileSuccess");
        jajaCodeInterpreter("whileSuccess");
        Assert.assertEquals("5",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());

    }

    @Test
    public void methodeTest() throws TasException {
        jajaCodeParserTestValid("methodeTest");
        jajaCodeInterpreter("methodeTest");
        Assert.assertEquals("\"test\"",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());

    }

    @Test
    public void appelINoVar() throws TasException {
        jajaCodeParserTestValid("appelINoVar");
        jajaCodeInterpreter("appelINoVar");
        Assert.assertEquals("""
                10
                false
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());

    }

    @Test
    public void appelIDecl() throws TasException {
        jajaCodeParserTestValid("appelIDecl");
        jajaCodeInterpreter("appelIDecl");
        Assert.assertEquals("""
                10
                false
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());

    }

    @Test
    public void appelIDeclInClass() throws TasException {
        jajaCodeParserTestValid("appelIDeclInClass");
        jajaCodeInterpreter("appelIDeclInClass");
        Assert.assertEquals("""
                10
                false
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void appelIwithVarsAndInstrs() throws TasException {
        jajaCodeParserTestValid("appelIwithVarsAndInstrs");
        jajaCodeInterpreter("appelIwithVarsAndInstrs");
        Assert.assertEquals("""
                10
                false
                11
                true
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());

    }

    @Test
    public void appelIScope() throws  TasException {
        jajaCodeParserTestValid("appelIScope");
        jajaCodeInterpreter("appelIScope");
        Assert.assertEquals("5",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void AppelETest() throws  TasException {
        jajaCodeParserTestValid("AppelETest");
        jajaCodeInterpreter("AppelETest");
        Assert.assertEquals("9",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void AppelEReturnWithIf() throws  TasException {
        jajaCodeParserTestValid("AppelEReturnWithIf");
        jajaCodeInterpreter("AppelEReturnWithIf");
        Assert.assertEquals("4",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void sommeScope() throws TasException {
        jajaCodeParserTestValid("sommeScope");
        jajaCodeInterpreter("sommeScope");
        Assert.assertEquals("7",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void fact() throws  TasException {
        jajaCodeParserTestValid("fact");
        jajaCodeInterpreter("fact");
        Assert.assertEquals("5040",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void tabComplex() throws  TasException {
        jajaCodeParserTestValid("tabComplex");
        jajaCodeInterpreter("tabComplex");
        Assert.assertEquals("2",outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }
    @Test
    public void recur() throws TasException {
        jajaCodeParserTestValid("recur");
        jajaCodeInterpreter("recur");
        Assert.assertEquals("""
                1
                2
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void renommage() throws TasException {
        jajaCodeParserTestValid("renommage");
        jajaCodeInterpreter("renommage");
        Assert.assertEquals("""
                12
                12
                13
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void surcharge() throws TasException {
        jajaCodeParserTestValid("surcharge");
        jajaCodeInterpreter("surcharge");
        Assert.assertEquals("""
                1
                2
                1
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void triAbulle() throws TasException {
        jajaCodeParserTestValid("triAbulle");
        jajaCodeInterpreter("triAbulle");
        Assert.assertEquals("""
                3
                6
                8
                11
                12
                15
                15
                16
                17
                30
                """,outContent.toString());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void unitTestNop(){
        ASTJJCnop nodeNop = new ASTJJCnop(0);
        Object value = nodeNop.jjtAccept(new ASTJJCPrinter(), null);
        Assert.assertNull(value);
    }

    @Test
    public void unitTestIDent(){
        ASTJJCjcident node = new ASTJJCjcident(0);
        Object value = node.jjtAccept(new ASTJJCPrinter(), null);
        Assert.assertNull(value);
    }

    @Test
    public void unitTestString(){
        ASTJJCjcstring node = new ASTJJCjcstring(0);
        Object value = node.jjtAccept(new ASTJJCPrinter(), null);
        Assert.assertNull(value);
    }

    @Test
    public void unitTestJcVrai(){
        ASTJJCjcvrai node = new ASTJJCjcvrai(0);
        Object value = node.jjtAccept(new ASTJJCPrinter(), null);
        Assert.assertNull(value);
    }

    @Test
    public void unitTestJcFaux(){
        ASTJJCjcfaux node = new ASTJJCjcfaux(0);
        Object value = node.jjtAccept(new ASTJJCPrinter(), null);
        Assert.assertNull(value);
    }

    @Test
    public void unitTestJcAddress(){
        ASTJJCjcadress node = new ASTJJCjcadress(0);
        Object value = node.jjtAccept(new ASTJJCPrinter(), null);
        Assert.assertNull(value);
    }

    @Test
    public void unitTestNbr(){
        ASTJJCjcnbre node = new ASTJJCjcnbre(0);
        Object value = node.jjtAccept(new ASTJJCPrinter(), null);
        Assert.assertNull(value);
    }

    @Test
    public void testError(){
        InterpreterError error = new InterpreterJjcError(1,1,"test");
        Assert.assertEquals("test at line: 1, column: 1",error.toString());
    }

    @Test
    public void opeError() throws  TasException {
        jajaCodeParserTestValid("opeError");
        jajaCodeInterpreter("opeError");
        Assert.assertNotEquals(0,errors.size());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

    @Test
    public void opeBoolError() throws  TasException {
        jajaCodeParserTestValid("opeBoolError");
        jajaCodeInterpreter("opeBoolError");
        Assert.assertNotEquals(0,errors.size());
        Assert.assertNull(memory.getSymbolTable().getTopOfStack());
    }

}
