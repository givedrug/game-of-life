package givedrug.math.gameoflife;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class LoadPatternFileTest {

    @Test
    public void testLoadGosperGliderGun() throws Exception {
        Pattern p = new LoadPatternFile().load("gosperglidergun.cells");

        // Gosper glider gun 占用 36 列 × 9 行
        assertEquals(p.getWidth(), 36, "width");
        assertEquals(p.getHeight(), 9, "height");

        // 它是一个有 36 个活细胞的著名图样
        assertEquals(p.getAliveCells().length, 36, "alive cells");

        // 抽样校验：第 0 行第 24 列应为活（O）
        boolean found = false;
        for (long k : p.getAliveCells()) {
            if (Pattern.decodeX(k) == 24 && Pattern.decodeY(k) == 0) { found = true; break; }
        }
        assertTrue(found, "expected (24,0) alive in gosper gun");
    }

    @Test
    public void testStepStillLifeBlock() {
        // 2x2 block 是稳定图样：演化后不变
        GameEngine eng = new GameEngine(10, 10);
        eng.setAlive(2, 2, true);
        eng.setAlive(3, 2, true);
        eng.setAlive(2, 3, true);
        eng.setAlive(3, 3, true);

        eng.step();

        assertEquals(eng.aliveCount(), 4);
        assertTrue(eng.isAlive(2, 2));
        assertTrue(eng.isAlive(3, 3));
    }

    @Test
    public void testStepBlinker() {
        // 水平 3 格振荡器：1 步后变竖直
        GameEngine eng = new GameEngine(10, 10);
        eng.setAlive(2, 3, true);
        eng.setAlive(3, 3, true);
        eng.setAlive(4, 3, true);

        eng.step();

        assertEquals(eng.aliveCount(), 3);
        assertTrue(eng.isAlive(3, 2));
        assertTrue(eng.isAlive(3, 3));
        assertTrue(eng.isAlive(3, 4));
    }
}
