package givedrug.math.gameoflife;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 用"活细胞坐标 Set"的稀疏算法演化生命游戏。
 *
 * 复杂度：每代约 O(A) —— A 是活细胞数。比起稠密的 O(W*H) 在稀疏图样下能快几个数量级。
 *
 * 边界：硬边界（出界视为死）。如果未来要环面拓扑可加 wrap 选项。
 *
 * 线程安全：非线程安全；调用方应在 JavaFX 应用线程上单线程使用。
 */
public final class GameEngine {

    private final int width;
    private final int height;
    private Set<Long> alive;
    private long generation;

    private static final int[] DX = {-1, -1, -1, 0, 0, 1, 1, 1};
    private static final int[] DY = {-1, 0, 1, -1, 1, -1, 0, 1};

    public GameEngine(int width, int height) {
        this.width = width;
        this.height = height;
        this.alive = new HashSet<>();
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public long getGeneration() { return generation; }
    public int aliveCount() { return alive.size(); }
    public Set<Long> aliveCells() { return alive; }

    public boolean isAlive(int x, int y) {
        return alive.contains(Pattern.encode(x, y));
    }

    public void setAlive(int x, int y, boolean state) {
        if (x < 0 || x >= width || y < 0 || y >= height) return;
        long key = Pattern.encode(x, y);
        if (state) alive.add(key); else alive.remove(key);
    }

    public void clear() {
        alive.clear();
        generation = 0;
    }

    /** 把图样按偏移加载到画布上；超出画布的部分会被裁剪。 */
    public void loadPattern(Pattern pattern, int shiftX, int shiftY) {
        for (long key : pattern.getAliveCells()) {
            int x = Pattern.decodeX(key) + shiftX;
            int y = Pattern.decodeY(key) + shiftY;
            if (x >= 0 && x < width && y >= 0 && y < height) {
                alive.add(Pattern.encode(x, y));
            }
        }
    }

    /** 演进一代。返回本帧"发生变化"的格子坐标集合（用于增量重绘）。 */
    public Set<Long> step() {
        // 邻居计数
        Map<Long, Integer> neighborCount = new HashMap<>(alive.size() * 4);
        for (long key : alive) {
            int x = Pattern.decodeX(key);
            int y = Pattern.decodeY(key);
            for (int d = 0; d < 8; d++) {
                int nx = x + DX[d];
                int ny = y + DY[d];
                if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                long nk = Pattern.encode(nx, ny);
                neighborCount.merge(nk, 1, Integer::sum);
            }
        }

        Set<Long> next = new HashSet<>(Math.max(16, alive.size()));
        Set<Long> changed = new HashSet<>();

        // 1. 死格子有 3 个活邻居 → 复活
        for (Map.Entry<Long, Integer> e : neighborCount.entrySet()) {
            long key = e.getKey();
            int n = e.getValue();
            if (alive.contains(key)) {
                if (n == 2 || n == 3) next.add(key);
                else changed.add(key); // 由活变死
            } else if (n == 3) {
                next.add(key);
                changed.add(key); // 由死变活
            }
        }

        // 2. 之前活的、邻居数为 0（不在 neighborCount 里）→ 死
        for (long key : alive) {
            if (!neighborCount.containsKey(key)) changed.add(key);
        }

        // 3. 新活但旧也活的（state 没变）—— 上面循环没加进 changed
        // 4. 新活但旧不活的——已加
        // 实际上 changed 现在含义是"状态翻转的格子"，正好用于增量重绘

        alive = next;
        generation++;
        return changed;
    }
}
