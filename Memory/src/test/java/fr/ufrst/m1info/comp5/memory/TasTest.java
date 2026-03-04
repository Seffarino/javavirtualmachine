package fr.ufrst.m1info.comp5.memory;

import fr.ufrst.m1info.comp5.memory.Tas;
import org.junit.Assert;
import org.junit.Test;

public class TasTest {
    @Test
    public void simpleHeapTest() throws TasException {
        Tas heap = new Tas();

    }
    @Test
    public void logTest() throws TasException {
        Tas t = new Tas();
        int a = t.logN(7,2);
        int b = t.logN(8,2);
        int c = t.logN(9,2);
        Assert.assertEquals(a,b);
        Assert.assertNotEquals(b,c);
        Assert.assertEquals(3,b);
        int d = t.logN(15,2);
        int e = t.logN(16,2);
        int f = t.logN(17,2);
        Assert.assertEquals(c,d);
        Assert.assertEquals(d,e);
        Assert.assertNotEquals(e,f);
        Assert.assertEquals(4,e);
        Assert.assertEquals(5,f);
    }
    @Test
    public void isPowerOfTwoTest() throws TasException {
        Tas t = new Tas();
        boolean a = t.isPowerOfTwo(7);
        boolean b = t.isPowerOfTwo(8);
        boolean c = t.isPowerOfTwo(9);
        Assert.assertTrue(b);
        Assert.assertFalse(a);
        Assert.assertFalse(c);
    }
    @Test
    public void lowerBTest() throws TasException {
        Tas t = new Tas();
        int a = t.getLowerBound(63);
        int b = t.getLowerBound(64);
        int c = t.getLowerBound(65);
        Assert.assertEquals(32,a);
        Assert.assertEquals(b,c);
        Assert.assertEquals(64,b);
    }
    @Test
    public void upperBTest() throws TasException {
        Tas t = new Tas();
        int a = t.getUpperBound(63);
        int b = t.getUpperBound(64);
        int c = t.getUpperBound(65);
        Assert.assertEquals(64,a);
        Assert.assertEquals(b,a);
        Assert.assertEquals(128,c);
    }
    @Test
    public void bestBlockTest() throws TasException{
        Tas t  = new Tas();
        int a = t.getBestBlock(63);
        int b = t.getBestBlock(64);
        int c = t.getBestBlock(65);
        Assert.assertEquals(512,a);
        Assert.assertEquals(b,a);
        Assert.assertEquals(512,c);
    }
    @Test
    public void amountFreeTest() throws TasException {
        Tas t = new Tas();
        int a = t.amountOfFreeSlots();
        Assert.assertEquals(512, a);
    }
    @Test
    public void addBlockTest1() throws TasException {
        Tas t = new Tas();
        t.addBlock(15);
        Assert.assertEquals(497,t.amountOfFreeSlots());
    }
    @Test
    public void addBlockTest2() throws TasException {
        Tas t = new Tas();
        Assert.assertEquals(512,t.amountOfFreeSlots());
        t.addBlock(15);
        Assert.assertEquals(497,t.amountOfFreeSlots());
        t.addBlock(127);
        Assert.assertEquals(370,t.amountOfFreeSlots());


    }
    @Test
    public void addBlockTest3() throws TasException {
        Tas t = new Tas();
        t.addBlock(255);
        Assert.assertEquals(257,t.amountOfFreeSlots());
    }
    @Test
    public void testCours() throws TasException{
        Tas t = new Tas(256);
        Assert.assertEquals(256,t.amountOfFreeSlots());
        t.addBlock(2);
        Assert.assertEquals(254,t.amountOfFreeSlots());
        t.addBlock(11);
        Assert.assertEquals(243,t.amountOfFreeSlots());
    }
    @Test
    public void testSetGetValue() throws TasException {
        Tas t = new Tas();
        Assert.assertEquals(512,t.amountOfFreeSlots());
        t.addBlock(2);
        t.setValueOfArray(0,0,1);
        t.setValueOfArray(0,1,2);
        Assert.assertEquals(1,t.getValueOfArray(0,0));
        Assert.assertEquals(2,t.getValueOfArray(0,1));
    }
    @Test (expected = IndexOutOfBoundsException.class)
    public void testSetNegative() throws TasException {
        Tas t = new Tas();
        t.addBlock(2);
        t.setValueOfArray(0,-1,1);
    }
    @Test (expected = IndexOutOfBoundsException.class)
    public void testSetBigger() throws TasException {
        Tas t = new Tas();
        t.addBlock(2);
        t.setValueOfArray(0,3,1);
    }
    @Test (expected = IndexOutOfBoundsException.class)
    public void testGetNegative() throws TasException {
        Tas t = new Tas();
        t.addBlock(2);
        t.getValueOfArray(0,-1);
    }
    @Test (expected = IndexOutOfBoundsException.class)
    public void testGetBigger() throws TasException {
        Tas t = new Tas();
        t.addBlock(2);
        t.getValueOfArray(0,3);
    }
    @Test ( expected = TasException.class)
    public void testUnknownId() throws TasException {
        Tas t = new Tas();
        t.addBlock(2);
        t.getValueOfArray(2,1);
    }
    @Test
    public void testClearArrayValues() throws TasException {
        Tas t = new Tas();
        Assert.assertEquals(512,t.amountOfFreeSlots());
        t.addBlock(2);
        t.setValueOfArray(0,0,1);
        t.setValueOfArray(0,1,2);
        Assert.assertEquals(1,t.getValueOfArray(0,0));
        Assert.assertEquals(2,t.getValueOfArray(0,1));
        t.clearArrayValues(0);
        Assert.assertNull(t.getValueOfArray(0,0));
        Assert.assertNull(t.getValueOfArray(0,1));
        Assert.assertEquals(512,t.amountOfFreeSlots());
    }
    @Test
    public void testReconstruct() throws TasException {
        Tas t = new Tas(16);
        t.addBlock(4);
        Assert.assertEquals(12,t.amountOfFreeSlots());
        t.addBlock(4);
        Assert.assertEquals(8,t.amountOfFreeSlots());
        t.reconstructHeap();
        Assert.assertEquals(8,t.amountOfFreeSlots());
    }
    @Test
    public void testIncreaseSize() throws TasException {
        Tas t = new Tas();
        int id = t.addBlock(4);
        t.setValueOfArray(id,0,12);
        t.setValueOfArray(id,1,13);
        t.setValueOfArray(id,2,14);
        t.setValueOfArray(id,3,15);
        int id2 = t.addBlock(4);
        t.setValueOfArray(id2,0,16);
        t.setValueOfArray(id2,1,17);
        t.setValueOfArray(id2,2,18);
        t.setValueOfArray(id2,3,19);
        Assert.assertEquals(12,t.getValueOfArray(id,0));
        Assert.assertEquals(13,t.getValueOfArray(id,1));
        Assert.assertEquals(14,t.getValueOfArray(id,2));
        Assert.assertEquals(15,t.getValueOfArray(id,3));
        Assert.assertEquals(16,t.getValueOfArray(id2,0));
        Assert.assertEquals(17,t.getValueOfArray(id2,1));
        Assert.assertEquals(18,t.getValueOfArray(id2,2));
        Assert.assertEquals(19,t.getValueOfArray(id2,3));
        t.increaseSize();
        Assert.assertEquals(1016,t.amountOfFreeSlots());
        Assert.assertEquals(12,t.getValueOfArray(id,0));
        Assert.assertEquals(12,t.getValueOfArray(id,0));
        Assert.assertEquals(13,t.getValueOfArray(id,1));
        Assert.assertEquals(14,t.getValueOfArray(id,2));
        Assert.assertEquals(15,t.getValueOfArray(id,3));
        Assert.assertEquals(16,t.getValueOfArray(id2,0));
        Assert.assertEquals(17,t.getValueOfArray(id2,1));
        Assert.assertEquals(18,t.getValueOfArray(id2,2));
        Assert.assertEquals(19,t.getValueOfArray(id2,3));
        System.out.println(t.toString());
    }
    @Test
    public void removeArrayTest() throws TasException {
        Tas t = new Tas();
        int id = t.addBlock(4);
        Assert.assertEquals(508,t.amountOfFreeSlots());
        t.removeArray(id);
        Assert.assertEquals(512,t.amountOfFreeSlots());
        Assert.assertEquals(0,t.getTableOfArrays().size());
    }

}
