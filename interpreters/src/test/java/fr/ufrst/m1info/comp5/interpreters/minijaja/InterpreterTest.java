package fr.ufrst.m1info.comp5.interpreters.minijaja;

import fr.ufrst.m1info.comp5.interpreters.minijaja.InterpreterError.*;
import fr.ufrst.m1info.comp5.memory.Memory;
import fr.ufrst.m1info.comp5.memory.TasException;
import fr.ufrst.m1info.comp5.type_checker.TypeCheckerVisitor;
import fr.ufrst.m1info.comp5.type_checker.TypeError.TypeCheckerError;
import fr.ufrst.m1info.comp5.minijaja.*;
import fr.ufrst.m1info.comp5.lexer_parser.minijaja.utils.ASTMjjPrinter;
import fr.ufrst.m1info.comp5.memory.SymbolTable;
import fr.ufrst.m1info.comp5.memory.SymbolTableException;
import org.junit.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class InterpreterTest {
    private Memory memory;
    private SymbolTable s;
    private List<InterpreterError> errorsI;
    private List<TypeCheckerError> errorsT;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    @Before
    public void setUp() throws TasException {
        memory = new Memory();
        s = memory.getSymbolTable();
        errorsI = new ArrayList<>();
        errorsT = new ArrayList<>();
    }
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }
    @After
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    public void miniJajaParserTestValid(String fileName)  {
        try {
            ASTMjjStart ast = MiniJajaAnalyser.parseFromFile("src/test/interpreterTestFile/"+fileName+".mjj");
            ast.jjtAccept(new ASTMjjPrinter(),null);
        }catch (FileNotFoundException e){
            Assert.fail("File not found");
        }catch (ParseException e){
            System.out.println(e.getMessage());
            Assert.fail("Should not throw ParseException");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void miniJajaInterpreter(String fileName) throws TasException {
        setUp();
        try {
            ASTMjjStart ast = MiniJajaAnalyser.parseFromFile("src/test/interpreterTestFile/"+fileName+".mjj");
            ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.FIRST_PASS,memory, errorsT),null);
            ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.SECOND_PASS,memory, errorsT),null);
            ast.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public boolean containsAllErrorsWithSimpleNames(List<InterpreterError> errors, List<String> errorSimpleNames) {
        List<String> errorNamesPresent = errors.stream()
                .map(error -> extractSimpleClassName(error.getClass().getName()))
                .toList();
        return errorNamesPresent.containsAll(errorSimpleNames);
    }

    private String extractSimpleClassName(String fullClassName) {
        return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
    }

    @Test
    public void classAddToStack() throws  TasException {
        miniJajaParserTestValid("declClass");
        miniJajaInterpreter("declClass");
        Assert.assertNull(s.getTopOfStack());
    }


    @Test
    public void declVarInt() throws  TasException {
        miniJajaParserTestValid("declVarInt");
        miniJajaInterpreter("declVarInt");
        Assert.assertEquals("""
                4
                -5
                455555
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());


    }

    @Test
    public void declVarBool() throws TasException {
        miniJajaParserTestValid("declVarBool");
        miniJajaInterpreter("declVarBool");
        Assert.assertEquals("""
                true
                false
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void declVarOpe() throws TasException {
        miniJajaParserTestValid("declVarOpe");
        miniJajaInterpreter("declVarOpe");
        Assert.assertEquals("""
                5
                -9
                24
                2
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());



    }

    @Test
    public void declVarDivError() throws TasException {
        miniJajaParserTestValid("declVarDivError");
        miniJajaInterpreter("declVarDivError");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(1,errorsI.size());

        List<String> requiredErrorNames = List.of("ArithmeticError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errorsI, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void maxIntOpe() throws TasException {
        miniJajaParserTestValid("maxIntOpe");
        miniJajaInterpreter("maxIntOpe");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(6,errorsI.size());

        List<String> requiredErrorNames = List.of("ArithmeticError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errorsI, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void Reassignement() throws IOException, TasException {
        miniJajaParserTestValid("Reassignement");
        miniJajaInterpreter("Reassignement");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(2,errorsI.size());

        List<String> requiredErrorNames = List.of("ReAssignementError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errorsI, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void notInitializedVcst() throws IOException, TasException {
        miniJajaParserTestValid("notInitializedVcst");
        miniJajaInterpreter("notInitializedVcst");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(7,errorsI.size());

        List<String> requiredErrorNames = List.of("NotInitializedError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errorsI, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        Assert.assertNull(s.getTopOfStack());

    }


    @Test
    public void AssignementVcst() throws IOException, TasException {
        miniJajaParserTestValid("AssignementVcst");
        miniJajaInterpreter("AssignementVcst");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(0,errorsI.size());
        Assert.assertEquals("1",outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }
    @Test
    public void declVarNotInitialized() throws IOException, TasException {
        miniJajaParserTestValid("declVarNotInitialized");
        miniJajaInterpreter("declVarNotInitialized");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(1,errorsI.size());

        List<String> requiredErrorNames = List.of("NotInitializedError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errorsI, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void declVarOpeBool() throws IOException, TasException {
        miniJajaParserTestValid("declVarOpeBool");
        miniJajaInterpreter("declVarOpeBool");
        Assert.assertEquals("true\",\"false\",\"true\",\"false\",\"true\",\"true\",\"false\",\"true\",\"false\",\"false\",\"true\",\"false",outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void declCstInt() throws IOException, TasException {
        miniJajaParserTestValid("declCstInt");
        miniJajaInterpreter("declCstInt");
        Assert.assertEquals("""
                4
                -5
                455555
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void declVarWithIdentInt() throws  TasException {
        miniJajaParserTestValid("declVarWithIdentInt");
        miniJajaInterpreter("declVarWithIdentInt");
        Assert.assertEquals("""
                4
                4
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void declVarWithIdentBool() throws TasException {
        miniJajaParserTestValid("declVarWithIdentBool");
        miniJajaInterpreter("declVarWithIdentBool");
        Assert.assertEquals("""
                true
                true
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }
    @Test
    public void declVarOpeIdent() throws  TasException {
        miniJajaParserTestValid("declVarOpeIdent");
        miniJajaInterpreter("declVarOpeIdent");
        Assert.assertEquals("true\",\"false\",\"true\",\"false\",\"true\",\"true\",\"false\",\"true\",\"false\",\"false\",\"true\",\"false",outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }
    @Test
    public void mainVarsInt() throws TasException {
        miniJajaParserTestValid("mainVarsInt");
        miniJajaInterpreter("mainVarsInt");
        Assert.assertEquals("""
                4
                -5
                455555
                4
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void declInClassAndMAin() throws TasException {
        miniJajaParserTestValid("declInClassAndMAin");
        miniJajaInterpreter("declInClassAndMAin");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(0,errorsI.size());
        Assert.assertEquals("""
                1
                0
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void mainVarsBool() throws TasException {
        miniJajaParserTestValid("mainVarsBool");
        miniJajaInterpreter("mainVarsBool");
        Assert.assertEquals("""
                true
                true
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());


    }

    @Test
    public void mainAffectationInt() throws  TasException {
        miniJajaParserTestValid("mainAffectationInt");
        miniJajaInterpreter("mainAffectationInt");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(0,errorsI.size());
        Assert.assertEquals("""
                5
                6
                13
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void mainAffectionIntScope() throws TasException {
        miniJajaParserTestValid("mainAffectionIntScope");
        miniJajaInterpreter("mainAffectionIntScope");
        Assert.assertEquals("""
                5
                2
                25
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void mainIncrementIntScope() throws TasException {
        miniJajaParserTestValid("mainIncrementIntScope");
        miniJajaInterpreter("mainIncrementIntScope");
        Assert.assertEquals("""
                4
                13
                24
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void mainSumScope() throws TasException {
        miniJajaParserTestValid("mainSumScope");
        miniJajaInterpreter("mainSumScope");
        Assert.assertEquals("""
                4
                16
                24
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void ifExpTrueWithoutElse() throws  TasException {
        miniJajaParserTestValid("ifExpTrueWithoutElse");
        miniJajaInterpreter("ifExpTrueWithoutElse");
        Assert.assertEquals("1",outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void ifExpFalseWithoutElse() throws TasException {
       miniJajaParserTestValid("ifExpFalseWithoutElse");
        miniJajaInterpreter("ifExpFalseWithoutElse");
        Assert.assertEquals("0",outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void ifWithElse() throws TasException {
       miniJajaParserTestValid("ifWithElse");
        miniJajaInterpreter("ifWithElse");
        Assert.assertEquals("2",outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }

    @Ignore
    public void whileMaxExec() throws IOException, TasException {
        miniJajaParserTestValid("whileMaxExec");
        miniJajaInterpreter("whileMaxExec");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(1,errorsI.size());

        List<String> requiredErrorNames = List.of("MaximumExecutionError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errorsI, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void whileSuccess() throws TasException {
        miniJajaParserTestValid("whileSuccess");
        miniJajaInterpreter("whileSuccess");
        Assert.assertEquals("5",outContent.toString());

        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void methodeTest() throws TasException {
        miniJajaParserTestValid("methodeTest");
        miniJajaInterpreter("methodeTest");
        Assert.assertEquals("\"test\"",outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }
    @Test
    public void appelINoVar() throws TasException {
        miniJajaParserTestValid("appelINoVar");
        miniJajaInterpreter("appelINoVar");
        Assert.assertEquals("""
                10
                false
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void appelIDecl() throws TasException {
        miniJajaParserTestValid("appelIDecl");
        miniJajaInterpreter("appelIDecl");
        Assert.assertEquals("""
                10
                false
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void appelIDeclInClass() throws TasException {
        miniJajaParserTestValid("appelIDeclInClass");
        miniJajaInterpreter("appelIDeclInClass");
        Assert.assertEquals("""
                10
                false
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void appelIwithVarsAndInstrs() throws TasException {
        miniJajaParserTestValid("appelIwithVarsAndInstrs");
        miniJajaInterpreter("appelIwithVarsAndInstrs");
        Assert.assertEquals("""
                10
                false
                11
                true
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());


    }

    @Test
    public void appelIScope() throws  TasException {
        miniJajaParserTestValid("appelIScope");
        miniJajaInterpreter("appelIScope");
        Assert.assertEquals("5",outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void AppelETest() throws  TasException {
        miniJajaParserTestValid("AppelETest");
        miniJajaInterpreter("AppelETest");
        Assert.assertEquals("9",outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void AppelECodeAfterReturn() throws  TasException {
        miniJajaParserTestValid("AppelECodeAfterReturn");
        miniJajaInterpreter("AppelECodeAfterReturn");
        Assert.assertEquals("9",outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void AppelEReturnWithIf() throws  TasException {
        miniJajaParserTestValid("AppelEReturnWithIf");
        miniJajaInterpreter("AppelEReturnWithIf");
        Assert.assertEquals("4",outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void write() throws IOException, TasException {
        miniJajaParserTestValid("write");
        miniJajaInterpreter("write");
        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void writeln() throws IOException, TasException {
        miniJajaParserTestValid("writeln");
        miniJajaInterpreter("writeln");
        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void unitTestMult() {
        ASTMjjmult nodeMult = new ASTMjjmult(0);
        ASTMjjnbre nodeLeft = new ASTMjjnbre(1);
        ASTMjjnbre nodeRight = new ASTMjjnbre(2);
        nodeLeft.jjtSetValue(2);
        nodeRight.jjtSetValue(3);
        nodeMult.jjtAddChild(nodeLeft,0);
        nodeMult.jjtAddChild(nodeRight,1);
        int i = (int) nodeMult.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertEquals(6,i);
    }

    @Test
    public void unitTestSub() {
        ASTMjjsoustraction nodeSoustraction = new ASTMjjsoustraction(0);
        ASTMjjnbre nodeLeft = new ASTMjjnbre(1);
        ASTMjjnbre nodeRight = new ASTMjjnbre(2);
        nodeLeft.jjtSetValue(2);
        nodeRight.jjtSetValue(3);
        nodeSoustraction.jjtAddChild(nodeLeft,0);
        nodeSoustraction.jjtAddChild(nodeRight,1);
        int i = (int) nodeSoustraction.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertEquals(-1,i);
    }

    @Test
    public void unitTestAdd() {
        ASTMjjaddition nodeAddition = new ASTMjjaddition(0);
        ASTMjjnbre nodeLeft = new ASTMjjnbre(1);
        ASTMjjnbre nodeRight = new ASTMjjnbre(2);
        nodeLeft.jjtSetValue(2);
        nodeRight.jjtSetValue(3);
        nodeAddition.jjtAddChild(nodeLeft,0);
        nodeAddition.jjtAddChild(nodeRight,1);
        int i = (int) nodeAddition.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertEquals(5,i);
    }

    @Test
    public void unitTestDiv() {
        ASTMjjdiv nodeDiv = new ASTMjjdiv(0);
        ASTMjjnbre nodeLeft = new ASTMjjnbre(1);
        ASTMjjnbre nodeRight = new ASTMjjnbre(2);
        nodeLeft.jjtSetValue(5);
        nodeRight.jjtSetValue(2);
        nodeDiv.jjtAddChild(nodeLeft,0);
        nodeDiv.jjtAddChild(nodeRight,1);
        int i = (int) nodeDiv.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertEquals(2,i);
    }


    @Test
    public void unitTestVrai(){
        ASTMjjvrai nodeVrai= new ASTMjjvrai(0);
        boolean i = (boolean) nodeVrai.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertTrue(i);
    }

    @Test
    public void unitTestFaux(){
        ASTMjjfaux nodeFaux= new ASTMjjfaux(0);
        boolean i = (boolean) nodeFaux.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertFalse(i);
    }
    @Test
    public void unitTestRien(){
        ASTMjjrien nodeRien= new ASTMjjrien(0);
        Object i = nodeRien.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertNull(i);
    }

    @Test
    public void unitTestEntier(){
        ASTMjjentier nodeEntier =  new ASTMjjentier(0);
        String i = (String) nodeEntier.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertEquals("int",i);
    }

    @Test
    public void unitTestEntierNull(){
        ASTMjjentier nodeEntier =  new ASTMjjentier(0);
        Object i = nodeEntier.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),1);
        Assert.assertNull(i);
    }

    @Test
    public void unitTestBool(){
        ASTMjjbool nodeBoolean =  new ASTMjjbool(0);
        String i = (String) nodeBoolean.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertEquals("boolean",i);
    }

    @Test
    public void unitTestBoolNull(){
        ASTMjjbool nodeBoolean =  new ASTMjjbool(0);
        Object i = nodeBoolean.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),1);
        Assert.assertNull(i);
    }

    @Test
    public void unitTestEnil(){
        ASTMjjenil nodeEnil =  new ASTMjjenil(0);
        Object i = nodeEnil.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),1);
        Assert.assertNull(i);
    }

    @Test
    public void unitTestExnil(){
        ASTMjjexnil nodeExnil =  new ASTMjjexnil(0);
        Object i = nodeExnil.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),1);
        Assert.assertNull(i);
    }

    @Test
    public void unitTestSimpleNode(){
        SimpleNode node =  new SimpleNode(0);
        Object i = node.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),1);
        Assert.assertNull(i);
    }

    @Test
    public void unitTestMoins() {
        ASTMjjmoins nodeMoins = new ASTMjjmoins(0);
        ASTMjjnbre nodeNbre = new ASTMjjnbre(1);
        nodeNbre.jjtSetValue(5);
        nodeMoins.jjtAddChild(nodeNbre,0);
        int i = (int) nodeMoins.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertEquals(-5,i);
    }

    @Test
    public void unitTestNeg() {
        ASTMjjnon nodeNon= new ASTMjjnon(0);
        ASTMjjfaux nodeFaux = new ASTMjjfaux(1);
        nodeFaux.jjtSetValue(false);
        nodeNon.jjtAddChild(nodeFaux,0);
        boolean i = (boolean) nodeNon.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertTrue(i);
    }

    @Test
    public void unitTestSup() {
        ASTMjjsup nodeSup = new ASTMjjsup(0);
        ASTMjjnbre nodeLeft = new ASTMjjnbre(1);
        ASTMjjnbre nodeRight = new ASTMjjnbre(2);
        nodeLeft.jjtSetValue(5);
        nodeRight.jjtSetValue(2);
        nodeSup.jjtAddChild(nodeLeft,0);
        nodeSup.jjtAddChild(nodeRight,1);
        boolean i = (boolean) nodeSup.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertTrue(i);
    }



    @Test
    public void unitTestEgal() {
        ASTMjjegal nodeEgal = new ASTMjjegal(0);
        ASTMjjnbre nodeLeft = new ASTMjjnbre(1);
        ASTMjjnbre nodeRight = new ASTMjjnbre(2);
        nodeLeft.jjtSetValue(5);
        nodeRight.jjtSetValue(5);
        nodeEgal.jjtAddChild(nodeLeft,0);
        nodeEgal.jjtAddChild(nodeRight,1);
        boolean i = (boolean) nodeEgal.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertTrue(i);
    }

    @Test
    public void unitTestEt() {
        ASTMjjet nodeEt = new ASTMjjet(0);
        ASTMjjvrai nodeLeft = new ASTMjjvrai(1);
        ASTMjjvrai nodeRight = new ASTMjjvrai(2);
        nodeEt.jjtAddChild(nodeLeft,0);
        nodeEt.jjtAddChild(nodeRight,1);
        boolean i = (boolean) nodeEt.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertTrue(i);
    }

    @Test
    public void unitTestOu() {
        ASTMjjou nodeOu = new ASTMjjou(0);
        ASTMjjfaux nodeLeft = new ASTMjjfaux(1);
        ASTMjjvrai nodeRight = new ASTMjjvrai(2);
        nodeOu.jjtAddChild(nodeLeft,0);
        nodeOu.jjtAddChild(nodeRight,1);
        boolean i = (boolean) nodeOu.jjtAccept(new InterpreterVisitor(memory, errorsI,false, null),null);
        Assert.assertTrue(i);
    }

    @Test
    public void interpreterErrorTest(){
        InterpreterError errorArthmetique = new ArithmeticError(1,5,"test");
        Assert.assertEquals("test at line: 1, column: 5",errorArthmetique.toString());
        InterpreterError exception = new InterpreterException(1,5,"test");
        Assert.assertEquals("test at line: 1, column: 5",exception.toString());
        InterpreterError maxExec= new MaximumExecutionError(1,5,"test");
        Assert.assertEquals("test at line: 1, column: 5",maxExec.toString());
        InterpreterError initialisation= new NotInitializedError(1,5,"test");
        Assert.assertEquals("test at line: 1, column: 5",initialisation.toString());
        InterpreterError reassign= new ReAssignementError(1,5);
        Assert.assertEquals("Can not reassign already assigned constant at line :1, column: 5",reassign.toString());
    }

    @Test
    public void declTableau() throws TasException {
        miniJajaParserTestValid("declTableau");
        miniJajaInterpreter("declTableau");
        Assert.assertEquals("5",outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void affectTab() throws TasException {
        miniJajaParserTestValid("affectTab");
        miniJajaInterpreter("affectTab");
        Assert.assertEquals("""
                0
                1
                2
                3
                4
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void outOfBoundExp() throws TasException {
        miniJajaParserTestValid("outOfBoundExp");
        miniJajaInterpreter("outOfBoundExp");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(1,errorsI.size());

        List<String> requiredErrorNames = List.of("OutOfBoundError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errorsI, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void notInitializedTab() throws  TasException {
        miniJajaParserTestValid("notInitializedTab");
        miniJajaInterpreter("notInitializedTab");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(1,errorsI.size());

        List<String> requiredErrorNames = List.of("NotInitializedError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errorsI, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        Assert.assertNull(s.getTopOfStack());

    }
    @Test
    public void sommeNotInitalizedScope() throws  TasException {
        miniJajaParserTestValid("sommeNotInitalizedScope");
        miniJajaInterpreter("sommeNotInitalizedScope");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(1,errorsI.size());

        List<String> requiredErrorNames = List.of("NotInitializedError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errorsI, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void sommeScope() throws TasException {
        miniJajaParserTestValid("sommeScope");
        miniJajaInterpreter("sommeScope");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(0,errorsI.size());
        Assert.assertEquals("7",outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void sommeTab() throws TasException {
        miniJajaParserTestValid("sommeTab");
        miniJajaInterpreter("sommeTab");
        Assert.assertEquals("""
                2
                3
                4
                5
                6
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void incrementTab() throws TasException {
        miniJajaParserTestValid("incrementTab");
        miniJajaInterpreter("incrementTab");
        Assert.assertEquals("""
                1
                2
                3
                4
                5
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }
    @Test
    public void affectTtoT() throws TasException {
        miniJajaParserTestValid("affectTtoT");
        miniJajaInterpreter("affectTtoT");
        Assert.assertEquals("""
                0
                1
                2
                3
                4
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void affectOutOfBound() throws TasException {
        miniJajaParserTestValid("affectOutOfBound");
        miniJajaInterpreter("affectOutOfBound");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(3,errorsI.size());

        List<String> requiredErrorNames = List.of("OutOfBoundError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errorsI, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void incTabNotInitialized() throws TasException {
        miniJajaParserTestValid("incTabNotInitialized");
        miniJajaInterpreter("incTabNotInitialized");
        Assert.assertEquals(0,errorsT.size());
        Assert.assertEquals(2,errorsI.size());

        List<String> requiredErrorNames = List.of("NotInitializedError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errorsI, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        Assert.assertNull(s.getTopOfStack());
    }


    @Test
    public void fact() throws  TasException {
        miniJajaParserTestValid("fact");
        miniJajaInterpreter("fact");
        Assert.assertEquals("5040",outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void tabComplex() throws  TasException {
        miniJajaParserTestValid("tabComplex");
        miniJajaInterpreter("tabComplex");
        Assert.assertEquals("2",outContent.toString());
        Assert.assertNull(s.getTopOfStack());
    }

    @Test
    public void recur() throws TasException {
        miniJajaParserTestValid("recur");
        miniJajaInterpreter("recur");
        Assert.assertEquals("""
                1
                2
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void renommage() throws TasException {
        miniJajaParserTestValid("renommage");
        miniJajaInterpreter("renommage");
        Assert.assertEquals("""
                12
                12
                13
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void surcharge() throws TasException {
        miniJajaParserTestValid("surcharge");
        miniJajaInterpreter("surcharge");
        Assert.assertEquals("""
                1
                2
                1
                """,outContent.toString());
        Assert.assertNull(s.getTopOfStack());

    }

    @Test
    public void triAbulle() throws TasException {
        miniJajaParserTestValid("triAbulle");
        miniJajaInterpreter("triAbulle");
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
        Assert.assertNull(s.getTopOfStack());

    }
}
