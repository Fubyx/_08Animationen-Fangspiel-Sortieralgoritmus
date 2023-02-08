package PacMan;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
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
    double backgroundWidth = 700, backgroundHeight = 700;
    double sceneWidth = backgroundWidth, sceneHeight = backgroundHeight;
    double sectionWidth, sectionHeight;
    Group root = new Group();
    Scene s;
    Rectangle background;
    ArrayList<Rectangle> walls;

    ArrayList<Entity> enemies = new ArrayList<>();
    Entity player = new Entity( new Ellipse(sceneWidth / ((WIDTH_WALLNODES + 0.5) * 4), sceneHeight / ((HEIGHT_WALLNODES + 0.5) * 4) - 1, sceneWidth / ((WIDTH_WALLNODES + 0.5) * 4) - 1, sceneHeight / ((HEIGHT_WALLNODES + 0.5) * 4) - 1));


    boolean arcInwards = true;
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
        background.setFill(Color.DARKGRAY);
        root.getChildren().add(background);
        buildMaze();
        player.ellipse.setFill(Color.YELLOW);
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
                    case Q :
                        Platform.exit();
                        System.exit(0);
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
                player.ellipse.setCenterX(player.ellipse.getCenterX()/oldBackgroundWidth * backgroundWidth);
                player.ellipse.setCenterY(player.ellipse.getCenterY()/oldBackgroundHeight * backgroundHeight);
                player.ellipse.setRadiusX(player.ellipse.getRadiusX()/oldBackgroundWidth * backgroundWidth);
                player.ellipse.setRadiusY(player.ellipse.getRadiusY()/oldBackgroundHeight * backgroundHeight);

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

        Timeline timeline = new Timeline(new KeyFrame(new Duration(10), actionEvent -> {
            playerMove();
            wallCollision(player);
            //enemyMovement(player);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        Arc mouthArc = new Arc();
        mouthArc.setCenterX(player.ellipse.getCenterX());
        mouthArc.setCenterY(player.ellipse.getCenterY());
        mouthArc.setRadiusX(player.ellipse.getRadiusX());
        mouthArc.setRadiusY(player.ellipse.getRadiusY());
        mouthArc.setStartAngle(-45.0);
        mouthArc.setLength(90);
        mouthArc.setFill(Color.DARKGRAY);
        mouthArc.setType(ArcType.ROUND);
        root.getChildren().add(mouthArc);

        Timeline arcTransition = new Timeline(new KeyFrame(new Duration(10), actionEvent -> {
            mouthArc.setCenterX(player.ellipse.getCenterX());
            mouthArc.setCenterY(player.ellipse.getCenterY());
            mouthArc.setRadiusX(player.ellipse.getRadiusX());
            mouthArc.setRadiusY(player.ellipse.getRadiusY());
            if(arcInwards){
                if(mouthArc.getStartAngle() >= 0){
                    arcInwards = false;
                }else{
                    mouthArc.setStartAngle(mouthArc.getStartAngle() + 1);
                    mouthArc.setLength(mouthArc.getLength() - 2);
                }
            }else{
                if(mouthArc.getStartAngle() <= -45){
                    arcInwards = true;
                }else{
                    mouthArc.setStartAngle(mouthArc.getStartAngle() - 1);
                    mouthArc.setLength(mouthArc.getLength() + 2);
                }
            }
        }));
        arcTransition.setCycleCount(Timeline.INDEFINITE);
        arcTransition.play();
    }

    boolean wallCollision(Entity entity) {
        Rectangle temp = new Rectangle(player.ellipse.getCenterX()-player.ellipse.getRadiusX(), player.ellipse.getCenterY()-player.ellipse.getRadiusY(), player.ellipse.getRadiusX()*2, player.ellipse.getRadiusY()*2);
        if (temp.getX() < 0 || temp.getX() + temp.getWidth() > sceneWidth || temp.getY() < 0 || temp.getY() + temp.getHeight() > sceneHeight) {
            return true;
        }
        for (Rectangle wall : walls) {
            if (temp.intersects(wall.getLayoutBounds())) {
                return true;
            }
        }
        return false;
    }
    public void playerMove() {
        if (keysPressed[0]) {
            player.ellipse.setCenterY(player.ellipse.getCenterY()-1);
            if (wallCollision(player)) {
                player.ellipse.setCenterY(player.ellipse.getCenterY()+1);
            }
        }
        if (keysPressed[1]) {
            player.ellipse.setCenterX(player.ellipse.getCenterX()+1);
            if (wallCollision(player)) {
                player.ellipse.setCenterX(player.ellipse.getCenterX()-1);

            }
        }
        if (keysPressed[2]) {
            player.ellipse.setCenterY(player.ellipse.getCenterY()+1);
            if (wallCollision(player)) {
                player.ellipse.setCenterY(player.ellipse.getCenterY()-1);

            }
        }
        if (keysPressed[3]) {
            player.ellipse.setCenterX(player.ellipse.getCenterX()-1);
            if (wallCollision(player)) {
                player.ellipse.setCenterX(player.ellipse.getCenterX()+1);

            }
        }

    }

    public void buildMaze() {
        walls = new ArrayList<>();
        sectionWidth = backgroundWidth / (WIDTH_WALLNODES + 0.5);
        sectionHeight = backgroundHeight / (HEIGHT_WALLNODES + 0.5);


        for (int y = 0; y < wallNodes.size(); ++y) {
            for (int x = 0; x < wallNodes.get(y).size(); ++x) {
                Rectangle r;
                if (wallNodes.get(y).get(x).wallInDirection[0]) {
                    r = new Rectangle((x + 0.5) * sectionWidth, (y - 0.5) * sectionHeight, sectionWidth / 2, 1.5 * sectionHeight);
                    r.setFill(Color.BLACK);
                    walls.add(r);
                }
                if (wallNodes.get(y).get(x).wallInDirection[1]) {
                    r = new Rectangle((x + 0.5) * sectionWidth, (y + 0.5) * sectionHeight, 1.5 * sectionWidth, sectionHeight / 2);
                    r.setFill(Color.BLACK);
                    walls.add(r);
                }
                if (wallNodes.get(y).get(x).wallInDirection[2]) {
                    r = new Rectangle((x + 0.5) * sectionWidth, (y + 0.5) * sectionHeight, sectionWidth / 2, 1.5 * sectionHeight);
                    r.setFill(Color.BLACK);
                    walls.add(r);
                }
                if (wallNodes.get(y).get(x).wallInDirection[3]) {
                    r = new Rectangle((x - 0.5) * sectionWidth, (y + 0.5) * sectionHeight, 1.5 * sectionWidth, sectionHeight / 2);
                    r.setFill(Color.BLACK);
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

    private void enemyMovement(Entity e){
        ArrayList<Integer> area = new ArrayList<>(3);
        for(int i = 0; i < 2; ++i){
            area.add(i);
        }
        area.add((int)sectionWidth - 1);
        if(area.contains((((int)((e.ellipse.getCenterX() - e.ellipse.getRadiusX())*1000)  % (int)(1000*sectionWidth))/1000))){
            System.out.println("test");
        }
        area.remove(area.size() - 1);
        area.add((int)sectionHeight - 1);

        double []distanceToPlayer = new double[4];//0 = up, 1 = right, 2 = down, 3 = left

        distanceToPlayer[0] = Math.sqrt(Math.pow(player.ellipse.getCenterX() - e.ellipse.getCenterX(), 2) + Math.pow(player.ellipse.getCenterY() - e.ellipse.getCenterY() - 1, 2));
        distanceToPlayer[1] = Math.sqrt(Math.pow(player.ellipse.getCenterX() - e.ellipse.getCenterX() + 1, 2) + Math.pow(player.ellipse.getCenterY() - e.ellipse.getCenterY(), 2));
        distanceToPlayer[2] = Math.sqrt(Math.pow(player.ellipse.getCenterX() - e.ellipse.getCenterX(), 2) + Math.pow(player.ellipse.getCenterY() - e.ellipse.getCenterY() + 1, 2));
        distanceToPlayer[3] = Math.sqrt(Math.pow(player.ellipse.getCenterX() - e.ellipse.getCenterX() - 1, 2) + Math.pow(player.ellipse.getCenterY() - e.ellipse.getCenterY(), 2));

        double min = Math.min(Math.min(distanceToPlayer[0], distanceToPlayer[1]), Math.min(distanceToPlayer[2], distanceToPlayer[3]));

    }

    public static void main(String[] args) {
        launch(args);
    }
}
