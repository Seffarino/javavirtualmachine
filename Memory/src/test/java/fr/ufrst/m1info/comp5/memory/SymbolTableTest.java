package fr.ufrst.m1info.comp5.memory;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import fr.ufrst.m1info.comp5.memory.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SymbolTableTest {

    @Test
    public void symboleTableAdd() {
        SymbolTable s = new SymbolTable();

        try {
            InfoIdent q = new InfoIdent("k", Type.ENTIER, Sorte.VAR);
            s.add(q);
            InfoIdent res = s.get(q);
            Assert.assertEquals(q.getID(),res.getID());
        } catch (MemoryException | SymbolTableException.ExistingSymbolException |
                 SymbolTableException.UnknownSymbolException e) {
            Assert.fail();
        }

    }

    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void removeNonExistant() throws SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        try {
            InfoIdent q = new InfoIdent("k", Type.ENTIER, Sorte.VAR);
            s.remove(q);
        } catch (SymbolTableException.UnknownSymbolException e) {
            throw new SymbolTableException.UnknownSymbolException();
        } catch (MemoryException e) {
            Assert.fail();
        }
    }

    @Test
    public void removeExistant() {
        SymbolTable s = new SymbolTable();

        try {
            InfoIdent q = new InfoIdent("k", Type.ENTIER, Sorte.VAR);
            s.add(q);
            boolean res;
            InfoIdent r = s.get(q);
            Assert.assertEquals(q.getID(),r.getID());
            res = s.remove(q);
            Assert.assertTrue(res);
        } catch (MemoryException | SymbolTableException.ExistingSymbolException |
                 SymbolTableException.UnknownSymbolException e) {
            Assert.fail();
        }

    }
    @Test(expected = SymbolTableException.ExistingSymbolException.class)
    public void addExistant() throws SymbolTableException.ExistingSymbolException {
        SymbolTable s = new SymbolTable();

        try {
            InfoIdent q = new InfoIdent("k", Type.ENTIER, Sorte.VAR);
            s.add(q);
            InfoIdent r = s.get(q);
            s.add(q);
        }catch(SymbolTableException.ExistingSymbolException e){
            throw new SymbolTableException.ExistingSymbolException();
        } catch (MemoryException |
                 SymbolTableException.UnknownSymbolException e) {
            Assert.fail();
        }

    }
    @Test
    public void carambolage(){
        SymbolTable s = spy(SymbolTable.class);

        when(s.hash(anyString())).thenReturn(1);
        try {
            InfoIdent q = new InfoIdent("k", Type.ENTIER, Sorte.VAR);
            InfoIdent a = new InfoIdent("a", Type.ENTIER, Sorte.VAR);
            s.add(q);
            s.add(a);
            InfoIdent res = s.get(q);
            Assert.assertEquals(res.getNext().getID(), a.getID());
        } catch (MemoryException | SymbolTableException.UnknownSymbolException |
                 SymbolTableException.ExistingSymbolException e) {
            Assert.fail();
        }
    }
    @Test
    public void symboleTableAddInstance() {
        SymbolTable s = new SymbolTable();

        try {
            InfoIdent q = new InfoIdent("k", Type.ENTIER, Sorte.VAR);

            s.add(q);
            Assert.assertFalse(q.hasInstance());
            Assert.assertNull(s.getTopOfStack());
            s.addInstance(q.getID(),5);
            Assert.assertTrue(q.hasInstance());
            InfoInstance res = q.getInstance();
            Assert.assertEquals(5,res.getValue());
            Assert.assertEquals(s.getTopOfStack(),res);
        } catch (MemoryException | SymbolTableException.ExistingSymbolException |
                 SymbolTableException.UnknownSymbolException e) {
            Assert.fail();
        }

    }
    @Test
    public void symboleTableAddNewInstance() {
        SymbolTable s = new SymbolTable();

        try {
            InfoIdent q = new InfoIdent("k", Type.ENTIER, Sorte.VAR);

            s.add(q);
            Assert.assertFalse(q.hasInstance());
            Assert.assertNull(s.getTopOfStack());
            s.addInstance(q.getID(),5);
            Assert.assertTrue(q.hasInstance());
            InfoInstance res = q.getInstance();
            Assert.assertEquals(5,res.getValue());
            Assert.assertEquals(s.getTopOfStack(),res);
            Assert.assertNull(res.getSuivantPile());
            Assert.assertNull(res.getValeurPrecedente());
            s.addInstance(q.getID(),1);
            res = q.getInstance();
            Assert.assertEquals(1,res.getValue());
            Assert.assertEquals(s.getTopOfStack(),res);
            InfoInstance prevPile = res.getSuivantPile();
            InfoInstance prevVal = res.getValeurPrecedente();
            Assert.assertEquals(5,prevVal.getValue());
            Assert.assertEquals(5,prevPile.getValue());
            Assert.assertTrue(res.isTopStack());
            Assert.assertFalse(prevPile.isTopStack());


        } catch (MemoryException | SymbolTableException.ExistingSymbolException |
                 SymbolTableException.UnknownSymbolException e) {
            Assert.fail();
        }

    }
    @Test
    public void symboleTableAddInstanceNull() {
        SymbolTable s = new SymbolTable();

        try {
            InfoIdent q = new InfoIdent("k", Type.ENTIER, Sorte.VAR);

            s.add(q);
            Assert.assertFalse(q.hasInstance());
            Assert.assertNull(s.getTopOfStack());
            s.addInstance(q.getID(),null);
            Assert.assertTrue(q.hasInstance());
            InfoInstance res = q.getInstance();
            Assert.assertNull(res.getValue());
            Assert.assertEquals(s.getTopOfStack(),res);


        } catch (MemoryException | SymbolTableException.ExistingSymbolException |
                 SymbolTableException.UnknownSymbolException e) {
            Assert.fail();
        }

    }
    @Test
    public void symboleTableAddString(){
        SymbolTable s = new SymbolTable();
        try {
            InfoIdent q = new InfoIdent("class@f@entier" ,Type.ENTIER,Sorte.VAR);
            s.add(q);
            InfoIdent res = s.get(q);
            Assert.assertEquals(res.getID(),q.getID());


        } catch (MemoryException | SymbolTableException.ExistingSymbolException |
                 SymbolTableException.UnknownSymbolException e) {
            Assert.fail();
        }
    }
    @Test(expected = SymbolTableException.ExistingSymbolException.class)
    public void symboleTableSameID_DiffTYPE() throws SymbolTableException.ExistingSymbolException {
        SymbolTable s = new SymbolTable();
        try {
            InfoIdent q = new InfoIdent("x" ,Type.ENTIER,Sorte.VAR);
            InfoIdent r = new InfoIdent("x" ,Type.BOOL,Sorte.VAR);
            s.add(q);
            s.add(r);
            InfoIdent res = s.get(q);


        }

        catch (MemoryException |
                 SymbolTableException.UnknownSymbolException e) {
            Assert.fail();
        }
        catch(SymbolTableException.ExistingSymbolException e){
            throw new SymbolTableException.ExistingSymbolException();
        }

    }
    @Test(expected = SymbolTableException.ExistingSymbolException.class)
    public void symboleTableScope() throws SymbolTableException.ExistingSymbolException {
        SymbolTable s = new SymbolTable();
        try {
            InfoIdent q = new InfoIdent("class@x" ,Type.ENTIER,Sorte.CST);
            s.add(q);
            InfoIdent r = new InfoIdent("class@x" ,Type.BOOL,Sorte.CST);
           s.add(r);



        }

        catch (MemoryException e) {
            Assert.fail();
        }
        catch(SymbolTableException.ExistingSymbolException e){
            throw new SymbolTableException.ExistingSymbolException();
        }

    }


    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void symboleTableGetSameID_DiffType() throws SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        try {
            InfoIdent q = new InfoIdent("class@x" ,Type.ENTIER,Sorte.CST);
            s.add(q);
            InfoIdent r = new InfoIdent("class@x" ,Type.BOOL,Sorte.CST);
            s.get(r);



        }

        catch (MemoryException e) {
            Assert.fail();
        }
        catch(SymbolTableException.ExistingSymbolException e){
            throw new SymbolTableException.ExistingSymbolException();
        } catch (SymbolTableException.UnknownSymbolException e) {
            throw new SymbolTableException.UnknownSymbolException();
        }

    }
    @Test
    public void symboleTableGetByIDSuccess() throws SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        try {
            InfoIdent q = new InfoIdent("class@x" ,Type.ENTIER,Sorte.CST);
            s.add(q);
            InfoIdent res = s.get("class@x", false);
            Assert.assertEquals(q,res);


        }

        catch (MemoryException e) {
            Assert.fail();
        }
        catch(SymbolTableException.ExistingSymbolException e){
            throw new SymbolTableException.ExistingSymbolException();
        } catch (SymbolTableException.UnknownSymbolException e) {
            throw new SymbolTableException.UnknownSymbolException();
        }

    }

    @Test(expected = SymbolTableException.UnknownSymbolException.class)
    public void symboleTableGetByIDFail() throws SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        try {
            InfoIdent q = new InfoIdent("class@x" ,Type.ENTIER,Sorte.CST);
            s.add(q);
            InfoIdent res = s.get("class@x", true);


        }

        catch (MemoryException e) {
            Assert.fail();
        }
        catch(SymbolTableException.ExistingSymbolException e){
            throw new SymbolTableException.ExistingSymbolException();
        } catch (SymbolTableException.UnknownSymbolException e) {
            throw new SymbolTableException.UnknownSymbolException();
        }

    }
    @Test
    public void symboleTableGetByIDBothPresent() throws SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        try {
            InfoIdent q = new InfoIdent("class@x" ,Type.ENTIER,Sorte.CST);
            s.add(q);
            InfoIdent a = new InfoIdent("class@x" ,Type.ENTIER,Sorte.METH);
            s.add(a);
            InfoIdent res = s.get("class@x", true);
            Assert.assertEquals(a,res);
            res = s.get("class@x", false);
            Assert.assertEquals(q,res);


        }

        catch (MemoryException e) {
            Assert.fail();
        }
        catch(SymbolTableException.ExistingSymbolException e){
            throw new SymbolTableException.ExistingSymbolException();
        } catch (SymbolTableException.UnknownSymbolException e) {
            throw new SymbolTableException.UnknownSymbolException();
        }

    }
    @Test(expected = SymbolTableException.ExistingSymbolException.class)
    public void symboleTableAddAllSorte() throws SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        try {
            InfoIdent q = new InfoIdent("class@x" ,Type.ENTIER,Sorte.CST);
            s.add(q);
            InfoIdent a = new InfoIdent("class@x" ,Type.ENTIER,Sorte.METH);
            s.add(a);
            InfoIdent b = new InfoIdent("class@x" ,Type.ENTIER,Sorte.VAR);
            s.add(b);


        }

        catch (MemoryException e) {
            Assert.fail();
        }
        catch(SymbolTableException.ExistingSymbolException e){
            throw new SymbolTableException.ExistingSymbolException();
        }

    }


    @Test
    public void hashFuncTest(){
        SymbolTable s = new SymbolTable();
       String id = "hello";
       String di = "olleh";
       String maj = "Hello";
       Assert.assertNotEquals(s.hash(id),s.hash(di));
        Assert.assertNotEquals(s.hash(id),s.hash(maj));
        Assert.assertNotEquals(s.hash(maj),s.hash(di));
    }
    @Test
    public void testStack() throws MemoryException, SymbolTableException.UnknownSymbolException, SymbolTableException.ExistingSymbolException {
        SymbolTable s = new SymbolTable();
        InfoIdent i = new InfoIdent("i",Type.ENTIER,Sorte.VAR);
        InfoInstance a = new InfoInstance(10);
        s.add(i);
        s.addInstance("i",a);
        Assert.assertEquals(a,i.getInstance());
        Assert.assertEquals(a,s.getTopOfStack());
        InfoInstance b = new InfoInstance(12);
        s.addInstance("i",b);
        Assert.assertEquals(b,i.getInstance());
        Assert.assertEquals(b,s.getTopOfStack());
        InfoIdent j = new InfoIdent("a",Type.ENTIER,Sorte.VAR);
        InfoInstance c = new InfoInstance(14);
        InfoInstance d = new InfoInstance(16);
        s.add(j);
        s.addInstance(j.getID(),c);
        Assert.assertEquals(c,j.getInstance());
        Assert.assertEquals(c,s.getTopOfStack());
        s.addInstance(i.getID(),d);
        Assert.assertEquals(d,i.getInstance());
        Assert.assertEquals(d,s.getTopOfStack());
        InfoInstance curr = s.getTopOfStack();
        List<InfoInstance> myList= new ArrayList<>();
        while(curr!=null){
            myList.add(curr);
            curr = curr.getSuivantPile();
        }
        Assert.assertEquals(4,myList.size());
        Assert.assertEquals(d,myList.get(0));
        Assert.assertEquals(c,myList.get(1));
        Assert.assertEquals(b,myList.get(2));
        Assert.assertEquals(a,myList.get(3));
    }

    @Test
    public void testRemoveCheckStack() throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        InfoIdent i = new InfoIdent("i",Type.ENTIER,Sorte.VAR);
        InfoInstance a = new InfoInstance(10);
        s.add(i);
        s.addInstance("i",a);
        Assert.assertEquals(a,i.getInstance());
        Assert.assertEquals(a,s.getTopOfStack());
        InfoInstance b = new InfoInstance(12);
        s.addInstance("i",b);
        Assert.assertEquals(b,i.getInstance());
        Assert.assertEquals(b,s.getTopOfStack());
        InfoIdent j = new InfoIdent("a",Type.ENTIER,Sorte.VAR);
        InfoInstance c = new InfoInstance(14);
        InfoInstance d = new InfoInstance(16);
        s.add(j);
        s.addInstance(j.getID(),c);
        Assert.assertEquals(c,j.getInstance());
        Assert.assertEquals(c,s.getTopOfStack());
        s.addInstance(i.getID(),d);
        Assert.assertEquals(c.getSuivantPile(),b);
        Assert.assertEquals(c.getPrecedantPile(),d);
        s.removeFromStack(c);
        Assert.assertEquals(d.getSuivantPile(),b);
        Assert.assertEquals(b.getPrecedantPile(),d);

    }
    @Test
    public void testRemoveFromStack() throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        InfoIdent i = new InfoIdent("i",Type.ENTIER,Sorte.VAR);
        InfoInstance a = new InfoInstance(10);
        s.add(i);
        s.addInstance("i",a);
        Assert.assertEquals(a,i.getInstance());
        Assert.assertEquals(a,s.getTopOfStack());
        InfoInstance b = new InfoInstance(12);
        s.addInstance("i",b);
        Assert.assertEquals(b,i.getInstance());
        Assert.assertEquals(b,s.getTopOfStack());
        InfoIdent j = new InfoIdent("a",Type.ENTIER,Sorte.VAR);
        InfoInstance c = new InfoInstance(14);
        InfoInstance d = new InfoInstance(16);
        s.add(j);
        s.addInstance(j.getID(),c);
        Assert.assertEquals(c,j.getInstance());
        Assert.assertEquals(c,s.getTopOfStack());
        s.addInstance(i.getID(),d);
        s.remove(i);
        Assert.assertEquals(s.getTopOfStack(),c);
        Assert.assertNull(c.getPrecedantPile());
        Assert.assertNull(c.getSuivantPile());

    }
    @Test
    public void testRelink() throws MemoryException {
        InfoIdent i = new InfoIdent("i",Type.ENTIER,Sorte.VAR);
        InfoInstance a = new InfoInstance(15);
        InfoInstance b = new InfoInstance(12);
        InfoInstance c = new InfoInstance(10);
        i.addInstance(c);
        i.addInstance(b);
        i.addInstance(a);
        b.linkBeforeAndAfter();
        Assert.assertEquals(a.getValeurPrecedente(),c);
    }
    @Test
    public void testRelinkNull() throws MemoryException {
        InfoIdent i = new InfoIdent("i",Type.ENTIER,Sorte.VAR);
        InfoInstance a = new InfoInstance(15);
        InfoInstance b = new InfoInstance(12);
        InfoInstance c = new InfoInstance(10);
        //i.addInstance(c);
        i.addInstance(b);
        i.addInstance(a);
        b.linkBeforeAndAfter();
        Assert.assertNull(a.getValeurPrecedente());
    }
    @Test
    public void testRelinkFirst() throws MemoryException {
        InfoIdent i = new InfoIdent("i",Type.ENTIER,Sorte.VAR);
        InfoInstance a = new InfoInstance(15);
        InfoInstance b = new InfoInstance(12);
        InfoInstance c = new InfoInstance(10);
        i.addInstance(c);
        i.addInstance(b);
        i.addInstance(a);
        a.linkBeforeAndAfter();
        Assert.assertEquals(i.getInstance(),b);
    }
    @Test
    public void testRelinkRemove() throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        InfoIdent i = new InfoIdent("i",Type.ENTIER,Sorte.VAR);
        s.add(i);
        InfoInstance a = new InfoInstance(15);
        InfoInstance b = new InfoInstance(12);
        InfoInstance c = new InfoInstance(10);
        s.addInstance("i",c);
        s.addInstance("i",b);
        s.addInstance("i",a);
        s.removeFromStack(b);
        Assert.assertEquals(a.getValeurPrecedente(),c);
    }
    @Test
    public void testRelinkNullRemove() throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        InfoIdent i = new InfoIdent("i",Type.ENTIER,Sorte.VAR);
        s.add(i);
        InfoInstance a = new InfoInstance(15);
        InfoInstance b = new InfoInstance(12);
        InfoInstance c = new InfoInstance(10);
        //s.addInstance("i",c);
        s.addInstance("i",b);
        s.addInstance("i",a);
        s.removeFromStack(b);
        Assert.assertNull(a.getValeurPrecedente());
    }
    @Test
    public void testRelinkFirstRemove() throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        InfoIdent i = new InfoIdent("i",Type.ENTIER,Sorte.VAR);
        s.add(i);
        InfoInstance a = new InfoInstance(15);
        InfoInstance b = new InfoInstance(12);
        InfoInstance c = new InfoInstance(10);
        s.addInstance("i",c);
        s.addInstance("i",b);
        s.addInstance("i",a);
        s.removeFromStack(a);
        Assert.assertEquals(i.getInstance(),b);
    }
    @Test
    public void testRemoveAll() throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        InfoIdent i = new InfoIdent("i",Type.ENTIER,Sorte.VAR);
        s.add(i);
        InfoInstance a = new InfoInstance(15);
        InfoInstance b = new InfoInstance(12);
        InfoInstance c = new InfoInstance(10);
        s.addInstance("i",c);
        s.addInstance("i",b);
        s.addInstance("i",a);
        s.removeAllInstanceFromStack(i);
        Assert.assertNull(i.getInstance());
        Assert.assertNull(s.getTopOfStack());

    }
    @Test
    public void testremoveOne() throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        InfoIdent i = new InfoIdent("i",Type.ENTIER,Sorte.VAR);
        s.add(i);
        InfoInstance a = new InfoInstance(15);
        s.addInstance("i",a);
        s.removeFromStack(a);
        Assert.assertNull(s.getTopOfStack());
    }
    @Test
    public void testSwapTwoElements(){
        SymbolTable s = new SymbolTable();
        InfoInstance a = new InfoInstance(10);
        InfoInstance b = new InfoInstance(15);
        s.addToStack(a);
        s.addToStack(b);
        Assert.assertEquals(b,s.getTopOfStack());
        s.swap();
        Assert.assertEquals(a,s.getTopOfStack());
        Assert.assertEquals(10,s.getTopOfStack().getValue());
        Assert.assertEquals(15,s.getTopOfStack().getSuivantPile().getValue());
    }
    @Test
    public void testSwapMoreThanTwo(){
        SymbolTable s = new SymbolTable();
        InfoInstance a = new InfoInstance(10);
        InfoInstance b = new InfoInstance(15);
        InfoInstance c = new InfoInstance(20);
        s.addToStack(a);
        s.addToStack(b);
        s.addToStack(c);
        Assert.assertEquals(c,s.getTopOfStack());
        s.swap();
        InfoInstance top = s.getTopOfStack();
        InfoInstance belowTop = top.getSuivantPile();
        InfoInstance belowBelow = belowTop.getSuivantPile();
        Assert.assertEquals(b,top);
        Assert.assertEquals(15,top.getValue());
        Assert.assertEquals(20,belowTop.getValue());
        Assert.assertEquals(10,belowBelow.getValue());
        Assert.assertEquals(belowTop,belowBelow.getPrecedantPile());
        Assert.assertEquals(belowBelow,belowTop.getSuivantPile());
        Assert.assertEquals(top,belowTop.getPrecedantPile());
        Assert.assertEquals(belowTop,top.getSuivantPile());

    }
    @Test
    public void testLink() throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        InfoIdent a = new InfoIdent("a",Type.ENTIER,Sorte.VAR);
        s.add(a);
        InfoInstance b = new InfoInstance(10);
        InfoInstance c = new InfoInstance(15);
        InfoInstance d = new InfoInstance(20);
        s.addInstance("a",d);
        s.addInstance("a",c);
        s.addInstance("a",b);
        Assert.assertEquals(a.getInstance(),b);
        s.removeFromStack(b);
        Assert.assertEquals(a.getInstance(),c);



    }
    @Test
    public void testLinkv2() throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        SymbolTable s = new SymbolTable();
        InfoIdent a = new InfoIdent("a",Type.ENTIER,Sorte.VAR);
        s.add(a);
        InfoInstance b = new InfoInstance(10);
        InfoInstance c = new InfoInstance(15);
        InfoInstance d = new InfoInstance(20);
        s.addInstance("a",d);
        s.addInstance("a",c);
        s.addInstance("a",b);
        Assert.assertEquals(a.getInstance(),b);
        s.removeFromStack(c);
        Assert.assertEquals(a.getInstance(),b);
        Assert.assertEquals(b.getValeurPrecedente(),d);
    }
    @Test
    public void testgetNthElement() throws SymbolTableException.StackOverflowException {
        SymbolTable s = new SymbolTable();
        InfoInstance a = new InfoInstance(5);
        InfoInstance b = new InfoInstance(10);
        InfoInstance c = new InfoInstance(15);
        InfoInstance d = new InfoInstance(20);
        s.addToStack(a);
        s.addToStack(b);
        s.addToStack(c);
        s.addToStack(d);
        Assert.assertEquals(s.getTopOfStack(),d);
        Assert.assertEquals(s.getNthInstance(0),d);
        Assert.assertEquals(s.getNthInstance(1),c);
        Assert.assertEquals(s.getNthInstance(2),b);
        Assert.assertEquals(s.getNthInstance(3),a);
    }
    @Test( expected = SymbolTableException.StackOverflowException.class)
    public void testNElementOverflow() throws SymbolTableException.StackOverflowException {
        SymbolTable s = new SymbolTable();
        InfoInstance a = new InfoInstance(5);
        InfoInstance b = new InfoInstance(10);
        InfoInstance c = new InfoInstance(15);
        InfoInstance d = new InfoInstance(20);
        s.addToStack(a);
        s.addToStack(b);
        s.addToStack(c);
        s.addToStack(d);
        s.getNthInstance(4);
    }
    @Test( expected = SymbolTableException.StackOverflowException.class)
    public void testNElementEmptyStack() throws SymbolTableException.StackOverflowException {
        SymbolTable s = new SymbolTable();
        s.getNthInstance(0);
    }
}

