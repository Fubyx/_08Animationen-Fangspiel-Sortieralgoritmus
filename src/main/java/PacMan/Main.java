package PacMan;

import javafx.application.Application;
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

    int stageWidth = 0, stageHeight = 0;
    Group root = new Group();


    @Override
    public void start(Stage primaryStage) throws Exception {
        for (int y = 0; y < HEIGHT_WALLNODES; y++) {
            wallNodes.add(new ArrayList());
            for (int x = 0; x < WIDTH_WALLNODES; x++) {
                wallNodes.get(y).add(new WallNode());
            }
        }
        generateRandomWallsWithNodes();
        buildMaze();
        primaryStage.setTitle("Pac Man!");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void buildMaze() {

        ArrayList<Rectangle> walls = new ArrayList<>();
        double sectionWidth = stageWidth / (WIDTH_WALLNODES + 2);
        double sectionHeight = stageHeight / (HEIGHT_WALLNODES + 2);


        for (int y = 0; y < wallNodes.size(); ++y) {
            for (int x = 0; x < wallNodes.get(y).size(); ++x) {
                Rectangle r;
                switch (wallNodes.get(y).get(x).wallInDirection) {
                    case 0 -> {//up
                        r = new Rectangle(x * sectionWidth, (y - 1) * sectionHeight, sectionWidth / 2, sectionHeight);
                    }
                    case 1 -> {//right
                        r = new Rectangle((x - 1) * sectionWidth, y * sectionHeight, sectionWidth, sectionHeight / 2);
                    }
                    case 2 -> {//down
                        r = new Rectangle(x * sectionWidth, y * sectionHeight, sectionWidth / 2, sectionHeight);
                    }
                    case 3 -> {//left
                        r = new Rectangle(x * sectionWidth, y * sectionHeight, sectionWidth, sectionHeight / 2);
                    }
                    default -> {
                        r = new Rectangle();
                        System.out.println("David du trottl");
                    }
                }
                r.setFill(Paint.valueOf("green"));
                walls.add(r);
            }
        }
        root.getChildren().addAll(walls);
    }

    private void generateRandomWallsWithNodes() {
        generateWallsRecursion(0, 0);
        // now delete some walls
        boolean loop = true;
        int skipXWalls = 3;
        int x = 0, y = 0;
        while (loop) {
            int direction = wallNodes.get(y).get(x).wallInDirection;
            if (skipXWalls == 0) {
                wallNodes.get(y).get(x).wallInDirection = -1;
                skipXWalls = random.nextInt(1, 10);
            }
            if (direction == 0) {
                y--;
            } else if (direction == 1) {
                x++;
            } else if (direction == 2) {
                y++;
            } else if (direction == 3) {
                x--;
            }
        }
    }

    private void generateWallsRecursion(int x, int y) {
        for (int i = 0; i < 20; i++) {
            switch (random.nextInt(0, 4)) {
                case 0:
                    if (x > 0 && !wallNodes.get(y).get(x - 1).isConnected) {
                        wallNodes.get(y).get(x).wallInDirection = 3;
                        generateWallsRecursion(x - 1, y);
                        return;
                    }
                case 1:
                    if (x + 1 < WIDTH_WALLNODES && !wallNodes.get(y).get(x + 1).isConnected) {
                        wallNodes.get(y).get(x).wallInDirection = 1;
                        generateWallsRecursion(x + 1, y);
                        return;
                    }
                case 2:
                    if (y > 0 && !wallNodes.get(y - 1).get(x).isConnected) {
                        wallNodes.get(y).get(x).wallInDirection = 0;
                        generateWallsRecursion(x, y - 1);
                        return;
                    }
                case 3:
                    if (y + 1 < HEIGHT_WALLNODES && !wallNodes.get(y + 1).get(x).isConnected) {
                        wallNodes.get(y).get(x).wallInDirection = 2;
                        generateWallsRecursion(x, y + 1);
                        return;
                    }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
