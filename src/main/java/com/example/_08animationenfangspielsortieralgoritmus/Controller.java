package com.example._08animationenfangspielsortieralgoritmus;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Controller {
    public Button btn;
    public Button btn1;
    public Group group;
    public Rectangle background=new Rectangle(300,500);

    public Controller(){
        System.out.println("first");
    }

    public void initialize(){
        System.out.println("second");   // fxml file wurde geladen
        background.setFill(Color.LIGHTGREEN);
        group.getChildren().add(background);
    }

    public void click(ActionEvent actionEvent) {
        FadeTransition ft = new FadeTransition(Duration.millis(3000), btn1);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.setCycleCount(Timeline.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();

        ScaleTransition st = new ScaleTransition(Duration.millis(2000), background);
        st.setByX(1);
        st.setByY(1);
        st.setCycleCount(4);
        st.setAutoReverse(true);
        st.play();
    }

    public void test (KeyEvent e){
        System.out.println("Test");
    }
}
