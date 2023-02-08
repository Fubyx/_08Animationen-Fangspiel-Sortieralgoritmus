package PacMan;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Binding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

public class Main extends Application {
    ArrayList<ArrayList<WallNode>> wallNodes = new ArrayList<>();
    final int WIDTH_WALLNODES = 10;
    final int HEIGHT_WALLNODES = 10;
    Random random = new Random();
    boolean[] keysPressed = new boolean[] {false, false, false, false};
    double stageWidth = 700, stageHeight = 700;
    double sceneWidth, sceneHeight;
    double backgroundWidth = 700, backgroundHeight = 700;
    Group root = new Group();
    Scene s;

    Rectangle background;
    ArrayList<Rectangle> walls;

    Entity player = new Entity( new Ellipse(0, 0, stageWidth / ((WIDTH_WALLNODES + 0.5) * 4), stageHeight / ((HEIGHT_WALLNODES + 0.5) * 4)));
    Entity bot = new Entity( new Ellipse());

    @Override
    public void start(Stage primaryStage) throws Exception {
        for (int y = 0; y < HEIGHT_WALLNODES; y++) {
            wallNodes.add(new ArrayList());
            for (int x = 0; x < WIDTH_WALLNODES; x++) {
                wallNodes.get(y).add(new WallNode());
            }
        }
        generateRandomWallsWithNodes();
        background = new Rectangle(0, 0, backgroundWidth, backgroundHeight);
        background.setFill(Paint.valueOf("white"));
        root.getChildren().add(background);
        buildMaze();
        root.getChildren().add(player.ellipse);
        //root.getChildren().add(new Rectangle(0, 0, stageWidth, stageHeight));
        primaryStage.setTitle("Pac Man!");
        s = new Scene(root);
        s.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case W:
                        keysPressed[0] = true;
                        break;
                    case A:
                        keysPressed[3] = true;
                        break;
                    case S:
                        keysPressed[2] = true;
                        break;
                    case D:
                        keysPressed[1] = true;
                        break;

                }

            }
        });
        s.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch (keyEvent.getCode()) {
                    case W:
                        keysPressed[0] = false;
                        break;
                    case A:
                        keysPressed[3] = false;
                        break;
                    case S:
                        keysPressed[2] = false;
                        break;
                    case D:
                        keysPressed[1] = false;
                        break;

                }
            }
        });
        primaryStage.setScene(s);
        primaryStage.show();
        sceneHeight = s.getHeight();
        sceneWidth = s.getWidth();


        ChangeListener<Number> resizeListener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                double oldSceneWidth = sceneWidth, oldSceneHeight = sceneHeight;
                sceneWidth = s.getWidth();
                sceneHeight = s.getHeight();

                //background
                double oldBackgroundHeight = backgroundHeight, oldBackgroundWidth = backgroundWidth;
                backgroundWidth= backgroundWidth/oldSceneWidth * sceneWidth;
                backgroundHeight =backgroundHeight/oldSceneHeight * sceneHeight;
                background.setWidth(backgroundWidth + 1);
                background.setHeight(backgroundHeight + 1);
                //maze
                for (Rectangle i: walls) {
                    i.setX(i.getX()/oldBackgroundWidth * backgroundWidth);
                    i.setY(i.getY()/oldBackgroundHeight * backgroundHeight);
                    i.setWidth(i.getWidth()/oldBackgroundWidth * backgroundWidth);
                    i.setHeight(i.getHeight()/oldBackgroundHeight * backgroundHeight);
                }
            }
        };
        s.widthProperty().addListener(resizeListener);
        s.heightProperty().addListener(resizeListener);
        primaryStage.setMinWidth(100);
        primaryStage.setMinHeight(100);

        Timeline timeline = new Timeline(new KeyFrame(new Duration(25), actionEvent -> {
            player.move(keysPressed);
            wallCollision(player);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }
    void wallCollision(Entity entity) {

    }

    public void buildMaze() {

        walls = new ArrayList<>();
        double sectionWidth = backgroundWidth / (WIDTH_WALLNODES + 0.5);
        double sectionHeight = backgroundHeight / (HEIGHT_WALLNODES + 0.5);


        for (int y = 0; y < wallNodes.size(); ++y) {
            for (int x = 0; x < wallNodes.get(y).size(); ++x) {
                Rectangle r;
                if (wallNodes.get(y).get(x).wallInDirection[0]) {
                    r = new Rectangle((x + 0.5) * sectionWidth, (y - 0.5) * sectionHeight, sectionWidth / 2, 1.5 * sectionHeight);
                    r.setFill(Paint.valueOf("green"));
                    walls.add(r);
                }
                if (wallNodes.get(y).get(x).wallInDirection[1]) {
                    r = new Rectangle((x + 0.5) * sectionWidth, (y + 0.5) * sectionHeight, 1.5 * sectionWidth, sectionHeight / 2);
                    r.setFill(Paint.valueOf("green"));
                    walls.add(r);
                }
                if (wallNodes.get(y).get(x).wallInDirection[2]) {
                    r = new Rectangle((x + 0.5) * sectionWidth, (y + 0.5) * sectionHeight, sectionWidth / 2, 1.5 * sectionHeight);
                    r.setFill(Paint.valueOf("green"));
                    walls.add(r);
                }
                if (wallNodes.get(y).get(x).wallInDirection[3]) {
                    r = new Rectangle((x - 0.5) * sectionWidth, (y + 0.5) * sectionHeight, 1.5 * sectionWidth, sectionHeight / 2);
                    r.setFill(Paint.valueOf("green"));
                    walls.add(r);
                }

                //r.heightProperty() new SimpleDoubleProperty();
            }
        }
        root.getChildren().addAll(walls);
    }

    private void generateRandomWallsWithNodes() {
        generateWallsRecursion(0, 0);
        deleteSomeWalls(0, 0, 5);
    }

    private void deleteSomeWalls(int x, int y, int skipXWalls) {
        if (wallNodes.get(y).get(x).wallInDirection[0] && wallNodes.get(y - 1).get(x).hasAWall()) {
            if (skipXWalls == 0) {
                wallNodes.get(y).get(x).wallInDirection[0] = false;
                skipXWalls = random.nextInt(3, 8);
            }
            deleteSomeWalls(x, y - 1, skipXWalls - 1);
        }
        if (wallNodes.get(y).get(x).wallInDirection[1] && wallNodes.get(y).get(x + 1).hasAWall()) {
            if (skipXWalls == 0) {
                wallNodes.get(y).get(x).wallInDirection[1] = false;
                skipXWalls = random.nextInt(3, 8);
            }
            deleteSomeWalls(x + 1, y, skipXWalls - 1);
        }
        if (wallNodes.get(y).get(x).wallInDirection[2] && wallNodes.get(y + 1).get(x).hasAWall()) {
            if (skipXWalls == 0) {
                wallNodes.get(y).get(x).wallInDirection[2] = false;
                skipXWalls = random.nextInt(3, 8);
            }
            deleteSomeWalls(x, y + 1, skipXWalls - 1);
        }
        if (wallNodes.get(y).get(x).wallInDirection[3] && wallNodes.get(y).get(x - 1).hasAWall()) {
            if (skipXWalls == 0) {
                wallNodes.get(y).get(x).wallInDirection[3] = false;
                skipXWalls = random.nextInt(3, 8);
            }
            deleteSomeWalls(x - 1, y, skipXWalls - 1);
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
