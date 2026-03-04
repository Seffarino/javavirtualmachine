package fr.ufrst.m1info.comp5.memory;

import fr.ufrst.m1info.comp5.memory.InfoInstance;
import org.junit.Assert;
import org.junit.Test;

public class InfoInstanceTest {
    @Test
    public void testInstance(){
        InfoInstance a = new InfoInstance(15);
        InfoInstance b = new InfoInstance(12);
        a.setSuivantPile(b);
        Assert.assertEquals(a.getSuivantPile(),b);
    }
}
