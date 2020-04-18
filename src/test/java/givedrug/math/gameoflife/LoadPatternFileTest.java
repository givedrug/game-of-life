package givedrug.math.gameoflife;

import org.testng.annotations.Test;

public class LoadPatternFileTest {

    @Test
    public void testGetPatternMap() {

        new LoadPatternFile().getPatternMap("gosperglidergun.cells",9,36);

    }
}