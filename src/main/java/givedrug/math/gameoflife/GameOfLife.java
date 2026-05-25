package givedrug.math.gameoflife;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.Set;

/**
 * Conway's Game of Life — JavaFX 实现（重构版）。
 *
 * 主要改进：
 *  - 渲染：单 Canvas + GraphicsContext.fillRect，替换原先 105 万 Rectangle 节点
 *  - 算法：稀疏活细胞 Set，每代复杂度 ~O(A)
 *  - 加载：自动测尺寸，命令行 / 默认枚举切换图样
 *  - 控制：Space 暂停/继续 · S 单步 · ↑↓ 调速 · R 重置 · C 清屏 · 鼠标点击切换格子
 *
 * 命令行：mvn javafx:run -Djavafx.args="breeder1"  或  "gun"
 */
public class GameOfLife extends Application {

    private GameEngine engine;
    private PatternPreset preset;
    private Canvas canvas;
    private GraphicsContext gc;
    private Stage stage;

    private boolean paused = false;
    private long stepIntervalNanos = 50_000_000L; // 50ms
    private long lastStepNanos = 0;

    private static final Color ALIVE_COLOR = Color.BLACK;
    private static final Color DEAD_COLOR = Color.WHITE;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;

        List<String> args = getParameters().getRaw();
        String arg = args.isEmpty() ? null : args.get(0);
        this.preset = PatternPreset.fromArg(arg, PatternPreset.BREEDER1);

        loadPreset(preset);

        BorderPane root = new BorderPane();
        canvas = new Canvas(
                preset.canvasWidth * (double) preset.sideLength,
                preset.canvasHeight * (double) preset.sideLength);
        gc = canvas.getGraphicsContext2D();
        root.setCenter(canvas);

        // 鼠标点击：切换格子状态（暂停时尤其有用）
        canvas.setOnMousePressed(e -> {
            int x = (int) (e.getX() / preset.sideLength);
            int y = (int) (e.getY() / preset.sideLength);
            engine.setAlive(x, y, !engine.isAlive(x, y));
            redrawCell(x, y);
        });

        Scene scene = new Scene(root, canvas.getWidth(), canvas.getHeight(), Color.WHITE);

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case SPACE:
                    paused = !paused;
                    updateTitle();
                    break;
                case S:
                    if (paused) doStep();
                    break;
                case UP:
                    stepIntervalNanos = Math.max(8_000_000L, stepIntervalNanos / 2);
                    updateTitle();
                    break;
                case DOWN:
                    stepIntervalNanos = Math.min(2_000_000_000L, stepIntervalNanos * 2);
                    updateTitle();
                    break;
                case R:
                    engine.clear();
                    loadPreset(preset);
                    fullRedraw();
                    break;
                case C:
                    engine.clear();
                    fullRedraw();
                    break;
                default:
            }
        });

        primaryStage.setScene(scene);
        updateTitle();
        primaryStage.show();
        canvas.requestFocus();

        fullRedraw();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (paused) return;
                if (now - lastStepNanos < stepIntervalNanos) return;
                lastStepNanos = now;
                doStep();
            }
        }.start();
    }

    private void loadPreset(PatternPreset p) {
        engine = new GameEngine(p.canvasWidth, p.canvasHeight);
        try {
            Pattern pattern = new LoadPatternFile().load(p.fileName);
            engine.loadPattern(pattern, p.shiftX, p.shiftY);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void doStep() {
        long t = System.currentTimeMillis();
        Set<Long> changed = engine.step();
        // 增量重绘
        for (long key : changed) {
            int x = Pattern.decodeX(key);
            int y = Pattern.decodeY(key);
            redrawCell(x, y);
        }
        long elapsed = System.currentTimeMillis() - t;
        if (engine.getGeneration() % 30 == 0) {
            System.out.printf("gen=%d alive=%d step=%dms changed=%d%n",
                    engine.getGeneration(), engine.aliveCount(), elapsed, changed.size());
        }
        Platform.runLater(this::updateTitle);
    }

    private void fullRedraw() {
        gc.setFill(DEAD_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(ALIVE_COLOR);
        int s = preset.sideLength;
        for (long key : engine.aliveCells()) {
            int x = Pattern.decodeX(key);
            int y = Pattern.decodeY(key);
            gc.fillRect(x * (double) s, y * (double) s, s, s);
        }
    }

    private void redrawCell(int x, int y) {
        int s = preset.sideLength;
        gc.setFill(engine.isAlive(x, y) ? ALIVE_COLOR : DEAD_COLOR);
        gc.fillRect(x * (double) s, y * (double) s, s, s);
    }

    private void updateTitle() {
        if (stage == null) return;
        double fps = 1_000_000_000.0 / stepIntervalNanos;
        stage.setTitle(String.format(
                "game-of-life · %s · gen=%d · alive=%d · %.1f gen/s%s",
                preset.name(), engine.getGeneration(), engine.aliveCount(),
                fps, paused ? " · [PAUSED]" : ""));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
