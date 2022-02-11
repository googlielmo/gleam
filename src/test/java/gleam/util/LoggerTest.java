package gleam.util;

import org.junit.Before;
import org.junit.Test;

import static gleam.util.Logger.Level.*;
import static org.junit.Assert.assertEquals;

public class LoggerTest {

    Logger logger;

    @Before
    public void setUp() {
        logger = Logger.getLogger();
    }

    @Test
    public void getLevel0() {
        // given
        logger.setLevel(0);
        // expected
        assertEquals(0, logger.getLevelValue());
    }

    @Test
    public void getLevel1() {
        // given
        logger.setLevel(1);
        // expected
        assertEquals(1, logger.getLevelValue());
    }

    @Test
    public void getLevel2() {
        // given
        logger.setLevel(2);
        // expected
        assertEquals(2, logger.getLevelValue());
    }

    @Test
    public void getLevel3() {
        // given
        logger.setLevel(3);
        // expected
        assertEquals(3, logger.getLevelValue());
    }

    @Test
    public void getLevel4() {
        // given
        logger.setLevel(4);
        // expected
        assertEquals(4, logger.getLevelValue());
    }

    @Test
    public void getLevel5() {
        // given
        logger.setLevel(5);
        // expected
        assertEquals(5, logger.getLevelValue());
    }

    @Test
    public void getLevel6() {
        // given
        logger.setLevel(6);
        // expected
        assertEquals(6, logger.getLevelValue());
    }

    @Test
    public void t1() {
        logger.setLevel(2);
        logger.log(1, "1");
        logger.log(2, "2");
        logger.log(3, "3");
        logger.log(4, "4");
        logger.log(5, "5");
    }

    @Test
    public void t2() {
        logger.setLevel(2);
        logger.log(DEBUG, "1");
        logger.log(CONFIG, "2");
        logger.log(INFO, "3");
        logger.log(WARNING, "4");
        logger.log(ERROR, "5");
    }
}
