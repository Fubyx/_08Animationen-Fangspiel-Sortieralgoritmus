package PacMan;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;

public class Main extends Application {
    ArrayList<ArrayList<WallNode>> wallNodes = new ArrayList<>();
    final int WIDTH_WALLNODES = 10;
    final int HEIGHT_WALLNODES = 10;

    int stageWidth = 0, stageHeight = 0;
    Group root = new Group();


    @Override
    public void start(Stage primaryStage) throws Exception{
        for (int y = 0; y < HEIGHT_WALLNODES; y++) {
            wallNodes.add(new ArrayList());
            for (int x = 0; x < WIDTH_WALLNODES; x++) {
                wallNodes.get(y).add(new WallNode());
            }
        }

        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public void buildMaze (){

        ArrayList<Rectangle> walls = new ArrayList<>();
        double sectionWidth = stageWidth/(WIDTH_WALLNODES + 2);
        double sectionHeight = stageHeight/(HEIGHT_WALLNODES + 2);


        for(int y = 0; y < wallNodes.size(); ++y){
            for(int x = 0; x < wallNodes.get(y).size(); ++x){
                Rectangle r;
                switch (wallNodes.get(y).get(x).wallInDirection){
                    case 0 -> {//up
                        r = new Rectangle(x * sectionWidth, (y - 1) * sectionHeight, sectionWidth/2, sectionHeight);
                    }
                    case 1 -> {//right
                        r = new Rectangle((x - 1) * sectionWidth, y * sectionHeight, sectionWidth, sectionHeight/2);
                    }
                    case 2 -> {//down
                        r = new Rectangle(x * sectionWidth, y * sectionHeight, sectionWidth/2, sectionHeight);
                    }
                    case 3 -> {//left
                        r = new Rectangle(x * sectionWidth, y * sectionHeight, sectionWidth, sectionHeight/2);
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


    public static void main(String[] args) {
        launch(args);
    }
}
