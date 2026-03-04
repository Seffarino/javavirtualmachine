package fr.ufrst.m1info.comp5.memory;

import fr.ufrst.m1info.comp5.memory.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class InfoIdentTest {
    @Test(expected = MemoryException.class)
    public void testConstructorEmptyString() throws MemoryException {
        InfoIdent i = new InfoIdent("", Type.ENTIER, Sorte.VAR);
    }
    @Test
    public void testConstructor() throws MemoryException {
        InfoIdent i = new InfoIdent("test", Type.ENTIER, Sorte.VAR);
        Assert.assertNull(i.getInstance());
        i.addInstance(new InfoInstance(1));
        Assert.assertNotNull(i.getInstance());
    }
    @Test
    public void testEquals() throws MemoryException {
        InfoIdent i = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        InfoIdent j = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        InfoIdent k = new InfoIdent("test",Type.ENTIER,Sorte.METH);
        InfoIdent l = new InfoIdent("test",Type.BOOL,Sorte.CST);
        Assert.assertEquals(i, j);
        Assert.assertNotEquals(i,k);
        Assert.assertEquals(i,l);
    }
    @Test
    public void testReturnLast() throws MemoryException {
        InfoIdent i = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        InfoIdent k = new InfoIdent("tesaat",Type.ENTIER,Sorte.METH);
        InfoIdent l = new InfoIdent("t",Type.BOOL,Sorte.CST);
        i.setNext(k);
        k.setNext(l);
        Assert.assertEquals(i.returnLast(),l);
    }
    @Test
    public void testListOfNext() throws MemoryException {
        InfoIdent i = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        InfoIdent k = new InfoIdent("tesaat",Type.ENTIER,Sorte.METH);
        InfoIdent l = new InfoIdent("t",Type.BOOL,Sorte.CST);
        i.setNext(k);
        k.setNext(l);
        List<InfoIdent> myList = i.getListOfNext();
        Assert.assertEquals(3,myList.size());
        Assert.assertEquals(i,myList.get(0));
        Assert.assertEquals(k,myList.get(1));
        Assert.assertEquals(l,myList.get(2));
    }
    @Test
    public void testHandlerInstanceNoExisting() throws MemoryException {
        InfoIdent i = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        InfoInstance a = new InfoInstance(10);
        i.handleNoInstance(a);
        Assert.assertEquals(i.getInstance(),a);
        Assert.assertEquals(10,i.getInstance().getValue());
    }
    @Test
    public void testHandlerObjectNoExisting() throws MemoryException {
        InfoIdent i = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        i.handleNoInstance(10);
        Assert.assertEquals(i.getInstance().getValue(),10);
    }
    @Test
    public void testHandlerInstanceExisting() throws MemoryException {
        InfoIdent i = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        InfoInstance a = new InfoInstance(10);
        i.handleNoInstance(a);
        Assert.assertEquals(i.getInstance(),a);
        Assert.assertEquals(a.getValue(),i.getInstance().getValue());
        InfoInstance b = new InfoInstance(20);
        i.handleExistingInstance(b);
        Assert.assertEquals(i.getInstance(),b);
        Assert.assertEquals(b.getValue(),i.getInstance().getValue());
    }
    @Test
    public void testHandlerObjectExisting() throws MemoryException{
        InfoIdent i = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        i.handleNoInstance(10);
        Assert.assertEquals(i.getInstance().getValue(),10);
        i.handleExistingInstance(20);
        Assert.assertEquals(i.getInstance().getValue(),20);
    }
    @Test
    public void testHandlerInstanceNoExistingWrongCall() throws MemoryException {
        InfoIdent i = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        InfoInstance a = new InfoInstance(10);
        i.handleExistingInstance(a);
        Assert.assertEquals(i.getInstance(),a);
        Assert.assertEquals(10,i.getInstance().getValue());
    }
    @Test
    public void testHandlerObjectNoExistingWrongCall() throws MemoryException {
        InfoIdent i = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        i.handleExistingInstance(10);
        Assert.assertEquals(i.getInstance().getValue(),10);
    }
    @Test
    public void testHandlerInstanceExistingWrongCall() throws MemoryException {
        InfoIdent i = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        InfoInstance a = new InfoInstance(10);
        i.handleNoInstance(a);
        Assert.assertEquals(i.getInstance(),a);
        Assert.assertEquals(a.getValue(),i.getInstance().getValue());
        InfoInstance b = new InfoInstance(20);
        i.handleNoInstance(b);
        Assert.assertEquals(i.getInstance(),b);
        Assert.assertEquals(b.getValue(),i.getInstance().getValue());
    }
    @Test
    public void testHandlerObjectExistingWrongCall() throws MemoryException{
        InfoIdent i = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        i.handleNoInstance(10);
        Assert.assertEquals(i.getInstance().getValue(),10);
        i.handleNoInstance(20);
        Assert.assertEquals(i.getInstance().getValue(),20);
    }

    @Test
    public void testListOfInstance() throws MemoryException {
        InfoIdent i = new InfoIdent("test",Type.ENTIER,Sorte.VAR);
        InfoInstance a = new InfoInstance(10);
        InfoInstance b = new InfoInstance(11);
        InfoInstance c = new InfoInstance(12);
        InfoInstance d = new InfoInstance(13);
        i.addInstance(a);
        i.addInstance(b);
        i.addInstance(c);
        i.addInstance(d);
        List<InfoInstance> myList = i.getListOfInstance();
        Assert.assertEquals(4,myList.size());
        Assert.assertEquals(d,myList.get(0));
        Assert.assertEquals(c,myList.get(1));
        Assert.assertEquals(b,myList.get(2));
        Assert.assertEquals(a,myList.get(3));

     }
}
