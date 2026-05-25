package givedrug.math.gameoflife;

/**
 * 解析后的图样：用稀疏的活细胞坐标列表表示，并附带原始图样的宽高（用于在大画布中居中/对齐）。
 */
public final class Pattern {

    private final int width;
    private final int height;
    private final long[] aliveCells; // 编码后的活细胞坐标 (x << 32) | (y & 0xffffffffL)

    public Pattern(int width, int height, long[] aliveCells) {
        this.width = width;
        this.height = height;
        this.aliveCells = aliveCells;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public long[] getAliveCells() { return aliveCells; }

    public static long encode(int x, int y) {
        return ((long) x << 32) | (y & 0xffffffffL);
    }

    public static int decodeX(long key) { return (int) (key >> 32); }
    public static int decodeY(long key) { return (int) (key & 0xffffffffL); }
}
