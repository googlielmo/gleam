package gleam.util;

import org.junit.Test;

import static gleam.util.Log.Level.*;
import static org.junit.Assert.*;

public class LogTest {

    @Test
    public void getLevel0() {
        // given
        Log.setLevel(0);
        // expected
        assertEquals(0, Log.getLevel());
    }

    @Test
    public void getLevel1() {
        // given
        Log.setLevel(1);
        // expected
        assertEquals(1, Log.getLevel());
    }

    @Test
    public void getLevel2() {
        // given
        Log.setLevel(2);
        // expected
        assertEquals(2, Log.getLevel());
    }

    @Test
    public void getLevel3() {
        // given
        Log.setLevel(3);
        // expected
        assertEquals(3, Log.getLevel());
    }

    @Test
    public void getLevel4() {
        // given
        Log.setLevel(4);
        // expected
        assertEquals(4, Log.getLevel());
    }

    @Test
    public void getLevel5() {
        // given
        Log.setLevel(5);
        // expected
        assertEquals(5, Log.getLevel());
    }

    @Test
    public void getLevel6() {
        // given
        Log.setLevel(6);
        // expected
        assertEquals(6, Log.getLevel());
    }

    @Test
    public void t1() {
        Log.setLevel(2);
        Log.record(1, "1");
        Log.record(2, "2");
        Log.record(3, "3");
        Log.record(4, "4");
        Log.record(5, "5");
    }

    @Test
    public void t2() {
        Log.setLevel(2);
        Log.record(FINE, "1");
        Log.record(CONFIG, "2");
        Log.record(INFO, "3");
        Log.record(WARNING, "4");
        Log.record(SEVERE, "5");
    }
}
