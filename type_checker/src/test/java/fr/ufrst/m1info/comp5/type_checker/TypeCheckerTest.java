package fr.ufrst.m1info.comp5.type_checker;

import fr.ufrst.m1info.comp5.type_checker.TypeError.TypeCheckerError;
import fr.ufrst.m1info.comp5.memory.*;
import fr.ufrst.m1info.comp5.minijaja.ASTMjjStart;
import fr.ufrst.m1info.comp5.minijaja.MiniJajaAnalyser;
import fr.ufrst.m1info.comp5.minijaja.ParseException;
import fr.ufrst.m1info.comp5.minijaja.utils.ASTMjjPrinter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TypeCheckerTest {

    private SymbolTable s;
    private Memory memory;
    private List<TypeCheckerError> errors;

    @Before
    public void setUp() throws TasException {
        memory = new Memory();
        s = memory.getSymbolTable();
        errors = new ArrayList<>();

    }

    public void miniJajaParserTestValid(String fileName) throws IOException {
        try {
            ASTMjjStart ast = MiniJajaAnalyser.parseFromFile("src/test/typeCheckerTestFile/"+fileName+".mjj");
            String res = (String) ast.jjtAccept(new ASTMjjPrinter(),null);
        }catch (FileNotFoundException e){
            Assert.fail("File not found");
        }catch (ParseException e){
            System.out.println(e.getMessage());
            Assert.fail("Should not throw ParseException");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void miniJajaTypeChecker(String fileName) {
        try {
            setUp();
        }catch (Exception ignored){}
        try {
            ASTMjjStart ast = MiniJajaAnalyser.parseFromFile("src/test/typeCheckerTestFile/"+fileName+".mjj");
            ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.FIRST_PASS,memory, errors),null);
            ast.jjtAccept(new TypeCheckerVisitor(TypeCheckerVisitor.SECOND_PASS,memory, errors),null);

        }catch (Exception ignored){}
    }


    public boolean containsAllErrorsWithSimpleNames(List<TypeCheckerError> errors, List<String> errorSimpleNames) {
        // Créez une liste de noms simples à partir des noms complets dans les erreurs
        List<String> errorNamesPresent = errors.stream()
                .map(error -> extractSimpleClassName(error.getClass().getName()))
                .toList();

        // Vérifiez que chaque nom simple fourni est présent dans la liste des noms extraits
        return errorNamesPresent.containsAll(errorSimpleNames);
    }

    private String extractSimpleClassName(String fullClassName) {
        return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
    }

    @Test
    public void classNameAddSymbolTable() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationVoidClass");
        miniJajaTypeChecker("affectationVoidClass");

        InfoIdent q = new InfoIdent("affectation", Type.ENTIER, Sorte.VAR);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }

    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void affectationVoidClassFirstPass() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationVoidClass");
        miniJajaTypeChecker("affectationVoidClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of("VoidTypeError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@x", Type.VOID, Sorte.VAR);
        s.get(q);
    }

    @Test
    public void affectationIntClassFirstPass() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationIntClass");
        miniJajaTypeChecker("affectationIntClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@x", Type.ENTIER, Sorte.VAR);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }

    @Test
    public void affectation2IntDuplicateClassFirstPass() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectation2IntDuplicateClass");
        miniJajaTypeChecker("affectation2IntDuplicateClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of("DuplicationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@x", Type.ENTIER, Sorte.VAR);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }

    @Test
    public void affectationDuplicateIntAndVoidClassFirstPass() throws IOException {
        miniJajaParserTestValid("affectationDuplicateIntAndVoidClass");
        miniJajaTypeChecker("affectationDuplicateIntAndVoidClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of("VoidTypeError","DuplicationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);


    }

    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void affectationIntAndVoidClassFirstPass() throws IOException, MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationIntAndVoidClass");
        miniJajaTypeChecker("affectationIntAndVoidClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of("VoidTypeError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@x", Type.VOID, Sorte.VAR);
        s.get(q);

        InfoIdent q2 = new InfoIdent("class@x", Type.ENTIER, Sorte.VAR);
        InfoIdent r = s.get(q2);
        Assert.assertEquals(q.getID(),r.getID());
    }


    @Test
    public void affectation2IntNotDuplicateClassFirstPass() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectation2IntNotDuplicateClass");
        miniJajaTypeChecker("affectation2IntNotDuplicateClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@x", Type.ENTIER, Sorte.VAR);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q2 = new InfoIdent("class@y", Type.ENTIER, Sorte.VAR);
        InfoIdent r2 = s.get(q2);
        Assert.assertEquals(q2.getID(),r2.getID());
    }


    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void affectation2VoidAnd2DuplicateIntClassFirstPass() throws IOException, MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectation2VoidAnd2DuplicateIntClass");
        miniJajaTypeChecker("affectation2VoidAnd2DuplicateIntClass");
        Assert.assertFalse(errors.isEmpty());
        Assert.assertEquals(3, errors.size());

        List<String> requiredErrorNames = List.of("VoidTypeError", "DuplicationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@x", Type.VOID, Sorte.VAR);
        s.get(q);

        InfoIdent q1 = new InfoIdent("class@x", Type.ENTIER, Sorte.VAR);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q1.getID(),r.getID());
    }
    @Test
    public void affectationBoolClassFirstPass() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationBoolClass");
        miniJajaTypeChecker("affectationBoolClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@x", Type.BOOL, Sorte.VAR);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }

    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void affectationDuplicateBoolAndIntClassFirstPass() throws IOException, MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationDuplicateBoolAndIntClass");
        miniJajaTypeChecker("affectationDuplicateBoolAndIntClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "DuplicationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@x", Type.BOOL, Sorte.VAR);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q1 = new InfoIdent("class@x", Type.ENTIER, Sorte.VAR);
        s.get(q1);
    }
    @Test
    public void affectationBoolAndIntClassFirstPass() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationBoolAndIntClass");
        miniJajaTypeChecker("affectationBoolAndIntClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@x", Type.BOOL, Sorte.VAR);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q1 = new InfoIdent("class@y", Type.ENTIER, Sorte.VAR);
        InfoIdent r1 = s.get(q1);
        Assert.assertEquals(q1.getID(),r1.getID());
    }

    @Test
    public void affectationFinalIntClassFirstPass() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationFinalIntClass");
        miniJajaTypeChecker("affectationFinalIntClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@x", Type.ENTIER, Sorte.VCST);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }

    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void affectationFinalIntClassFailFirstPass() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationFinalIntClass");
        miniJajaTypeChecker("affectationFinalIntClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@x", Type.ENTIER, Sorte.CST);
        s.get(q);
    }


    @Test
    public void affectationFinalwithIntClassFirstPass() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationFinalwithIntClass");
        miniJajaTypeChecker("affectationFinalwithIntClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@i", Type.ENTIER, Sorte.VCST);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }
    @Test
    public void affectationFinalBoolClassFirstPass() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationFinalBoolClass");
        miniJajaTypeChecker("affectationFinalBoolClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@x", Type.BOOL, Sorte.VCST);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

    }
    @Test
    public void affectationFinalIntDuplicateClassFirstPass() throws IOException, MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationFinalIntDuplicateClass");
        miniJajaTypeChecker("affectationFinalIntDuplicateClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "DuplicationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@x", Type.ENTIER, Sorte.VCST);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }

    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void affectationFinalDuplicateIntAndBoolClassFirstPass() throws IOException, MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationFinalDuplicateIntAndBool");
        miniJajaTypeChecker("affectationFinalDuplicateIntAndBool");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "DuplicationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@x", Type.ENTIER, Sorte.VCST);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q1 = new InfoIdent("class@x", Type.BOOL, Sorte.VCST);
        s.get(q1);
    }

    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void affectationFinalAndVarDuplicateClassFirstPass() throws IOException, MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationFinalAndVarDuplicateClass");
        miniJajaTypeChecker("affectationFinalAndVarDuplicateClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "DuplicationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@x", Type.ENTIER, Sorte.VCST);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q1 = new InfoIdent("class@x", Type.ENTIER, Sorte.VAR);
        s.get(q1);

    }

    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void affectationFinalBoolAndVarIntDuplicateClassFirstPass() throws IOException, MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationFinalBoolAndVarIntDuplicateClass");
        miniJajaTypeChecker("affectationFinalBoolAndVarIntDuplicateClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "DuplicationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@x", Type.BOOL, Sorte.CST);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q1 = new InfoIdent("class@x", Type.ENTIER, Sorte.VAR);
        s.get(q1);
    }

    @Test
    public void methodeIntWhithoutParamClassFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("methodeIntWhithoutParamClass");
        miniJajaTypeChecker("methodeIntWhithoutParamClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@f", Type.ENTIER, Sorte.METH);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }


    @Test
    public void methodeIntWhithOneIntParamClassFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("methodeIntWhithOneIntParamClass");
        miniJajaTypeChecker("methodeIntWhithOneIntParamClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@f@int", Type.ENTIER, Sorte.METH);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q2 = new InfoIdent("class@f@int@i", Type.ENTIER, Sorte.VAR);
        InfoIdent r2 = s.get(q2);
        Assert.assertEquals(q2.getID(),r2.getID());

    }

    @Test
    public void methodeIntWithParam2IntClassFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("methodeIntWithParam2IntClass");
        miniJajaTypeChecker("methodeIntWithParam2IntClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "DuplicationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@f@int@int", Type.ENTIER, Sorte.METH);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q2 = new InfoIdent("class@f@int@int@i", Type.ENTIER, Sorte.VAR);
        InfoIdent r2 = s.get(q2);
        Assert.assertEquals(q2.getID(),r2.getID());
    }

    @Test
    public void methodeIntWithIntAntBoolParamFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("methodeIntWithIntAntBoolParam");
        miniJajaTypeChecker("methodeIntWithIntAntBoolParam");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@f@int@boolean", Type.ENTIER, Sorte.METH);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q2 = new InfoIdent("class@f@int@boolean@i", Type.ENTIER, Sorte.VAR);
        InfoIdent r2 = s.get(q2);
        Assert.assertEquals(q2.getID(),r2.getID());

        InfoIdent q3 = new InfoIdent("class@f@int@boolean@x", Type.BOOL, Sorte.VAR);
        InfoIdent r3 = s.get(q3);
        Assert.assertEquals(q3.getID(),r3.getID());
    }

    @Test
    public void declTabIntClassFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("declTabIntClass");
        miniJajaTypeChecker("declTabIntClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@t", Type.ENTIER, Sorte.TAB);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }

    @Test
    public void declTabBoolClassFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("declTabBoolClass");
        miniJajaTypeChecker("declTabBoolClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@t", Type.BOOL, Sorte.TAB);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }

    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void declTabVoidClassFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("declTabVoidClass");
        miniJajaTypeChecker("declTabVoidClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of("VoidTypeError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@t", Type.VOID, Sorte.TAB);
        s.get(q);
    }



    @Test
    public void declTabIntAndMethDuplicationClassFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("declTabIntAndMethDuplicationClass");
        miniJajaTypeChecker("declTabIntAndMethDuplicationClass");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@t", Type.ENTIER, Sorte.TAB);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q1 = new InfoIdent("class@t", Type.ENTIER, Sorte.METH);
        InfoIdent r1 = s.get(q1);
        Assert.assertEquals(q1.getID(),r1.getID());
    }
    @Test
    public void declTabIntInMethFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("declTabIntInMeth");
        miniJajaTypeChecker("declTabIntInMeth");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@f@t", Type.ENTIER, Sorte.TAB);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q1 = new InfoIdent("class@f", Type.ENTIER, Sorte.METH);
        InfoIdent r1 = s.get(q1);
        Assert.assertEquals(q1.getID(),r1.getID());
    }

    @Test
    public void declTabIntInMainFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("declTabIntInMain");
        miniJajaTypeChecker("declTabIntInMain");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q1 = new InfoIdent("class@main@t", Type.ENTIER, Sorte.TAB);
        InfoIdent r1 = s.get(q1);
        Assert.assertEquals(q1.getID(),r1.getID());
    }


    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void declTabIntAndIntDuplicationClassFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("declTabIntAndIntDuplicationClass");
        miniJajaTypeChecker("declTabIntAndIntDuplicationClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of("DuplicationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@t", Type.ENTIER, Sorte.TAB);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q1 = new InfoIdent("class@t", Type.ENTIER, Sorte.VAR);
        s.get(q1);
    }

    @Test
    public void declTabIntWithBoolInClassSecondPass() throws IOException {
        miniJajaParserTestValid("declTabIntWithBoolInClass");
        miniJajaTypeChecker("declTabIntWithBoolInClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of("TabDeclarationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

    }

    @Test
    public void declTabIntWithIntInClassSecondPass() throws IOException {
        miniJajaParserTestValid("declTabIntWithIntInClass");
        miniJajaTypeChecker("declTabIntWithIntInClass");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void declTabIntWithOperationInSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("declTabIntWithOperationInSuccess");
        miniJajaTypeChecker("declTabIntWithOperationInSuccess");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void declTabMoinsIntClassSecondPass() throws IOException {
        miniJajaParserTestValid("declTabMoinsIntClass");
        miniJajaTypeChecker("declTabMoinsIntClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of("NegativeArraySizeError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

    }


    @Test
    public void methodeBoolWithoutParamFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("methodeBoolWithoutParam");
        miniJajaTypeChecker("methodeBoolWithoutParam");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@f", Type.BOOL, Sorte.METH);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }

    @Test
    public void methodeVoidWithoutParamFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("methodeVoidWithoutParam");
        miniJajaTypeChecker("methodeVoidWithoutParam");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@f", Type.VOID, Sorte.METH);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }


    @Test
    public void methodeIntWithIntVarsFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("methodeIntWithIntVars");
        miniJajaTypeChecker("methodeIntWithIntVars");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@f", Type.ENTIER, Sorte.METH);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q1 = new InfoIdent("class@f@i", Type.ENTIER, Sorte.VAR);
        InfoIdent r1 = s.get(q1);
        Assert.assertEquals(q1.getID(),r1.getID());
    }

    @Test
    public void methodeIntWithBoolAndIntVarsFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("methodeIntWithBoolAndIntVars");
        miniJajaTypeChecker("methodeIntWithBoolAndIntVars");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@f@int", Type.ENTIER, Sorte.METH);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q1 = new InfoIdent("class@f@int@i", Type.ENTIER, Sorte.VAR);
        InfoIdent r1 = s.get(q1);
        Assert.assertEquals(q1.getID(),r1.getID());

        InfoIdent q2 = new InfoIdent("class@f@int@y", Type.BOOL, Sorte.VAR);
        InfoIdent r2 = s.get(q2);
        Assert.assertEquals(q2.getID(),r2.getID());

        InfoIdent q3 = new InfoIdent("class@f@int@j", Type.ENTIER, Sorte.VAR);
        InfoIdent r3 = s.get(q3);
        Assert.assertEquals(q3.getID(),r3.getID());
    }

    @Test
    public void methodeIntWith2ParamDuplicateIntAndBoolFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("methodeIntWith2ParamDuplicateIntAndBool");
        miniJajaTypeChecker("methodeIntWith2ParamDuplicateIntAndBool");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "DuplicationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@f@int@boolean", Type.ENTIER, Sorte.METH);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q2 = new InfoIdent("class@f@int@boolean@i", Type.ENTIER, Sorte.VAR);
        InfoIdent r2 = s.get(q2);
        Assert.assertEquals(q2.getID(),r2.getID());
    }

    @Test
    public void methodeDuplicateEnteteAndVarFirstPass() throws IOException, SymbolTableException.UnknownSymbolException, MemoryException {
        miniJajaParserTestValid("methodeDuplicateEnteteAndVar");
        miniJajaTypeChecker("methodeDuplicateEnteteAndVar");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "DuplicationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

        InfoIdent q = new InfoIdent("class@f@int", Type.ENTIER, Sorte.METH);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q2 = new InfoIdent("class@f@int@i", Type.ENTIER, Sorte.VAR);
        InfoIdent r2 = s.get(q2);
        Assert.assertEquals(q2.getID(),r2.getID());
    }


    @Test
    public void affectationIntMainFirstPass() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationIntMain");
        miniJajaTypeChecker("affectationIntMain");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@main@i", Type.ENTIER, Sorte.VAR);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());
    }

    @Test
    public void affectationSameIntClasseAndMainFirstPass() throws IOException, MemoryException, SymbolTableException.UnknownSymbolException {
        miniJajaParserTestValid("affectationSameIntClasseAndMain");
        miniJajaTypeChecker("affectationSameIntClasseAndMain");
        Assert.assertTrue(errors.isEmpty());

        InfoIdent q = new InfoIdent("class@i", Type.ENTIER, Sorte.VAR);
        InfoIdent r = s.get(q);
        Assert.assertEquals(q.getID(),r.getID());

        InfoIdent q2 = new InfoIdent("class@main@i", Type.ENTIER, Sorte.VAR);
        InfoIdent r2 = s.get(q2);
        Assert.assertEquals(q2.getID(),r2.getID());
    }

    @Test
    public void affectationBoolIntoIntClassSecondPass() throws IOException {
        miniJajaParserTestValid("affectationBoolIntoIntClass");
        miniJajaTypeChecker("affectationBoolIntoIntClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "TypeMismatchError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

    }

    @Test
    public void affectationIntIntoIntClassClassSecondPass() throws IOException {
        miniJajaParserTestValid("affectationIntIntoIntClass");
        miniJajaTypeChecker("affectationIntIntoIntClass");
        Assert.assertTrue(errors.isEmpty());

    }

    @Test
    public void affectationIntIntoBoolClassSecondPass() throws IOException {
        miniJajaParserTestValid("affectationIntIntoBoolClass");
        miniJajaTypeChecker("affectationIntIntoBoolClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "TypeMismatchError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

    }

    @Test
    public void affectationBoolIntoBoolClassSecondPass() throws IOException {
        miniJajaParserTestValid("affectationBoolIntoBoolClass");
        miniJajaTypeChecker("affectationBoolIntoBoolClass");
        Assert.assertTrue(errors.isEmpty());

    }

    @Test
    public void affectationFinalIntIntoIntClassSecondPass() throws IOException {
        miniJajaParserTestValid("affectationFinalIntIntoIntClass");
        miniJajaTypeChecker("affectationFinalIntIntoIntClass");
        Assert.assertTrue(errors.isEmpty());

    }

    @Test
    public void affectationFinalIntIntoBoolClassSecondPass() throws IOException {
        miniJajaParserTestValid("affectationFinalIntIntoBoolClass");
        miniJajaTypeChecker("affectationFinalIntIntoBoolClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "TypeMismatchError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);


    }

    @Test
    public void addIntAndIntSecondPass() throws IOException {
        miniJajaParserTestValid("addIntAndInt");
        miniJajaTypeChecker("addIntAndInt");
        Assert.assertTrue(errors.isEmpty());

    }

    @Test
    public void addIntAndBoolSecondPass() throws IOException {
        miniJajaParserTestValid("addIntAndBool");
        miniJajaTypeChecker("addIntAndBool");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);


    }

    @Test
    public void sousIntAndIntSecondPass() throws IOException {
        miniJajaParserTestValid("sousIntAndInt");
        miniJajaTypeChecker("sousIntAndInt");
        Assert.assertTrue(errors.isEmpty());

    }

    @Test
    public void sousIntAndBoolSecondPass() throws IOException {
        miniJajaParserTestValid("sousIntAndBool");
        miniJajaTypeChecker("sousIntAndBool");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);


    }

    @Test
    public void multIntAndIntSecondPass() throws IOException {
        miniJajaParserTestValid("multIntAndInt");
        miniJajaTypeChecker("multIntAndInt");
        Assert.assertTrue(errors.isEmpty());

    }

    @Test
    public void multIntAndBoolSecondPass() throws IOException {
        miniJajaParserTestValid("multIntAndBool");
        miniJajaTypeChecker("multIntAndBool");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);


    }

    @Test
    public void divIntAndIntSecondPass() throws IOException {
        miniJajaParserTestValid("divIntAndInt");
        miniJajaTypeChecker("divIntAndInt");
        Assert.assertTrue(errors.isEmpty());

    }

    @Test
    public void divIntAndBoolSecondPass() throws IOException {
        miniJajaParserTestValid("divIntAndBool");
        miniJajaTypeChecker("divIntAndBool");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

    }

    @Test
    public void addMultDivSousComplexSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("addMultDivSousComplexSuccess");
        miniJajaTypeChecker("addMultDivSousComplexSuccess");
        Assert.assertTrue(errors.isEmpty());

    }

    @Test
    public void addMultDivSousComplexFailSecondPass() throws IOException {
        miniJajaParserTestValid("addMultDivSousComplexFail");
        miniJajaTypeChecker("addMultDivSousComplexFail");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);


    }

    @Test
    public void affectationFinalIntOperationClassSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("affectationFinalIntOperationClassSuccess");
        miniJajaTypeChecker("affectationFinalIntOperationClassSuccess");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void affectationFinalIntOperationClassFailSecondPass() throws IOException {
        miniJajaParserTestValid("affectationFinalIntOperationClassFail");
        miniJajaTypeChecker("affectationFinalIntOperationClassFail");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

    }

    @Test
    public void affectationIntMoinIntSecondPass() throws IOException {
        miniJajaParserTestValid("affectationIntMoinInt");
        miniJajaTypeChecker("affectationIntMoinInt");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void affectationIntMoinBoolSecondPass() throws IOException {
        miniJajaParserTestValid("affectationIntMoinBool");
        miniJajaTypeChecker("affectationIntMoinBool");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void egalSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("egalSuccess");
        miniJajaTypeChecker("egalSuccess");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void egalFailSecondPass() throws IOException {
        miniJajaParserTestValid("egalFail");
        miniJajaTypeChecker("egalFail");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void egalSuccessWithIdentSecondPass() throws IOException {
        miniJajaParserTestValid("egalSuccessWithIdent");
        miniJajaTypeChecker("egalSuccessWithIdent");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void egalFailWithIdentSecondPass() throws IOException {
        miniJajaParserTestValid("egalFailWithIdent");
        miniJajaTypeChecker("egalFailWithIdent");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void egalWithMethSecondPass() throws IOException {
        miniJajaParserTestValid("egalWithMeth");
        miniJajaTypeChecker("egalWithMeth");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "SorteError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }


    @Test
    public void egalWithCstSecondPass() throws IOException {
        miniJajaParserTestValid("egalWithCst");
        miniJajaTypeChecker("egalWithCst");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void egalWithCstBoolSecondPass() throws IOException {
        miniJajaParserTestValid("egalWithCstBool");
        miniJajaTypeChecker("egalWithCstBool");

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }
    @Test
    public void egalInMethFailSecondPass() throws IOException {
        miniJajaParserTestValid("egalInMethFail");
        miniJajaTypeChecker("egalInMethFail");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "SorteError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }


    @Test
    public void supSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("supSuccess");
        miniJajaTypeChecker("supSuccess");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void supFailSecondPass() throws IOException {
        miniJajaParserTestValid("supFail");
        miniJajaTypeChecker("supFail");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void etSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("etSuccess");
        miniJajaTypeChecker("etSuccess");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void etFailSecondPass() throws IOException {
        miniJajaParserTestValid("etFail");
        miniJajaTypeChecker("etFail");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void ouSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("ouSuccess");
        miniJajaTypeChecker("ouSuccess");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void ouFailSecondPass() throws IOException {
        miniJajaParserTestValid("ouFail");
        miniJajaTypeChecker("ouFail");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void incrIntMainSecondPass() throws IOException {
        miniJajaParserTestValid("incrIntMain");
        miniJajaTypeChecker("incrIntMain");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void incrIntClassInMainSecondPass() throws IOException {
        miniJajaParserTestValid("incrIntMain");
        miniJajaTypeChecker("incrIntMain");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void incrIntClassAndMainSecondPass() throws IOException {
        miniJajaParserTestValid("incrIntMain");
        miniJajaTypeChecker("incrIntMain");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void incrTableauClassSecondPass() throws IOException {
        miniJajaParserTestValid("incrTableauClass");
        miniJajaTypeChecker("incrTableauClass");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "SorteError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

    }

    @Test
    public void incrTabIntSecondPass() throws IOException {
        miniJajaParserTestValid("incrTabInt");
        miniJajaTypeChecker("incrTabInt");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void incrTabBooleanPass() throws IOException {
        miniJajaParserTestValid("incrTabBoolean");
        miniJajaTypeChecker("incrTabBoolean");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void NotWithIdentFailSecondPass() throws IOException {
        miniJajaParserTestValid("NotWithIdentFail");
        miniJajaTypeChecker("NotWithIdentFail");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

    }


    @Test
    public void NotWithIdentSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("notWithIDentSuccess");
        miniJajaTypeChecker("notWithIDentSuccess");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void notWithIdentNotInScopeSecondPass() throws IOException {
        miniJajaParserTestValid("notWithIdentNotInScope");
        miniJajaTypeChecker("notWithIdentNotInScope");
        Assert.assertTrue(errors.isEmpty());


    }

    @Test
    public void sommeInMethIntSecondPass() throws IOException {
        miniJajaParserTestValid("sommeInMethInt");
        miniJajaTypeChecker("sommeInMethInt");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void sommeWithEnteteInMethSecondPass() throws IOException {
        miniJajaParserTestValid("sommeWithEnteteInMeth");
        miniJajaTypeChecker("sommeWithEnteteInMeth");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void sommeWithBooleanAndIntSecondPass() throws IOException {
        miniJajaParserTestValid("sommeWithBooleanAndInt");
        miniJajaTypeChecker("sommeWithBooleanAndInt");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

    }

    @Test
    public void sommeWithBoolAndBoolSecondPass() throws IOException {
        miniJajaParserTestValid("sommeWithBoolAndBool");
        miniJajaTypeChecker("sommeWithBoolAndBool");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError","TypeMismatchError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

    }

    @Test
    public void sommeWithTabIntSecondPass() throws IOException {
        miniJajaParserTestValid("sommeWithTabInt");
        miniJajaTypeChecker("sommeWithTabInt");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void sommeWithTabBoolSecondPass() throws IOException {
        miniJajaParserTestValid("sommeWithTabBool");
        miniJajaTypeChecker("sommeWithTabBool");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "TypeMismatchError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void ifExpBooleanSecondPass() throws IOException {
        miniJajaParserTestValid("ifExpBoolean");
        miniJajaTypeChecker("ifExpBoolean");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void ifExpNotBooleanSecondPass() throws IOException {
        miniJajaParserTestValid("ifExpNotBoolean");
        miniJajaTypeChecker("ifExpNotBoolean");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void ifExpEmptySecondPass() throws IOException {
        miniJajaParserTestValid("ifExpEmpty");
        miniJajaTypeChecker("ifExpEmpty");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "EmptyBlockError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void ifEmptyAndElseNotEmptySecondPass() throws IOException {
        miniJajaParserTestValid("ifEmptyAndElseNotEmpty");
        miniJajaTypeChecker("ifEmptyAndElseNotEmpty");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "EmptyBlockError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }
    @Test
    public void ifAndElseSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("ifAndElseSuccess");
        miniJajaTypeChecker("ifAndElseSuccess");
        Assert.assertTrue(errors.isEmpty());
    }
    @Test
    public void whileEmptyBlockSecondPass() throws IOException {
        miniJajaParserTestValid("whileEmptyBlock");
        miniJajaTypeChecker("whileEmptyBlock");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "EmptyBlockError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void whileExpIntSecondPass() throws IOException {
        miniJajaParserTestValid("whileExpInt");
        miniJajaTypeChecker("whileExpInt");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidOperationError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void whileExpBoolSecondPass() throws IOException {
        miniJajaParserTestValid("whileExpBool");
        miniJajaTypeChecker("whileExpBool");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void whileSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("whileSuccess");
        miniJajaTypeChecker("whileSuccess");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void writeLnStringSecondPass() throws IOException {
        miniJajaParserTestValid("writeLnString");
        miniJajaTypeChecker("writeLnString");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void writeLnIntSecondPass() throws IOException {
        miniJajaParserTestValid("writeLnInt");
        miniJajaTypeChecker("writeLnInt");
        Assert.assertTrue(errors.isEmpty());
    }
    @Test
    public void writeLnBoolSecondPass() throws IOException {
        miniJajaParserTestValid("writeLnBool");
        miniJajaTypeChecker("writeLnBool");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void writeLnMethSecondPass() throws IOException {
        miniJajaParserTestValid("writeLnMeth");
        miniJajaTypeChecker("writeLnMeth");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "SorteError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void writeLnCstSecondPass() throws IOException {
        miniJajaParserTestValid("writeLnCst");
        miniJajaTypeChecker("writeLnCst");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void lengthSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("lengthSuccess");
        miniJajaTypeChecker("lengthSuccess");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void lengthIntSecondPass() throws IOException {
        miniJajaParserTestValid("lengthInt");
        miniJajaTypeChecker("lengthInt");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "SorteError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void lengthMethSecondPass() throws IOException {
        miniJajaParserTestValid("lengthMeth");
        miniJajaTypeChecker("lengthMeth");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "SorteError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void returnVoidSecondPass() throws IOException {
        miniJajaParserTestValid("returnVoid");
        miniJajaTypeChecker("returnVoid");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidReturnError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void returnIntMethIntSecondPass() throws IOException {
        miniJajaParserTestValid("returnIntMethInt");
        miniJajaTypeChecker("returnIntMethInt");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void returnBoolMethIntSecondPass() throws IOException {
        miniJajaParserTestValid("returnBoolMethInt");
        miniJajaTypeChecker("returnBoolMethInt");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidReturnError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void returnBoolMethVoidSecondPass() throws IOException {
        miniJajaParserTestValid("returnBoolMethVoid");
        miniJajaTypeChecker("returnBoolMethVoid");
        Assert.assertFalse(errors.isEmpty());
        Assert.assertEquals(1,errors.size());


        List<String> requiredErrorNames = List.of( "InvalidReturnError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void returnInClassAndMainSecondPass() throws IOException {
        miniJajaParserTestValid("returnInClassAndMain");
        miniJajaTypeChecker("returnInClassAndMain");
        Assert.assertFalse(errors.isEmpty());
        Assert.assertEquals(1,errors.size());

        List<String> requiredErrorNames = List.of( "InvalidReturnError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void returnBoolTabSecondPass() throws IOException {
        miniJajaParserTestValid("returnBoolTab");
        miniJajaTypeChecker("returnBoolTab");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void returnIntTabSecondPass() throws IOException {
        miniJajaParserTestValid("returnIntTab");
        miniJajaTypeChecker("returnIntTab");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void returnMethIntMethIntSecondPass() throws IOException {
        miniJajaParserTestValid("returnMethIntMethInt");
        miniJajaTypeChecker("returnMethIntMethInt");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void appelINotExistantMethSecondPass() throws IOException {
        miniJajaParserTestValid("appelINotExistantMeth");
        miniJajaTypeChecker("appelINotExistantMeth");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "CanNotFindSymbolError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void AppelISuccessSecondPass() throws IOException {
        miniJajaParserTestValid("AppelISuccess");
        miniJajaTypeChecker("AppelISuccess");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void appellIdentMEthSecondPass() throws IOException {
        miniJajaParserTestValid("appellIdentMEth");
        miniJajaTypeChecker("appellIdentMEth");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "SorteError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void appelIInvalidCallSecondPass() throws IOException {
        miniJajaParserTestValid("appelIInvalidCall");
        miniJajaTypeChecker("appelIInvalidCall");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidCallError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void appelEInexistantSecondPass() throws IOException {
        miniJajaParserTestValid("appelEInexistant");
        miniJajaTypeChecker("appelEInexistant");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "CanNotFindSymbolError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void appelEIntInIntSecondPass() throws IOException {
        miniJajaParserTestValid("appelEIntInInt");
        miniJajaTypeChecker("appelEIntInInt");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void appelEBoolInIntSecondPass() throws IOException {
        miniJajaParserTestValid("appelEBoolInInt");
        miniJajaTypeChecker("appelEBoolInInt");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "TypeMismatchError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void appelEVoidSecondPass() throws IOException {
        miniJajaParserTestValid("appelEVoid");
        miniJajaTypeChecker("appelEVoid");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidCallError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void assignementTabIntWithIntSecondPass() throws IOException {
        miniJajaParserTestValid("assignementTabIntWithInt");
        miniJajaTypeChecker("assignementTabIntWithInt");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void assignementTabIntWithBoolSecondPass() throws IOException {
        miniJajaParserTestValid("assignementTabIntWithBool");
        miniJajaTypeChecker("assignementTabIntWithBool");
        Assert.assertFalse(errors.isEmpty());
        Assert.assertEquals(2,errors.size());

        List<String> requiredErrorNames = List.of( "TypeMismatchError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void assignementIntMethSecondPass() throws IOException {
        miniJajaParserTestValid("assignementIntMeth");
        miniJajaTypeChecker("assignementIntMeth");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidCallError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void assignementTabIntoTabSecondPass() throws IOException {
        miniJajaParserTestValid("assignementTabIntoTab");
        miniJajaTypeChecker("assignementTabIntoTab");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void assignementIntIntoTabSecondPass() throws IOException {
        miniJajaParserTestValid("assignementIntIntoTab");
        miniJajaTypeChecker("assignementIntIntoTab");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "InvalidAssignementError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void assignementBoolTabIntoIntTabSecondPass() throws IOException {
        miniJajaParserTestValid("assignementBoolTabIntoIntTab");
        miniJajaTypeChecker("assignementBoolTabIntoIntTab");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "TypeMismatchError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void assignementBoolIntoIntSecondPass() throws IOException {
        miniJajaParserTestValid("assignementBoolIntoInt");
        miniJajaTypeChecker("assignementBoolIntoInt");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "TypeMismatchError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void assignementIntIntoIntSecondPass() throws IOException {
        miniJajaParserTestValid("assignementIntIntoInt");
        miniJajaTypeChecker("assignementIntIntoInt");
        Assert.assertTrue(errors.isEmpty());
    }


    @Test
    public void assignementBoolWithExpSecondPass() throws IOException {
        miniJajaParserTestValid("assignementBoolWithExp");
        miniJajaTypeChecker("assignementBoolWithExp");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void assignementIntWithTabSecondPass() throws IOException {
        miniJajaParserTestValid("assignementIntWithTab");
        miniJajaTypeChecker("assignementIntWithTab");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "SorteError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void crossIntFailSecondPass() throws IOException {
        miniJajaParserTestValid("crossIntFail");
        miniJajaTypeChecker("crossIntFail");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "CanNotFindSymbolError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void missingReturnInMethSecondPass() throws IOException {
        miniJajaParserTestValid("missingReturnInMeth");
        miniJajaTypeChecker("missingReturnInMeth");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "MissingReturnError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void missingReturnInMethSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("missingReturnInMethSuccess");
        miniJajaTypeChecker("missingReturnInMethSuccess");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void missingReturnWithReturnInIfSecondPass() throws IOException {
        miniJajaParserTestValid("missingReturnWithReturnInIf");
        miniJajaTypeChecker("missingReturnWithReturnInIf");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "MissingReturnError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void missingReturnIfSuccessSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("missingReturnIfSuccess");
        miniJajaTypeChecker("missingReturnIfSuccess");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void missingReturnIfElseSecondPass() throws IOException {
        miniJajaParserTestValid("missingReturnIfElse");
        miniJajaTypeChecker("missingReturnIfElse");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void missingReturnIfElseFailSecondPass() throws IOException {
        miniJajaParserTestValid("missingReturnIfElseFail");
        miniJajaTypeChecker("missingReturnIfElseFail");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "MissingReturnError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void missingReturnWhileSecondPass() throws IOException {
        miniJajaParserTestValid("missingReturnWhile");
        miniJajaTypeChecker("missingReturnWhile");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "MissingReturnError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void missingReturnIfInWhileSecondPass() throws IOException {
        miniJajaParserTestValid("missingReturnIfInWhile");
        miniJajaTypeChecker("missingReturnIfInWhile");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "MissingReturnError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void missingReturnIfAndElseInWhileSecondPass() throws IOException {
        miniJajaParserTestValid("missingReturnIfAndElseInWhile");
        miniJajaTypeChecker("missingReturnIfAndElseInWhile");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "MissingReturnError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);
    }

    @Test
    public void missingReturnIfAndElseInWhileSuccessSecondPass() throws IOException {
        miniJajaParserTestValid("missingReturnIfElse");
        miniJajaTypeChecker("missingReturnIfElse");
        Assert.assertTrue(errors.isEmpty());
    }

    @Test
    public void tabIndexType() throws IOException {
        miniJajaParserTestValid("tabIndexType");
        miniJajaTypeChecker("tabIndexType");
        Assert.assertFalse(errors.isEmpty());

        List<String> requiredErrorNames = List.of( "TypeMismatchError");
        boolean containsAllErrors = containsAllErrorsWithSimpleNames(errors, requiredErrorNames);
        Assert.assertTrue("La liste d'erreurs ne contient pas toutes les erreurs spécifiées", containsAllErrors);

    }

    @Test
    public void tabIndexTypeSuccess() throws IOException {
        miniJajaParserTestValid("tabIndexTypeSuccess");
        miniJajaTypeChecker("tabIndexTypeSuccess");
        Assert.assertTrue(errors.isEmpty());
    }


}
