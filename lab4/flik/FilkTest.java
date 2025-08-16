package flik;

import org.junit.Test;

import static org.junit.Assert.*;

public class FilkTest {
    @Test
    public void isSameNumberTest() {
        assertTrue(Flik.isSameNumber(2, 2));
        assertFalse(Flik.isSameNumber(3, 4));
    }

    @Test
    public void is64SameNumberTest() {
        boolean is64Same = Flik.isSameNumber(64, 64);
        System.out.println(is64Same);
        assertTrue(is64Same);
    }

    @Test
    public void is128SameNumberTest() {
        boolean is128Same = Flik.isSameNumber(128, 128);
        System.out.println(is128Same);
        assertTrue(is128Same);
    }
}
