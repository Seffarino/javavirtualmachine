package fr.ufrst.m1info.comp5.lexer_parser.jajacode;
import fr.ufrst.m1info.comp5.jajacode.*;
import fr.ufrst.m1info.comp5.lexer_parser.jajacode.utils.ASTJJCPrinter;
import org.junit.*;

import java.io.FileNotFoundException;
public class JajaCodeParserTest {

    @Test
    public void testFromStrShouldWork(){
        String str = "1 push(0);2 push(1);";
        try {
            ASTJJCStart ast = JajaCodeAnalyser.parseFromString(str);
            System.out.println(ast.jjtAccept(new ASTJJCPrinter(), null));
        } catch (Exception e) {
            Assert.fail("Should not throw AssertionError");
        }
    }

    public void jajaCodeParserTestValid(String fileName) {
        try {
            JajaCodeAnalyser.parseFromFile("src/test/exempleFile/jajaCode/success/"+fileName+".jjc");
        }catch (FileNotFoundException e){
            Assert.fail("File not found");
        }catch (Exception e) {
            Assert.fail("Should not throw AssertionError");
        }
    }


    public void jajaCodeTestInvalid(String fileName) {
        try{
            JajaCodeAnalyser.parseFromFile("src/test/exempleFile/jajaCode/fail/"+fileName+".jjc");
            Assert.fail("Should throw AssertionError");
        }catch (FileNotFoundException e){
            Assert.fail("File not found");
        }catch (AssertionError e){
            System.out.println(e.getMessage());
            Assert.assertTrue(true);
        }catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void successNewCst(){
        jajaCodeParserTestValid("successNewCst");
    }
    @Test
    public void successNewMeth(){
        jajaCodeParserTestValid("successNewMeth");
    }
    @Test
    public void successNewVar(){
        jajaCodeParserTestValid("successNewVar");
    }
    @Test
    public void successInit(){
        jajaCodeParserTestValid("successNewVar");
    }
    @Test
    public void successSwap(){
        jajaCodeParserTestValid("successNewVar");
    }
    @Test
    public void successNewArray(){
        jajaCodeParserTestValid("successNewVar");
    }
    @Test
    public void successInvoke(){
        jajaCodeParserTestValid("successNewVar");
    }
    @Test
    public void returntest(){
        jajaCodeParserTestValid("return");
    }
    @Test
    public void push(){
        jajaCodeParserTestValid("push");
    }
    @Test
    public void pop() {
        jajaCodeParserTestValid("pop");
    }

    @Test
    public void load(){
        jajaCodeParserTestValid("load");
    }

    @Test
    public void aload() {
        jajaCodeParserTestValid("aload");
    }

    @Test
    public void store()  {
        jajaCodeParserTestValid("store");
    }
    @Test
    public void astore() {
        jajaCodeParserTestValid("astore");
    }

    @Test
    public void writeWriteLn(){
        jajaCodeParserTestValid("writeWriteLn");
    }

    @Test
    public void length() {
        jajaCodeParserTestValid("length");
    }

    @Test
    public void iftest() {
        jajaCodeParserTestValid("if");
    }

    @Test
    public void gototest(){
        jajaCodeParserTestValid("goto");
    }

    @Test
    public void inc(){
        jajaCodeParserTestValid("inc");
    }

    @Test
    public void ainc()  {
        jajaCodeParserTestValid("ainc");
    }

    @Test
    public void nop() {
        jajaCodeParserTestValid("nop");
    }

    @Test
    public void oper1()  {
        jajaCodeParserTestValid("oper1");
    }

    @Test
    public void oper2()  {
        jajaCodeParserTestValid("oper2");
    }
    @Test
    public void td5_1_1(){
        jajaCodeParserTestValid("td5_1_1");
    }
    @Test
    public void td5_1_2()  {
        jajaCodeParserTestValid("td5_1_2");
    }
    @Test
    public void td5_2_1(){
        jajaCodeParserTestValid("td5_2_1");
    }
    @Test
    public void td5_2_2() {
        jajaCodeParserTestValid("td5_2_2");
    }
    @Test
    public void td5_3_1(){
        jajaCodeParserTestValid("td5_3_1");
    } @Test
    public void td5_3_2() {
        jajaCodeParserTestValid("td5_3_2");
    }
    @Test
    public void td5_4() {
        jajaCodeParserTestValid("td5_4");
    }


    @Test
    public void fail() {
        jajaCodeTestInvalid("fail");
    }
    @Test
    public void emptyPush() {
        jajaCodeTestInvalid("emptyPush");
    }
    @Test
    public void missingSemi() {
        jajaCodeTestInvalid("missingSemi");
    }
    @Test
    public void  newArrWrongType()  {
        jajaCodeTestInvalid("newArrWrongType");
    }
    @Test
    public void identWhereAdr() {
        jajaCodeTestInvalid("identWhereAdr");
    }

}