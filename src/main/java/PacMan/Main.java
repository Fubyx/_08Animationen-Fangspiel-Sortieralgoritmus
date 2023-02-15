package PacMan;

import javafx.animation.FadeTransition;
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
import javafx.stage.WindowEvent;
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
    /**1 section = the distance between 2 Nodes*/
    double sectionWidth, sectionHeight;
    Group root = new Group();
    Scene s;
    Rectangle background;
    ArrayList<Rectangle> walls;

    ArrayList<Entity> enemies = new ArrayList<>();
    Entity player = new Entity(new Ellipse(sceneWidth / ((WIDTH_WALLNODES + 0.5) * 4), sceneHeight / ((HEIGHT_WALLNODES + 0.5) * 4) - 1, sceneWidth / ((WIDTH_WALLNODES + 0.5) * 4) - 1, sceneHeight / ((HEIGHT_WALLNODES + 0.5) * 4) - 1), 100, 5);


    boolean arcInwards = true;
    int arcDirection = 1;


    ArrayList<Ellipse> powerUps = new ArrayList<>();

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
        player.direction = 1;
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
            double oldBackgroundWidth, oldBackgroundHeight;

            private void resizeEllipse (Ellipse e){
                e.setCenterX(e.getCenterX()/oldBackgroundWidth * backgroundWidth);
                e.setCenterY(e.getCenterY()/oldBackgroundHeight * backgroundHeight);
                e.setRadiusX(e.getRadiusX()/oldBackgroundWidth * backgroundWidth);
                e.setRadiusY(e.getRadiusY()/oldBackgroundHeight * backgroundHeight);
            }
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                double oldSceneWidth = sceneWidth, oldSceneHeight = sceneHeight;
                sceneWidth = s.getWidth();
                sceneHeight = s.getHeight();

                //background
                oldBackgroundHeight = backgroundHeight;
                oldBackgroundWidth = backgroundWidth;
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

                for(Ellipse e : powerUps){
                    resizeEllipse(e);
                }

                resizeEllipse(player.ellipse);

                ArrayList<Entity> toRem = new ArrayList<>();
                for(Entity e : enemies){
                    if(!e.toRemove)
                        resizeEllipse(e.ellipse);
                    else
                        toRem.add(e);
                }
                for(Entity e : toRem){
                    enemies.remove(e);
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
            ArrayList<Entity> toRem = new ArrayList<>();
            for(Entity e : enemies){
                if(!e.toRemove)
                    enemyMovement(e);
                else
                    toRem.add(e);
            }
            for(Entity e : toRem){
                enemies.remove(e);
            }
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
        mouthArc.setFill(background.getFill());
        mouthArc.setType(ArcType.ROUND);
        root.getChildren().add(mouthArc);

        Timeline arcTransition = new Timeline(new KeyFrame(new Duration(10), actionEvent -> {
            mouthArc.setCenterX(player.ellipse.getCenterX());
            mouthArc.setCenterY(player.ellipse.getCenterY());
            mouthArc.setRadiusX(player.ellipse.getRadiusX());
            mouthArc.setRadiusY(player.ellipse.getRadiusY());
            double arcStart = -45;
            if(arcDirection != player.direction) {
                arcDirection = player.direction;
                switch (player.direction) {
                    case 0 -> {
                        arcStart = 45;
                    }
                    case 2 -> {
                        arcStart = 225;
                    }
                    case 3 -> {
                        arcStart = 135;
                    }
                }
                mouthArc.setStartAngle(arcStart);
                mouthArc.setLength(90);
                arcInwards = true;
            }else{
                switch (arcDirection){
                    case 0 -> {
                        arcStart = 45;
                    }
                    case 2 -> {
                        arcStart = 225;
                    }
                    case 3 -> {
                        arcStart = 135;
                    }
                }
            }
            if(arcInwards){
                if(mouthArc.getStartAngle() >= arcStart + 45){
                    arcInwards = false;
                }else{
                    mouthArc.setStartAngle(mouthArc.getStartAngle() + 1);
                    mouthArc.setLength(mouthArc.getLength() - 2);
                }
            }else{
                if(mouthArc.getStartAngle() <= arcStart){
                    arcInwards = true;
                }else{
                    mouthArc.setStartAngle(mouthArc.getStartAngle() - 1);
                    mouthArc.setLength(mouthArc.getLength() + 2);
                }
            }
        }));
        arcTransition.setCycleCount(Timeline.INDEFINITE);
        arcTransition.play();

        enemies.add(new Entity(new Ellipse(backgroundWidth - player.ellipse.getRadiusX() - 1, backgroundHeight - player.ellipse.getRadiusY() - 1, player.ellipse.getRadiusX(), player.ellipse.getRadiusY()), 10, 15));
        enemies.get(0).ellipse.setFill(Color.RED);
        root.getChildren().add(enemies.get(0).ellipse);

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                int amountOfEnemies = random.nextInt(1, 5);
                for(int i = 0; i < amountOfEnemies; ++i){
                    double x = sectionWidth * (random.nextInt(1, WIDTH_WALLNODES));
                    double y = sectionHeight * (random.nextInt(1, HEIGHT_WALLNODES));
                    double radiusX = player.ellipse.getRadiusX();
                    double radiusY = player.ellipse.getRadiusY();
                    Entity e = new Entity(new Ellipse(x + radiusX + 1, y + radiusY + 1, radiusX, radiusY), 10, 5);
                    e.ellipse.setFill(Color.RED);
                    if(!wallCollision(e)) {
                        enemies.add(e);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                root.getChildren().add(e.ellipse);
                            }
                        });

                    }else{
                        --i;
                    }
                }
            }
        });
        t.start();


        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                timeline.stop();
                arcTransition.stop();
                Platform.exit();
                System.exit(0);
            }
        });

        Rectangle r = new Rectangle(sectionWidth * 4, 0, sectionWidth/2, backgroundHeight);
        r.setFill(Color.GREEN);
        ///root.getChildren().add(r);

    }

    boolean wallCollision(Entity entity) {
        Rectangle temp = new Rectangle(entity.ellipse.getCenterX() - entity.ellipse.getRadiusX(), entity.ellipse.getCenterY() - entity.ellipse.getRadiusY(), entity.ellipse.getRadiusX() * 2, entity.ellipse.getRadiusY() * 2);
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
    boolean ellipseCollision (Ellipse a, Ellipse b){
        return (Math.max(a.getCenterY(), b.getCenterY()) - Math.min(a.getCenterY(), b.getCenterY()) < a.getRadiusY() + b.getRadiusY()) && (Math.max(a.getCenterX(), b.getCenterX()) - Math.min(a.getCenterX(), b.getCenterX()) < a.getRadiusX() + b.getRadiusX());
    }
    public void playerMove() {
        if(player.hp <= 0){
            return;
        }
        if (keysPressed[0]) {
            player.ellipse.setCenterY(player.ellipse.getCenterY()-1);
            if (wallCollision(player)) {
                player.ellipse.setCenterY(player.ellipse.getCenterY()+1);
            }else{
                player.direction = 0;
            }
        }
        if (keysPressed[1]) {
            player.ellipse.setCenterX(player.ellipse.getCenterX()+1);
            if (wallCollision(player)) {
                player.ellipse.setCenterX(player.ellipse.getCenterX()-1);
            }else{
                player.direction = 1;
            }
        }
        if (keysPressed[2]) {
            player.ellipse.setCenterY(player.ellipse.getCenterY()+1);
            if (wallCollision(player)) {
                player.ellipse.setCenterY(player.ellipse.getCenterY()-1);
            }else{
                player.direction = 2;
            }
        }
        if (keysPressed[3]) {
            player.ellipse.setCenterX(player.ellipse.getCenterX()-1);
            if (wallCollision(player)) {
                player.ellipse.setCenterX(player.ellipse.getCenterX()+1);
            }else{
                player.direction = 3;
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
        if(e.hp <= 0){
            FadeTransition f = new FadeTransition();
            f.setNode(e.ellipse);
            f.setDuration(new Duration(3000));
            f.setCycleCount(1);
            f.setFromValue(1.0);
            f.setToValue(0.0);
            f.play();
            e.toRemove = true;
            return;
        }

        int hits = 0;
        ArrayList<Integer> area = new ArrayList<>(3);
        for(int i = 0; i < 2; ++i){
            area.add(i);
        }
        //area.add((int)sectionWidth - 1);
        if(area.contains((((int)((e.ellipse.getCenterX() - e.ellipse.getRadiusX())*1000)  % (int)(1000*sectionWidth))/1000))){
            ++hits;
        }
        //area.remove(area.size() - 1);
        //area.add((int)sectionHeight - 1);
        if(area.contains((((int)((e.ellipse.getCenterY() - e.ellipse.getRadiusY())*1000)  % (int)(1000*sectionHeight))/1000))){
            ++hits;
        }

        if(hits == 2){

            double w = sectionWidth/2;
            double h = sectionHeight/2;

            int newDirection = 0;
            double x = e.ellipse.getCenterX();
            double y = e.ellipse.getCenterY();

            double posX = (x - e.ellipse.getRadiusX())/sectionWidth;
            double posY = (y - e.ellipse.getRadiusY())/sectionHeight;
            x = sectionWidth * (int)posX + e.ellipse.getRadiusX() + 1;
            y = sectionHeight * (int)posY + e.ellipse.getRadiusY() + 1;

            double lowestDistance = findOptimalPath(x, y - h, 0, w, h);
            double dist = findOptimalPath(x + w, y, 0, w, h);
            if (dist < lowestDistance) {
                newDirection = 1;
                lowestDistance = dist;
            }
            dist = findOptimalPath(x, y + h, 0, w, h);
            if (dist < lowestDistance) {
                newDirection = 2;
                lowestDistance = dist;
            }
            dist = findOptimalPath(x - w, y, 0, w, h);
            if (dist < lowestDistance) {
                newDirection = 3;
            }
            e.direction = newDirection;
        }
        if(e.direction == 0){
            e.ellipse.setCenterY(e.ellipse.getCenterY() - 1);
            if(wallCollision(e)){
                e.ellipse.setCenterY(e.ellipse.getCenterY() + 1);
            }
            if(e.ellipse.getCenterY() < e.ellipse.getRadiusY()){
                e.ellipse.setCenterY(e.ellipse.getCenterY() + 1);
            }
        }else if(e.direction == 1){
            e.ellipse.setCenterX(e.ellipse.getCenterX() + 1);
            if(wallCollision(e)){
                e.ellipse.setCenterX(e.ellipse.getCenterX() - 1);
            }
            if(e.ellipse.getCenterX() > backgroundWidth - e.ellipse.getRadiusX()){
                e.ellipse.setCenterX(e.ellipse.getCenterX() - 1);
            }
        }else if(e.direction == 2){
            e.ellipse.setCenterY(e.ellipse.getCenterY() + 1);
            if(wallCollision(e)){
                e.ellipse.setCenterY(e.ellipse.getCenterY() - 1);
            }
            if(e.ellipse.getCenterY() > backgroundHeight - e.ellipse.getRadiusY()){
                e.ellipse.setCenterY(e.ellipse.getCenterY() - 1);
            }
        }else if(e.direction == 3){
            e.ellipse.setCenterX(e.ellipse.getCenterX() - 1);
            if(wallCollision(e)){
                e.ellipse.setCenterX(e.ellipse.getCenterX() + 1);
            }
            if(e.ellipse.getCenterY() < e.ellipse.getRadiusY()){
                e.ellipse.setCenterX(e.ellipse.getCenterX() + 1);
            }
        }//*/
        if(ellipseCollision(e.ellipse, player.ellipse)){
            player.hp -= e.damage;
            e.hp -= player.damage;
        }
    }
    private double findOptimalPath(double x, double y, int depth, double w, double h){
        if(depth > 6){
            return 50000;
        }
        //Ellipse e = new Ellipse(x, y, player.ellipse.getRadiusX(), player.ellipse.getRadiusY());
        //root.getChildren().add(e);
        if(wallCollision(new Entity(new Ellipse(x, y, player.ellipse.getRadiusX(), player.ellipse.getRadiusY()), 0, 0))){
            System.out.println(depth);
            return 50000;
        }
        double posX = x/sectionWidth;
        double posY = y/sectionHeight;


        double lowestDistance = Math.sqrt(Math.pow((x - player.ellipse.getCenterX()), 2) + Math.pow((y - player.ellipse.getCenterY()), 2));

        lowestDistance = Math.min(lowestDistance, findOptimalPath(x, y - h, depth + 1, w, h));
        lowestDistance = Math.min(lowestDistance, findOptimalPath(x + w, y, depth + 1, w, h));
        lowestDistance = Math.min(lowestDistance, findOptimalPath(x, y + h, depth + 1, w, h));
        lowestDistance = Math.min(lowestDistance, findOptimalPath(x - w, y, depth + 1, w, h));
        return lowestDistance;
    }

    /*
    private boolean isWholeNumber(Double d){
        String str = d.toString();
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) == '.'){
                String substring = str.substring(i + 1, 8);
                int ix = Integer.parseInt(substring);
                if(ix > 0){
                    return false;
                }
            }
        }
        return true;
    }//*/

    public static void main(String[] args) {
        launch(args);
    }
}
