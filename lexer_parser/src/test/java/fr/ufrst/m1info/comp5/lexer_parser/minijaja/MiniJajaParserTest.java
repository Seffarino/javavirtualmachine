package fr.ufrst.m1info.comp5.lexer_parser.minijaja;

import fr.ufrst.m1info.comp5.lexer_parser.minijaja.utils.ASTMjjPrinter;
import fr.ufrst.m1info.comp5.minijaja.*;
import org.junit.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.io.FileNotFoundException;

public class MiniJajaParserTest {

    @Test
    public void testFromStrShouldWork(){
        String str = "class Test { int a = 3 +3; main{}}";
        try {
            ASTMjjStart ast = MiniJajaAnalyser.parseFromString(str);
            System.out.println(ast.jjtAccept(new ASTMjjPrinter(), null));
        }catch (ParseException e){
            System.out.println(e.getMessage());
            Assert.fail("Should not throw ParseException");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void miniJajaParserTestValid(String fileName) throws IOException {
        String resExpected = new String(Files.readAllBytes(Paths.get("src/test/exempleFile/miniJaja/successTree/"+fileName+".xml")), "UTF-8");
        resExpected = resExpected.replaceAll("\\s+", "");;
        try {
            ASTMjjStart ast = MiniJajaAnalyser.parseFromFile("src/test/exempleFile/miniJaja/success/"+fileName+".mjj");
            String res = (String) ast.jjtAccept(new ASTMjjPrinter(),null);
            Assert.assertEquals(resExpected, res.replaceAll("\\s+", ""));
        }catch (FileNotFoundException e){
            Assert.fail("File not found");
        }catch (ParseException e){
            System.out.println(e.getMessage());
            Assert.fail("Should not throw ParseException");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void miniJajaParserTestInvalid(String fileName) {
        try{
            ASTMjjStart ast = MiniJajaAnalyser.parseFromFile("src/test/exempleFile/miniJaja/fail/"+fileName+".mjj");

            Assert.fail("Should throw ParseException");
        }catch (FileNotFoundException e){
            Assert.fail("File not found");
        }catch (ParseException  e){
            System.out.println(e.getMessage());
            Assert.assertTrue(true);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    // SUCCEED TESTS
    @Test
    public void affectationShouldBeValid() throws IOException {
        miniJajaParserTestValid("affectation");
    }

    @Test
    public void affectationAdditionShouldBeValid() throws IOException {
        miniJajaParserTestValid("affectationAddition");
    }

    @Test
    public void arrayBoolShouldBeValid() throws IOException {
        miniJajaParserTestValid("arrayBool");

    }

    @Test
    public void arrayCondShouldBeValid() throws IOException {
        miniJajaParserTestValid("arrayCond");

    }

    @Test
    public void arrayIntShouldBeValid() throws IOException {
        miniJajaParserTestValid("arrayInt");

    }

    @Test
    public void arrayLengthShouldBeValid() throws IOException {
        miniJajaParserTestValid("arrayLength");
    }

    @Test
    public void arrayListExpShouldBeValid() throws IOException {
        miniJajaParserTestValid("arrayListExp");
        //manque exnil, listexp($1,exnil)
    }

    @Test
    public void arrayMathShouldBeValid() throws IOException {
        miniJajaParserTestValid("arrayMath");
    }

    @Test
    public void arrayNegativeShouldBeValid() throws IOException {
        miniJajaParserTestValid("arrayNegative");

    }

    @Test
    public void arrayVoidShouldBeValid() throws IOException {
        miniJajaParserTestValid("arrayVoid");

    }

    @Test
    public void attrIntBoolShouldBeValid() throws IOException {
        miniJajaParserTestValid("attrIntBool");

    }

    @Test
    public void caractereSpecialShouldBeValid() throws IOException {
        miniJajaParserTestValid("caractereSpecial");

    }

    @Test
    public void conditionEqBoolShouldBeValid() throws IOException {
        miniJajaParserTestValid("conditionEqBool");
        //il ny a pas de <instrs><si></si></instrs> alors qu'il devrait
    }

    @Test
    public void conditionEqIntShouldBeValid() throws IOException {
        miniJajaParserTestValid("conditionEqInt");

    }

    @Test
    public void conditionEtShouldBeValid() throws IOException {
        miniJajaParserTestValid("conditionEt");

    }

    @Test
    public void conditionNonShouldBeValid() throws IOException {
        miniJajaParserTestValid("conditionNon");

    }

    @Test
    public void conditionOuShouldBeValid() throws IOException {
        miniJajaParserTestValid("conditionOu");

    }

    @Test
    public void conditionSupShouldBeValid() throws IOException {
        miniJajaParserTestValid("conditionSup");

    }

    @Test
    public void ifCompletRempliShouldBeValid() throws IOException {
        miniJajaParserTestValid("ifCompletRempli");

    }

    @Test
    public void ifCompletVideShouldBeValid() throws IOException {
        miniJajaParserTestValid("ifCompletVide");

    }

    @Test
    public void ifListExpShouldBeValid() throws IOException {
        miniJajaParserTestValid("ifListExp");

    }

    @Test
    public void ifSansElseShouldBeValid() throws IOException {
        miniJajaParserTestValid("ifSansElse");

    }

    @Test
    public void incrementationShouldBeValid() throws IOException {
        miniJajaParserTestValid("incrementation");

    }

    @Test
    public void lengthAnythingShouldBeValid() throws IOException {
        miniJajaParserTestValid("lengthAnything");

    }

    @Test
    public void lengthArrayShouldBeValid() throws IOException {
        miniJajaParserTestValid("lengthArray");

    }

    @Test
    public void lengthIntBoolShouldBeValid() throws IOException {
        miniJajaParserTestValid("lengthIntBool");

    }

    @Test
    public void methBoolShouldBeValid() throws IOException {
        miniJajaParserTestValid("methBool");
        //enil manquant dans le dernier noeud entetes car entetes(entete,enil)
    }

    @Test
    public void methBooleanShouldBeValid() throws IOException {
        miniJajaParserTestValid("methBoolean");
        // <entetes><enil/></entetes> alors que juste <enil/> suffit
        //car entetes -> vide     enil
    }

    @Test
    public void methFullShouldBeValid() throws IOException {
        miniJajaParserTestValid("methFull");
        //enil manquant dans le dernier noeud entetes car entetes(entete,enil)

    }

    @Test
    public void methIntegerShouldBeValid() throws IOException {
        miniJajaParserTestValid("methInteger");
        // <entetes><enil/></entetes> alors que juste <enil/> suffit
        //car entetes -> vide     enil
    }

    @Test
    public void methNoArgsShouldBeValid() throws IOException {
        miniJajaParserTestValid("methNoArgs");
        // <entetes><enil/></entetes> alors que juste <enil/> suffit
        //car entetes -> vide     enil
    }

    @Test
    public void methNoReturnShouldBeValid() throws IOException {
        miniJajaParserTestValid("methNoReturn");
        //enil manquant dans le dernier noeud entetes car entetes(entete,enil)
    }

    @Test
    public void methVoidShouldBeValid() throws IOException {
        miniJajaParserTestValid("methVoid");
        //enil manquant dans le dernier noeud entetes car entetes(entete,enil)
    }

    @Test
    public void minimalShouldBeValid() throws IOException {
        miniJajaParserTestValid("minimal");

    }

    @Test
    public void oneDeclarationWithInstShouldBeValid() throws IOException {
        miniJajaParserTestValid("oneDeclarationWithInst");

    }

    @Test
    public void oneDeclarationWithoutInstShouldBeValid() throws IOException {
        miniJajaParserTestValid("oneDeclarationWithoutInst");

    }

    @Test
    public void oneFunctionIntShouldBeValid() throws IOException {
        miniJajaParserTestValid("oneFunctionInt");
        //souci enil <entetes><enil/><entete> au lieu de juste <enil/>
    }

    @Test
    public void oneFunctionIntWithCallShouldBeValid() throws IOException {
        miniJajaParserTestValid("oneFunctionIntWithCall");
        //souci enil + <listexp><exnil></listexp> au lieu de juste <exnil>
    }

    @Test
    public void oneFunctionIntWithIntrsShouldBeValid() throws IOException {
        miniJajaParserTestValid("oneFunctionIntWithIntrs");
        //souci enil + <listexp><exnil></listexp> au lieu de juste <exnil>

    }

    @Test
    public void oneFuntionVoidShouldBeValid() throws IOException {
        miniJajaParserTestValid("oneFuntionVoid");
        //souci enil <entetes><enil/><entete> au lieu de juste <enil/>
    }

    @Test
    public void oneFuntionVoidWithCallShouldBeValid() throws IOException {
        miniJajaParserTestValid("oneFuntionVoidWithCall");
        //souci enil + <listexp><exnil></listexp> au lieu de juste <exnil>
    }

    @Test
    public void oneFuntionVoidWithInstrsShouldBeValid() throws IOException {
        miniJajaParserTestValid("oneFuntionVoidWithInstrs");
        //souci enil + <listexp><exnil></listexp> au lieu de juste <exnil>
    }

    @Test
    public void operationAddShouldBeValid() throws IOException {
        miniJajaParserTestValid("operationAdd");
    }

    @Test
    public void operationDivShouldBeValid() throws IOException {
        miniJajaParserTestValid("operationDiv");

    }

    @Test
    public void operationMultShouldBeValid() throws IOException {
        miniJajaParserTestValid("operationMult");
    }

    @Test
    public void operationSousShouldBeValid() throws IOException {
        miniJajaParserTestValid("operationSous");

    }

    @Test
    public void varBooleanShouldBeValid() throws IOException {
        miniJajaParserTestValid("varBoolean");
    }

    @Test
    public void varNotDeclaredShouldBeValid() throws IOException {
        miniJajaParserTestValid("varNotDeclared");

    }

    @Test
    public void voidVariableShouldBeValid() throws IOException {
        miniJajaParserTestValid("voidVariable");

    }

    @Test
    public void whileCondShouldBeValid() throws IOException {
        miniJajaParserTestValid("whileCond");
    }

    @Test
    public void whileEmptyBodyShouldBeValid() throws IOException {
        miniJajaParserTestValid("whileEmptyBody");

    }

    @Test
    public void whileFullShouldBeValid() throws IOException {
        miniJajaParserTestValid("whileFull");

    }

    @Test
    public void whileInWhileShouldBeValid() throws IOException {
        miniJajaParserTestValid("whileInWhile");

    }

    @Test
    public void whileMathShouldBeValid() throws IOException {
        miniJajaParserTestValid("whileMath");

    }

    @Test
    public void writeBoolShouldBeValid() throws IOException {
        miniJajaParserTestValid("writeBool");

    }

    @Test
    public void writeIntShouldBeValid() throws IOException {
        miniJajaParserTestValid("writeInt");

    }

    @Test
    public void writeStringShouldBeValid() throws IOException {
        miniJajaParserTestValid("writeString");

    }

    @Test
    public void testNonBeforeEtShouldBeValid() throws IOException {
        miniJajaParserTestValid("testNonBeforeEt");
    }

    @Test
    public void negAndMinusShouldBeValid() throws IOException {
        miniJajaParserTestValid("negAndMinus");

    }

    @Test
    public void quickSortShouldBeValid(){
        try {
            ASTMjjStart ast = MiniJajaAnalyser.parseFromFile("src/test/exempleFile/miniJaja/success/quickSort.mjj");
        }catch (FileNotFoundException  e){
            Assert.fail("File not found");
        }catch (Exception e){
            System.out.println(e.getMessage());
            Assert.fail("Should not throw ParseException");
        }
    }

    // FAIL TESTS
    @Test
    public void afterMainShouldBeInvalid(){
        miniJajaParserTestInvalid("afterMain");
    }

    @Test
    public void arrayWrongNameShouldBeInvalid(){
        miniJajaParserTestInvalid("arrayWrongName");
    }

    @Test
    public void arrayWrongTypeShouldBeInvalid(){
        miniJajaParserTestInvalid("arrayWrongType");
    }

    @Test
    public void classInClassShouldBeInvalid(){
        miniJajaParserTestInvalid("classInClass");
    }

    @Test
    public void classNotNamedShouldBeInvalid(){
        miniJajaParserTestInvalid("classNotNamed");
    }

    @Test
    public void declAfterMainShouldBeInvalid(){
        miniJajaParserTestInvalid("declAfterMain");
    }

    @Test
    public void functionInFunctionShouldBeInvalid(){
        miniJajaParserTestInvalid("functionInFunction");
    }

    @Test
    public void functionNotNamedShouldBeInvalid(){
        miniJajaParserTestInvalid("functionNotNamed");
    }

    @Test
    public void ifConditionVideShouldBeInvalid(){
        miniJajaParserTestInvalid("ifConditionVide");
    }

    @Test
    public void lengthEmptyShouldBeInvalid(){
        miniJajaParserTestInvalid("lengthEmpty");
    }

    @Test
    public void lengthNumberShouldBeInvalid(){
        miniJajaParserTestInvalid("lengthNumber");
    }

    @Test
    public void mainInstrBeforeVarShouldBeInvalid(){
        miniJajaParserTestInvalid("mainInstrBeforeVar");
    }

    @Test
    public void mainNotFinalShouldBeInvalid(){
        miniJajaParserTestInvalid("mainNotFinal");
    }

    @Test
    public void methInstrBeforeVarShouldBeInvalid(){
        miniJajaParserTestInvalid("methInstrBeforeVar");
    }

    @Test
    public void methMissingArgsShouldBeInvalid(){
        miniJajaParserTestInvalid("methMissingArgs");
    }

    @Test
    public void methMissingBodyShouldBeInvalid(){
        miniJajaParserTestInvalid("methMissingBody");
    }

    @Test
    public void methTypeDoubleShouldBeInvalid(){
        miniJajaParserTestInvalid("methTypeDouble");
    }

    @Test
    public void methWrongTypeShouldBeInvalid(){
        miniJajaParserTestInvalid("methWrongType");
    }

    @Test
    public void missColodonShouldBeInvalid(){
        miniJajaParserTestInvalid("missColodon");
    }

    @Test
    public void noMainShouldBeInvalid(){
        miniJajaParserTestInvalid("noMain");
    }

    @Test
    public void varTypeDoubleShouldBeInvalid(){
        miniJajaParserTestInvalid("varTypeDouble");
    }

    @Test
    public void whileEmptyCondShouldBeInvalid(){
        miniJajaParserTestInvalid("whileEmptyCond");
    }

    @Test
    public void whileNoBodyShouldBeInvalid(){
        miniJajaParserTestInvalid("whileNoBody");
    }

    @Test
    public void writeArrayShouldBeInvalid(){
        miniJajaParserTestInvalid("writeArray");
    }

    @Test
    public void writeNumberShouldBeInvalid(){
        miniJajaParserTestInvalid("writeNumber");
    }
}
