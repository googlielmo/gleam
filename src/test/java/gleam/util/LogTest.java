package gleam.util;

import org.junit.Test;

import static gleam.util.Log.Level.*;
import static org.junit.Assert.assertEquals;

public class LogTest {

    @Test
    public void getLevel0() {
        // given
        Log.setLevel(0);
        // expected
        assertEquals(0, Log.getLevelValue());
    }

    @Test
    public void getLevel1() {
        // given
        Log.setLevel(1);
        // expected
        assertEquals(1, Log.getLevelValue());
    }

    @Test
    public void getLevel2() {
        // given
        Log.setLevel(2);
        // expected
        assertEquals(2, Log.getLevelValue());
    }

    @Test
    public void getLevel3() {
        // given
        Log.setLevel(3);
        // expected
        assertEquals(3, Log.getLevelValue());
    }

    @Test
    public void getLevel4() {
        // given
        Log.setLevel(4);
        // expected
        assertEquals(4, Log.getLevelValue());
    }

    @Test
    public void getLevel5() {
        // given
        Log.setLevel(5);
        // expected
        assertEquals(5, Log.getLevelValue());
    }

    @Test
    public void getLevel6() {
        // given
        Log.setLevel(6);
        // expected
        assertEquals(6, Log.getLevelValue());
    }

    @Test
    public void t1() {
        Log.setLevel(2);
        Log.enter(1, "1");
        Log.enter(2, "2");
        Log.enter(3, "3");
        Log.enter(4, "4");
        Log.enter(5, "5");
    }

    @Test
    public void t2() {
        Log.setLevel(2);
        Log.enter(FINE, "1");
        Log.enter(CONFIG, "2");
        Log.enter(INFO, "3");
        Log.enter(WARNING, "4");
        Log.enter(ERROR, "5");
    }
}
