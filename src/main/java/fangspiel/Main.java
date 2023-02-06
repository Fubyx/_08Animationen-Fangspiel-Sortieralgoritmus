package fangspiel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Random;


public class Main extends Application {
    Rectangle[] obstacles;
    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("Hello World");


        // polymorphismus
        ArrayList<Person> arrayList = new ArrayList();
        Lehrer l1 = new Lehrer("lv", "ln");
        Schueler s1 = new Schueler("sv", "sn");

        Controller c = new Controller();
        c.l = l1;
        c.s = s1;

        arrayList.add(l1);
        arrayList.add(s1);


        Rectangle r = new Rectangle(500, 500);
        r.setFill(Paint.valueOf("white"));

        c.background = r;
        Group g = new Group(r);
        g.getChildren().addAll(l1.r, s1.c);

        Scene scene = new Scene(g);

        primaryStage.setScene(scene);
        primaryStage.show();

        int amountOfObstacles = 50;
        obstacles = new Rectangle[amountOfObstacles];
        Random rand = new Random();
        for(int i = 0; i < amountOfObstacles; ++i){
            if(rand.nextBoolean()) {
                obstacles[i] = new Rectangle(10, 50);
            }else{
                obstacles[i] = new Rectangle(50, 10);
            }
            obstacles[i].setY(rand.nextDouble(0, primaryStage.getHeight() - 100));
            obstacles[i].setX(rand.nextDouble(0, primaryStage.getWidth() - 100));
            obstacles[i].setFill(Paint.valueOf("gray"));
            obstacles[i].setStroke(Paint.valueOf("green"));
            obstacles[i].setStrokeWidth(2.5);

            g.getChildren().add(obstacles[i]);
        }

        c.obstacles = obstacles;

        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch(keyEvent.getCode()){
                    case W -> {
                        c.l.pos[0] = true;
                    }
                    case A -> {
                        c.l.pos[1] = true;
                    }
                    case S -> {
                        c.l.pos[2] = true;
                    }
                    case D -> {
                        c.l.pos[3] = true;
                    }
                    case UP -> {
                        c.s.pos[0] = true;
                    }
                    case LEFT -> {
                        c.s.pos[1] = true;
                    }
                    case DOWN -> {
                        c.s.pos[2] = true;
                    }
                    case RIGHT -> {
                        c.s.pos[3] = true;
                    }
                    case Q -> {
                        System.out.println(l1.points);
                        Platform.exit();
                        System.exit(0);
                    }
                }
            }
        });


        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                switch(keyEvent.getCode()){
                    case W -> {
                        c.l.pos[0] = false;
                    }
                    case A -> {
                        c.l.pos[1] = false;
                    }
                    case S -> {
                        c.l.pos[2] = false;
                    }
                    case D -> {
                        c.l.pos[3] = false;
                    }
                    case UP -> {
                        c.s.pos[0] = false;
                    }
                    case LEFT -> {
                        c.s.pos[1] = false;
                    }
                    case DOWN -> {
                        c.s.pos[2] = false;
                    }
                    case RIGHT -> {
                        c.s.pos[3] = false;
                    }
                }
            }
        });

        c.startGame();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
