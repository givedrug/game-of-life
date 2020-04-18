package givedrug.math.gameoflife;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameOfLife extends Application {
    int generation = 0;

    //从左上角，向右为x正方向，向下为y正方向
    //gosperglidergun.cells
//    String patternFile = "gosperglidergun.cells";
//    int boundx = 36;
//    int boundy = 9;
//    boolean[][] map;
//    int width = 100;
//    int heigth = 50;
//    int sideLength = 10;
//    int shiftx = 10;
//    int shifty = 5;
//    boolean[][] bigMap;

    //breeder1.cells
    String patternFile = "breeder1.cells";
    int boundx = 749;
    int boundy = 338;
    boolean[][] map;
    int width = 1500;
    int heigth = 700;
    int sideLength = 1;
    int shiftx = 0;
    int shifty = 350;
    boolean[][] bigMap;

    @Override
    public void init() throws Exception {
        super.init();
        map = new LoadPatternFile().getPatternMap(patternFile, boundx, boundy);

        bigMap = new boolean[width][heigth];

        for (int i = 0; i < boundx; i++) {
            for (int j = 0; j < boundy; j++) {
                if (map[j][i]) {
                    bigMap[i + shiftx][j + shifty] = true;
                }
            }
        }

    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("game-of-life");

        Group root = new Group();
        Rectangle[][] rs = generateGrid(width, heigth, sideLength);

        painting(rs, bigMap);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < heigth; j++) {
                root.getChildren().add(rs[i][j]);
            }
        }

        Scene scene = new Scene(root, width * sideLength, heigth * sideLength, Color.WHITE);
        primaryStage.setScene(scene);
        primaryStage.show();

        EventHandler<ActionEvent> eventHandler = e -> {
            long time = System.currentTimeMillis();
            nextStep(bigMap);
            painting(rs, bigMap);
            System.out.println("generation:"+(++generation) + ",time" + (System.currentTimeMillis() - time));
        };

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(200), eventHandler));
        animation.setCycleCount(Timeline.INDEFINITE);
        animation.play();
    }

    private void nextStep(boolean[][] map) {

        int[][] count = new int[width][heigth];

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < heigth; j++) {
                int sum = 0;
                if (i - 1 >= 0 && map[i - 1][j]) sum++;
                if (i + 1 <= width - 1 && map[i + 1][j]) sum++;
                if (j - 1 >= 0 && map[i][j - 1]) sum++;
                if (j + 1 <= heigth - 1 && map[i][j + 1]) sum++;
                if (i - 1 >= 0 && j - 1 >= 0 && map[i - 1][j - 1]) sum++;
                if (i + 1 <= width - 1 && j + 1 <= heigth - 1 && map[i + 1][j + 1]) sum++;
                if (i - 1 >= 0 && j + 1 <= heigth - 1 && map[i - 1][j + 1]) sum++;
                if (i + 1 <= width - 1 && j - 1 >= 0 && map[i + 1][j - 1]) sum++;
                count[i][j] = sum;
            }
        }

        for (int i = 0; i < count.length; i++) {
            for (int j = 0; j < count[i].length; j++) {
                if (map[i][j] && (count[i][j] < 2 || count[i][j] > 3)) map[i][j] = false;
                if (!map[i][j] && count[i][j] == 3) map[i][j] = true;
            }
        }

    }

    private void painting(Rectangle[][] rs, boolean[][] map) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < heigth; j++) {
                if (map[i][j]) {
                    rs[i][j].setFill(Color.BLACK);
                } else {
                    rs[i][j].setFill(Color.WHITE);
                }
            }
        }

    }

    public Rectangle[][] generateGrid(int width, int height, int sideLength) {
        Rectangle[][] rs = new Rectangle[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Rectangle r = new Rectangle();
                r.setX(i * sideLength);
                r.setY(j * sideLength);
                r.setWidth(sideLength);
                r.setHeight(sideLength);
                r.setFill(Color.WHITE);
                rs[i][j] = r;
            }
        }

        return rs;
    }

}
