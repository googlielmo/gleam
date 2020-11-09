package gleam.util;

import org.junit.Test;

import static gleam.util.Logger.Level.*;
import static org.junit.Assert.assertEquals;

public class LoggerTest {

    @Test
    public void getLevel0() {
        // given
        Logger.setLevel(0);
        // expected
        assertEquals(0, Logger.getLevelValue());
    }

    @Test
    public void getLevel1() {
        // given
        Logger.setLevel(1);
        // expected
        assertEquals(1, Logger.getLevelValue());
    }

    @Test
    public void getLevel2() {
        // given
        Logger.setLevel(2);
        // expected
        assertEquals(2, Logger.getLevelValue());
    }

    @Test
    public void getLevel3() {
        // given
        Logger.setLevel(3);
        // expected
        assertEquals(3, Logger.getLevelValue());
    }

    @Test
    public void getLevel4() {
        // given
        Logger.setLevel(4);
        // expected
        assertEquals(4, Logger.getLevelValue());
    }

    @Test
    public void getLevel5() {
        // given
        Logger.setLevel(5);
        // expected
        assertEquals(5, Logger.getLevelValue());
    }

    @Test
    public void getLevel6() {
        // given
        Logger.setLevel(6);
        // expected
        assertEquals(6, Logger.getLevelValue());
    }

    @Test
    public void t1() {
        Logger.setLevel(2);
        Logger.enter(1, "1");
        Logger.enter(2, "2");
        Logger.enter(3, "3");
        Logger.enter(4, "4");
        Logger.enter(5, "5");
    }

    @Test
    public void t2() {
        Logger.setLevel(2);
        Logger.enter(FINE, "1");
        Logger.enter(CONFIG, "2");
        Logger.enter(INFO, "3");
        Logger.enter(WARNING, "4");
        Logger.enter(ERROR, "5");
    }
}
