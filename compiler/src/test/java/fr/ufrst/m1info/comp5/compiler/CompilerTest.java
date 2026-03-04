package fr.ufrst.m1info.comp5.compiler;
import fr.ufrst.m1info.comp5.lexer_parser.minijaja.utils.*;
import fr.ufrst.m1info.comp5.minijaja.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
public class CompilerTest {

    private LinkedList<String> jjcStack;

    @Before
    public void setUp() {
        jjcStack = new LinkedList<String>();
    }


    private void miniJajaParserTestValid(String fileName) {
        try {
            ASTMjjStart ast = MiniJajaAnalyser.parseFromFile("src/test/CompilerTestFile/" + fileName + ".mjj");
            ast.jjtAccept(new ASTMjjPrinter(), null);
        } catch (FileNotFoundException e) {
            Assert.fail("File not found");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            Assert.fail("Should not throw ParseException");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void miniJajaCompiler(String fileName) throws IOException {
        setUp();
        try {
            ASTMjjStart ast = MiniJajaAnalyser.parseFromFile("src/test/CompilerTestFile/"+fileName+".mjj");
            ast.jjtAccept(new CompilerVisitor(jjcStack),null);
        }catch (Exception ignored){}
    }

    private void compareCompilerAndJjc(String fileName) throws IOException {
        CompilerPrinter printer = new CompilerPrinter(jjcStack);
        String compiler =  printer.printJjcStack();
        String jjc = Files.readString(Paths.get("src/test/CompilerTestFile/"+fileName+".jjc"));
        Assert.assertEquals(jjc,compiler);
    }

    @Test
    public void testSimpleClass() throws IOException {
        miniJajaParserTestValid("simpleClass");
        miniJajaCompiler("simpleClass");
        compareCompilerAndJjc("simpleClass");
    }

    @Test
    public void declVar() throws IOException {
        miniJajaParserTestValid("declVar");
        miniJajaCompiler("declVar");
        compareCompilerAndJjc("declVar");
    }

    @Test
    public void declCst() throws IOException {
        miniJajaParserTestValid("declCst");
        miniJajaCompiler("declCst");
        compareCompilerAndJjc("declCst");
    }

    @Test
    public void declTab() throws IOException {
        miniJajaParserTestValid("declTab");
        miniJajaCompiler("declTab");
        compareCompilerAndJjc("declTab");
    }

    @Test
    public void mainVarCstTab() throws IOException {
        miniJajaParserTestValid("mainVarCstTab");
        miniJajaCompiler("mainVarCstTab");
        compareCompilerAndJjc("mainVarCstTab");
    }

    @Test
    public void add() throws IOException {
        miniJajaParserTestValid("add");
        miniJajaCompiler("add");
        compareCompilerAndJjc("add");
    }

    @Test
    public void sub() throws IOException {
        miniJajaParserTestValid("sub");
        miniJajaCompiler("sub");
        compareCompilerAndJjc("sub");
    }

    @Test
    public void mul() throws IOException {
        miniJajaParserTestValid("mul");
        miniJajaCompiler("mul");
        compareCompilerAndJjc("mul");
    }

    @Test
    public void div() throws IOException {
        miniJajaParserTestValid("div");
        miniJajaCompiler("div");
        compareCompilerAndJjc("div");
    }

    @Test
    public void omega() throws IOException {
        miniJajaParserTestValid("omega");
        miniJajaCompiler("omega");
        compareCompilerAndJjc("omega");
    }
    @Test
    public void neg() throws IOException {
        miniJajaParserTestValid("neg");
        miniJajaCompiler("neg");
        compareCompilerAndJjc("neg");
    }

    @Test
    public void not() throws IOException {
        miniJajaParserTestValid("not");
        miniJajaCompiler("not");
        compareCompilerAndJjc("not");
    }

    @Test
    public void sup() throws IOException {
        miniJajaParserTestValid("sup");
        miniJajaCompiler("sup");
        compareCompilerAndJjc("sup");
    }

    @Test
    public void egal() throws IOException {
        miniJajaParserTestValid("egal");
        miniJajaCompiler("egal");
        compareCompilerAndJjc("egal");
    }

    @Test
    public void and() throws IOException {
        miniJajaParserTestValid("and");
        miniJajaCompiler("and");
        compareCompilerAndJjc("and");
    }

    @Test
    public void or() throws IOException {
        miniJajaParserTestValid("or");
        miniJajaCompiler("or");
        compareCompilerAndJjc("or");
    }

    @Test
    public void length() throws IOException {
        miniJajaParserTestValid("length");
        miniJajaCompiler("length");
        compareCompilerAndJjc("length");
    }


    @Test
    public void affectTab() throws IOException {
        miniJajaParserTestValid("affectTab");
        miniJajaCompiler("affectTab");
        compareCompilerAndJjc("affectTab");
    }

    @Test
    public void tab() throws IOException {
        miniJajaParserTestValid("tab");
        miniJajaCompiler("tab");
        compareCompilerAndJjc("tab");
    }
    @Test
    public void write() throws IOException {
        miniJajaParserTestValid("write");
        miniJajaCompiler("write");
        compareCompilerAndJjc("write");
    }
    @Test
    public void affectVar() throws IOException {
        miniJajaParserTestValid("affectVar");
        miniJajaCompiler("affectVar");
        compareCompilerAndJjc("affectVar");
    }

    @Test
    public void incVar() throws IOException {
        miniJajaParserTestValid("incVar");
        miniJajaCompiler("incVar");
        compareCompilerAndJjc("incVar");
    }

    @Test
    public void incTab() throws IOException {
        miniJajaParserTestValid("incTab");
        miniJajaCompiler("incTab");
        compareCompilerAndJjc("incTab");
    }

    @Test
    public void sumVar() throws IOException {
        miniJajaParserTestValid("sumVar");
        miniJajaCompiler("sumVar");
        compareCompilerAndJjc("sumVar");
    }

    @Test
    public void sumTab() throws IOException {
        miniJajaParserTestValid("sumTab");
        miniJajaCompiler("sumTab");
        compareCompilerAndJjc("sumTab");
    }

    @Test
    public void declMethVoidWithoutParamAndInstrs() throws IOException {
        miniJajaParserTestValid("declMethVoidWithoutParamAndInstrs");
        miniJajaCompiler("declMethVoidWithoutParamAndInstrs");
        compareCompilerAndJjc("declMethVoidWithoutParamAndInstrs");
    }

    @Test
    public void declMethVoidWithOneParamWithoutInstrs() throws IOException {
        miniJajaParserTestValid("declMethVoidWithOneParamWithoutInstrs");
        miniJajaCompiler("declMethVoidWithOneParamWithoutInstrs");
        compareCompilerAndJjc("declMethVoidWithOneParamWithoutInstrs");
    }

    @Test
    public void declMethVoid() throws IOException {
        miniJajaParserTestValid("declMethVoid");
        miniJajaCompiler("declMethVoid");
        compareCompilerAndJjc("declMethVoid");
    }

    @Test
    public void declMethVoidTwoParamWithoutInstrs() throws IOException {
        miniJajaParserTestValid("declMethVoidTwoParamWithoutInstrs");
        miniJajaCompiler("declMethVoidTwoParamWithoutInstrs");
        compareCompilerAndJjc("declMethVoidTwoParamWithoutInstrs");
    }

    @Test
    public void declMethInt() throws IOException {
        miniJajaParserTestValid("declMethInt");
        miniJajaCompiler("declMethInt");
        compareCompilerAndJjc("declMethInt");
    }

    @Test
    public void declMethBool() throws IOException {
        miniJajaParserTestValid("declMethBool");
        miniJajaCompiler("declMethBool");
        compareCompilerAndJjc("declMethBool");
    }

    @Test
    public void appelI() throws IOException {
        miniJajaParserTestValid("appelI");
        miniJajaCompiler("appelI");
        compareCompilerAndJjc("appelI");
    }

    @Test
    public void appelE() throws IOException {
        miniJajaParserTestValid("appelE");
        miniJajaCompiler("appelE");
        compareCompilerAndJjc("appelE");
    }

    @Test
    public void appelIGetListExpAll() throws IOException {
        miniJajaParserTestValid("appelIGetListExpAll");
        miniJajaCompiler("appelIGetListExpAll");
        compareCompilerAndJjc("appelIGetListExpAll");
    }

    @Test
    public void ifTest() throws IOException {
        miniJajaParserTestValid("if");
        miniJajaCompiler("if");
        compareCompilerAndJjc("if");
    }
    @Test
    public void ifWithElse() throws IOException {
        miniJajaParserTestValid("ifWithElse");
        miniJajaCompiler("ifWithElse");
        compareCompilerAndJjc("ifWithElse");
    }
    @Test
    public void whileTest() throws IOException {
        miniJajaParserTestValid("while");
        miniJajaCompiler("while");
        compareCompilerAndJjc("while");
    }

    @Test
    public void testTD() throws IOException {
        miniJajaParserTestValid("testTD");
        miniJajaCompiler("testTD");
        compareCompilerAndJjc("testTD");
    }

    @Test
    public void testRien() {
        ASTMjjrien nodeRien = new ASTMjjrien(0);
        Object res = nodeRien.jjtAccept(new CompilerVisitor(jjcStack),null);
        Assert.assertEquals(0,res);
    }
    @Test
    public void testEntier(){
        ASTMjjentier nodeEntier = new ASTMjjentier(0);
        Object res = nodeEntier.jjtAccept(new CompilerVisitor(jjcStack),null);
        Assert.assertEquals(0,res);
    }
    @Test
    public void testBool()  {
        ASTMjjbool nodeBool = new ASTMjjbool(0);
        Object res = nodeBool.jjtAccept(new CompilerVisitor(jjcStack),null);
        Assert.assertEquals(0,res);
    }

    @Test
    public void testSimpleNode()  {
        SimpleNode node = new SimpleNode(0);
        Object res = node.jjtAccept(new CompilerVisitor(jjcStack),null);
        Assert.assertNull(res);
    }

    @Test
    public void CompilerDataTest(){
        LinkedList<String> scopeStack = new LinkedList<>();

        CompilerData compilerData = new CompilerData(scopeStack, CompilerMode.DECL,0);
        Assert.assertNull(compilerData.getEnclosingScopeString());
        compilerData.exitScope();
        compilerData.enterScope("class");
        Assert.assertEquals("class",compilerData.getFullScopeString());
        compilerData.enterScope("main");
        Assert.assertEquals("class",compilerData.getEnclosingScopeString());

    }
}
