package PacMan;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {
    ArrayList<ArrayList<WallNode>> wallNodes = new ArrayList<>();
    final int WIDTH_WALLNODES = 10;
    final int HEIGHT_WALLNODES = 10;
    Random random = new Random();

    double stageWidth = 700, stageHeight = 700;
    Group root = new Group();
    Scene s;

    Rectangle background;
    ArrayList<Rectangle> walls;


    @Override
    public void start(Stage primaryStage) throws Exception {
        for (int y = 0; y < HEIGHT_WALLNODES; y++) {
            wallNodes.add(new ArrayList());
            for (int x = 0; x < WIDTH_WALLNODES; x++) {
                wallNodes.get(y).add(new WallNode());
            }
        }
        generateRandomWallsWithNodes();
        background = new Rectangle(0, 0, stageWidth, stageHeight);
        background.setFill(Paint.valueOf("black"));
        root.getChildren().add(background);
        buildMaze();
        //root.getChildren().add(new Rectangle(0, 0, stageWidth, stageHeight));
        primaryStage.setTitle("Pac Man!");
        s = new Scene(root);
        primaryStage.setScene(s);
        primaryStage.show();


        ChangeListener<Number> resizeListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                double oldStageWidth = stageWidth, oldStageHeight = stageHeight;
                stageWidth = primaryStage.getWidth();
                stageHeight = primaryStage.getHeight();

                //background
                background.setWidth(background.getWidth()/oldStageWidth * stageWidth);
                background.setHeight(background.getHeight()/oldStageHeight * stageHeight);

                //maze
                for (Rectangle i: walls) {
                    i.setX(i.getX()/oldStageWidth * stageWidth);
                    i.setY(i.getY());
                }
            }
        };
        primaryStage.heightProperty().addListener(resizeListener);
        primaryStage.widthProperty().addListener(resizeListener);

    }

    public void buildMaze() {

        walls = new ArrayList<>();
        double sectionWidth = stageWidth / ((double)WIDTH_WALLNODES + 0.5);
        double sectionHeight = stageHeight / ((double)HEIGHT_WALLNODES + 0.5);


        for (int y = 0; y < wallNodes.size(); ++y) {
            for (int x = 0; x < wallNodes.get(y).size(); ++x) {
                Rectangle r;
                if (wallNodes.get(y).get(x).wallInDirection[0]) {//up
                    r = new Rectangle((x + 0.5) * sectionWidth, (y - 0.5) * sectionHeight, sectionWidth / 2, sectionHeight * 1.5);
                    r.setFill(Paint.valueOf("green"));
                    walls.add(r);
                }
                if (wallNodes.get(y).get(x).wallInDirection[1]) {//right
                    r = new Rectangle((x + 0.5) * sectionWidth, (y + 0.5) * sectionHeight, sectionWidth * 1.5, sectionHeight / 2);
                    r.setFill(Paint.valueOf("green"));
                    walls.add(r);
                }
                if (wallNodes.get(y).get(x).wallInDirection[2]) {//down
                    r = new Rectangle((x + 0.5) * sectionWidth, (y + 0.5) * sectionHeight, sectionWidth / 2, sectionHeight * 1.5);
                    r.setFill(Paint.valueOf("green"));
                    walls.add(r);
                }
                if (wallNodes.get(y).get(x).wallInDirection[3]) {//left
                    r = new Rectangle((x - 0.5) * sectionWidth, (y + 0.5) * sectionHeight, sectionWidth * 1.5, sectionHeight / 2);
                    r.setFill(Paint.valueOf("green"));
                    walls.add(r);
                }
            }
        }
        root.getChildren().addAll(walls);
    }

    private void generateRandomWallsWithNodes() {
        generateWallsRecursion(0, 0);
        // now delete some walls
        boolean loop = false; // auf true setzn wenn die schleife donoch funktioniert
        int skipXWalls = 3;
        int x = 0, y = 0;
        while (loop) {
            if (wallNodes.get(y).get(x).wallInDirection[0]) {
                if (skipXWalls == 0) {
                    wallNodes.get(y).get(x).wallInDirection[0] = false;
                    skipXWalls = random.nextInt(1, 10);
                }
                y--;
            } else if (wallNodes.get(y).get(x).wallInDirection[1]) {
                if (skipXWalls == 0) {
                    wallNodes.get(y).get(x).wallInDirection[1] = false;
                    skipXWalls = random.nextInt(1, 10);
                }
                x++;
            } else if (wallNodes.get(y).get(x).wallInDirection[2]) {
                if (skipXWalls == 0) {
                    wallNodes.get(y).get(x).wallInDirection[2] = false;
                    skipXWalls = random.nextInt(1, 10);
                }
                y++;
            } else if (wallNodes.get(y).get(x).wallInDirection[3]) {
                if (skipXWalls == 0) {
                    wallNodes.get(y).get(x).wallInDirection[3] = false;
                    skipXWalls = random.nextInt(1, 10);
                }
                x--;
            }

        }
    }

    private void generateWallsRecursion(int x, int y) {
        wallNodes.get(y).get(x).isConnected = true;
        for (int i = 0; i < 20; i++) {
            switch (random.nextInt(0, 4)) {
                case 0:
                    if (x > 0 && !wallNodes.get(y).get(x - 1).isConnected) {

                        wallNodes.get(y).get(x).wallInDirection[3] = true;
                        generateWallsRecursion(x - 1, y);
                        break;
                    }
                case 1:
                    if (x + 1 < WIDTH_WALLNODES && !wallNodes.get(y).get(x + 1).isConnected) {
                        wallNodes.get(y).get(x).wallInDirection[1] = true;
                        generateWallsRecursion(x + 1, y);
                        break;
                    }
                case 2:
                    if (y > 0 && !wallNodes.get(y - 1).get(x).isConnected) {
                        wallNodes.get(y).get(x).wallInDirection[0] = true;
                        generateWallsRecursion(x, y - 1);
                        break;
                    }
                case 3:
                    if (y + 1 < HEIGHT_WALLNODES && !wallNodes.get(y + 1).get(x).isConnected) {
                        wallNodes.get(y).get(x).wallInDirection[2] = true;
                        generateWallsRecursion(x, y + 1);
                        break;
                    }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
