package fr.ufrst.m1info.comp5.memory;

import fr.ufrst.m1info.comp5.memory.Memory;
import fr.ufrst.m1info.comp5.memory.Tas;
import org.junit.Assert;
import org.junit.Test;

public class MemoryTest {
    @Test
    public void testPushPop() throws MemoryException, SymbolTableException.UnknownSymbolException, TasException {
        Memory m = new Memory();
        InfoInstance a = new InfoInstance(10);
        InfoInstance b = new InfoInstance(15);

        m.push(a);
        m.push(b);
        Object c = m.pop();
        Object d = m.pop();
        Assert.assertEquals(c,b.getValue());
        Assert.assertEquals(a.getValue(),d);
    }
    @Test
    public void testPushVersions() throws MemoryException, SymbolTableException.UnknownSymbolException, TasException {
        Memory m = new Memory();
        InfoInstance a = new InfoInstance(10);
        InfoInstance b = new InfoInstance(15);

        m.push(a);
        m.push(15);
        m.push(null);
        Object c = m.pop();
        Object d = m.pop();
        Assert.assertEquals(c,b.getValue());
        Assert.assertEquals(a.getValue(),d);
    }
    @Test
    public void testIdentVal() throws SymbolTableException.UnknownSymbolException, MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.StackOverflowException, TasException {
        Memory m = new Memory();
        InfoInstance a = new InfoInstance(10);
        InfoInstance b = new InfoInstance(15);
        m.push(a);
        m.identVal("a",Type.ENTIER,0);
        Assert.assertEquals(m.getSymbolTable().get("a",false).getInstance(),a);
    }
    @Test
    public void testAffecterVal() throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException, TasException {
        Memory m = new Memory();
        InfoIdent b = new InfoIdent("b",Type.ENTIER,Sorte.CST);
        InfoIdent c = new InfoIdent("c",Type.ENTIER,Sorte.VAR);
        m.getSymbolTable().add(b);
        m.getSymbolTable().add(c);
        m.affecterVal("c",12);
        Assert.assertEquals(12, m.getSymbolTable().get("c", false).getInstance().getValue());
        Assert.assertEquals(12,m.getSymbolTable().getTopOfStack().getValue());

    }
    @Test
    public void testvcst() throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException, TasException {
        Memory m = new Memory();
        m.declCst("mycst","w",Type.ENTIER);
        InfoIdent a = m.getSymbolTable().get("mycst",false);
        Assert.assertEquals(Sorte.VCST, a.getSorte());
        Assert.assertNotNull(a.getInstance());
        m.affecterVal("mycst",12);
        Assert.assertEquals(Sorte.CST, a.getSorte());
        Assert.assertEquals(12,a.getInstance().getValue());


    }
    @Test( expected = SymbolTableException.UnknownSymbolException.class)
    public void testPopWithIDent() throws MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException, TasException {
        Memory m = new Memory();
        m.declVar("a",10,Type.ENTIER);
        m.pop();
        m.getSymbolTable().get("a",false);
    }
    @Test
    public void testDeclTab() throws TasException, MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        Memory m = new Memory();
        m.declTab("mytab",4,Type.ENTIER);
        Tas t = m.getTas();
        Assert.assertEquals(508,t.amountOfFreeSlots());
        m.affecterValT("mytab",0,10);
        m.affecterValT("mytab",1,12);
        m.affecterValT("mytab",2,14);
        m.affecterValT("mytab",3,16);
        Assert.assertEquals(10,m.valT("mytab",0));
        Assert.assertEquals(12,m.valT("mytab",1));
        Assert.assertEquals(14,m.valT("mytab",2));
        Assert.assertEquals(16,m.valT("mytab",3));
    }
    @Test (expected = IndexOutOfBoundsException.class)
    public void testOutOfBound() throws TasException, MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        Memory m = new Memory();
        m.declTab("mytab",4,Type.ENTIER);
        Tas t = m.getTas();
        m.affecterValT("mytab",0,10);
        m.affecterValT("mytab",1,12);
        m.affecterValT("mytab",2,14);
        m.affecterValT("mytab",3,16);
        m.valT("mytab",4);
    }
    @Test
    public void testVal() throws TasException, MemoryException, SymbolTableException.ExistingSymbolException, SymbolTableException.UnknownSymbolException {
        Memory m = new Memory();
        m.declVar("a",12,Type.ENTIER);
        Assert.assertEquals(12,m.val("a"));
    }
}
