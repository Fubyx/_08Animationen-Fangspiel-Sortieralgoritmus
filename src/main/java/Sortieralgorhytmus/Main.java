package Sortieralgorhytmus;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Random;

public class Main extends Application {
    Scene scene;
    Timeline timeline;
    Group group = new Group();
    Group menu = new Group();
    Group sortarea = new Group();
    int amountToSort, timePerStep;
    int[] numArr;
    Rectangle[] arrrayRepresentationRectangles;
    Random random = new Random();
    Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Hello World");
        Rectangle background = new Rectangle(700, 300, Color.WHITE);
        TextField textFieldAmount = new TextField("Anzahl");
        TextField textFieldMin = new TextField("Minimaler Wert");
        TextField textFieldMax = new TextField("Maximaler Wert");
        TextField textFieldTime = new TextField("Zeit pro Schritt [ms]");
        textFieldAmount.setPrefWidth(180);
        textFieldAmount.setLayoutX(10);
        textFieldAmount.setLayoutY(40);
        EventHandler textEvent = new EventHandler() {
            @Override
            public void handle(Event event) {
                try {
                    amountToSort = Integer.parseInt(textFieldAmount.getText());
                    timePerStep = Integer.parseInt(textFieldTime.getText());
                    if (timeline != null) {
                        timeline.stop();
                    }
                    loadSort();
                } catch (NumberFormatException e) {
                    textFieldAmount.setText("ungültig");
                    textFieldTime.setText("ungültig");
                }

            }
        };
        textFieldAmount.setOnAction(textEvent);
        textFieldTime.setPrefWidth(180);
        textFieldTime.setLayoutX(10);
        textFieldTime.setLayoutY(80);
        textFieldTime.setOnAction(textEvent);
        menu.getChildren().add(background);
        menu.getChildren().add(textFieldAmount);
        menu.getChildren().add(textFieldTime);
        group.getChildren().add(menu);
        group.getChildren().add(sortarea);
        scene = new Scene(group);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void loadSort() {
        numArr = new int[amountToSort];
        for (int i = 0; i < amountToSort; i++) {
            numArr[i] = random.nextInt(1, amountToSort + 1);
        }
        startSort();
    }

    public void startSort() {
        group.getChildren().remove(sortarea);
        arrrayRepresentationRectangles = new Rectangle[amountToSort];
        sortarea = new Group(new Rectangle(500, 300, Color.BLACK));
        sortarea.setLayoutX(200);
        for (int i = 0; i < amountToSort; i++) {
            arrrayRepresentationRectangles[i] = new Rectangle(10.0/amountToSort+ i * (500.0 - 10.0/amountToSort) / amountToSort, 300 - 290.0 * numArr[i] / amountToSort, (500.0- 10.0/amountToSort) / amountToSort, 290.0 * numArr[i] / amountToSort);
            arrrayRepresentationRectangles[i].setFill(Color.BLUE);
            arrrayRepresentationRectangles[i].setStroke(Color.WHITE);
            arrrayRepresentationRectangles[i].setStrokeWidth(20.0/amountToSort);
            sortarea.getChildren().add(arrrayRepresentationRectangles[i]);
        }
        group.getChildren().add(sortarea);

        //farbmarkierung für de wos kontrolliert werdn?
        int[] temp = {0}, i = {0}, j = {0};
        timeline = new Timeline(new KeyFrame(new Duration(timePerStep), ActionEvent -> {

            if (j[0] == amountToSort - 1 - i[0]) {
                i[0]++;
                j[0] = 0;
            }
            arrrayRepresentationRectangles[j[0]].setFill(Color.BLUE);
            if (i[0] == amountToSort - 2) {
                arrrayRepresentationRectangles[j[0] + 1].setFill(Color.BLUE);
                try {
                    this.stop();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            if (numArr[j[0]] > numArr[j[0] + 1]) {
                temp[0] = numArr[j[0]];
                numArr[j[0]] = numArr[j[0] + 1];
                numArr[j[0] + 1] = temp[0];
            }
            updateArray();
            j[0] ++;
            if (j[0] == amountToSort-1-i[0]) {
                arrrayRepresentationRectangles[j[0]].setFill(Color.BLUE);
                arrrayRepresentationRectangles[0].setFill(Color.YELLOW);
                arrrayRepresentationRectangles[1].setFill(Color.YELLOW);

            } else {
                arrrayRepresentationRectangles[j[0]].setFill(Color.YELLOW);
                arrrayRepresentationRectangles[j[0] + 1].setFill(Color.YELLOW);
            }
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    public void updateArray() {
        for (int i = 0; i < amountToSort; i++) {
            arrrayRepresentationRectangles[i].setY(300 - 290.0 * numArr[i] / amountToSort);
            arrrayRepresentationRectangles[i].setHeight(290.0 * numArr[i] / amountToSort);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


}
