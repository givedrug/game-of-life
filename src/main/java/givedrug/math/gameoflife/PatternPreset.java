package givedrug.math.gameoflife;

/**
 * 内置图样预设：避免在 GameOfLife 里用注释切换。
 */
public enum PatternPreset {

    GOSPER_GLIDER_GUN(
            "gosperglidergun.cells",
            /* canvasWidth */ 200,
            /* canvasHeight */ 100,
            /* sideLength */  6,
            /* shiftX */      20,
            /* shiftY */      20),

    BREEDER1(
            "breeder1.cells",
            /* canvasWidth */ 1500,
            /* canvasHeight */ 700,
            /* sideLength */  1,
            /* shiftX */      0,
            /* shiftY */      350);

    public final String fileName;
    public final int canvasWidth;
    public final int canvasHeight;
    public final int sideLength;
    public final int shiftX;
    public final int shiftY;

    PatternPreset(String fileName, int canvasWidth, int canvasHeight,
                  int sideLength, int shiftX, int shiftY) {
        this.fileName = fileName;
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;
        this.sideLength = sideLength;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }

    /** 命令行参数解析，如 "gun" / "breeder1" / "GOSPER_GLIDER_GUN"。 */
    public static PatternPreset fromArg(String s, PatternPreset fallback) {
        if (s == null) return fallback;
        String n = s.trim().toUpperCase().replace('-', '_');
        switch (n) {
            case "GUN":
            case "GOSPER":
            case "GOSPER_GLIDER_GUN":
                return GOSPER_GLIDER_GUN;
            case "BREEDER":
            case "BREEDER1":
                return BREEDER1;
            default:
                try { return PatternPreset.valueOf(n); }
                catch (IllegalArgumentException e) { return fallback; }
        }
    }
}
