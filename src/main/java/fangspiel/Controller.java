package fangspiel;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.KeyEvent;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.security.Key;

public class Controller{

    Rectangle background;

    Rectangle []obstacles;
    Lehrer l;
    Schueler s;

    int speed = 2;
    private boolean contact = false;

    Timeline t = new Timeline(new KeyFrame(Duration.millis(10), new EventHandler<ActionEvent>() {
        @Override
        public void handle(ActionEvent actionEvent) {
            move();
        }
    }));

    public void startGame(){
        t.setCycleCount(Timeline.INDEFINITE);
        t.play();
    }

    public void move(){
        double width = background.getWidth();
        double height = background.getHeight();

        double newX = 0, newY = 0;

        if((s.pos[0] || s.pos[2]) && !(s.pos[0] && s.pos[2])){
            if(s.pos[0]){
                newY = Math.max(s.c.getRadius()/2, s.c.getCenterY() - speed);
            }else{
                newY = Math.min(height - s.c.getRadius()/2, s.c.getCenterY() + speed);
            }
        }
        if((s.pos[1] || s.pos[3]) && !(s.pos[1] && s.pos[3])){
            if(s.pos[1]){
                newX = Math.max(s.c.getRadius()/2, s.c.getCenterX() - speed);
            }else{
                newX = Math.min(width - s.c.getRadius()/2, s.c.getCenterX() + speed);
            }
        }

        if(newX == 0){
            newX = s.c.getCenterX();
        }
        if(newY == 0){
            newY = s.c.getCenterY();
        }

        for(int i = 0; i < obstacles.length; ++i){
            //System.out.println(c.getCenterX() + " " + c.getCenterY() + "\t" + obstacles[i].getX() + " " + obstacles[i].getY() + " " + obstacles[i].getWidth() + " " + obstacles[i].getHeight());
            if(/*c.intersects(obstacles[i].getBoundsInParent()) || */obstacles[i].intersects(newX - s.c.getRadius(), newY - s.c.getRadius(), 2 * s.c.getRadius(), 2 * s.c.getRadius())){
                newX = s.c.getCenterX();
                newY = s.c.getCenterY();
            }
        }
        if(newX != 0){
            s.c.setCenterX(newX);
        }
        if(newY != 0){
            s.c.setCenterY(newY);
        }

        newX = 0;
        newY = 0;

        if((l.pos[0] || l.pos[2]) && !(l.pos[0] && l.pos[2])){
            if(l.pos[0]){
                newY = Math.max(0, l.r.getY() - speed);
            }else{
                newY = Math.min(height - l.r.getHeight(), l.r.getY() + speed);
            }
        }
        if((l.pos[1] || l.pos[3]) && !(l.pos[1] && l.pos[3])){
            if(l.pos[1]){
                newX = Math.max(0, l.r.getX() - speed);
            }else{
                newX = Math.min(width - l.r.getWidth(), l.r.getX() + speed);
            }
        }

        if(newX == 0){
            newX = l.r.getX();
        }
        if(newY == 0){
            newY = l.r.getY();
        }

        for(int i = 0; i < obstacles.length; ++i){
            if(obstacles[i].intersects(newX, newY, l.r.getWidth(), l.r.getHeight())){
                newX = l.r.getX();
                newY = l.r.getY();
            }
        }
        if(newX != 0){
            l.r.setX(newX);
        }
        if(newY != 0){
            l.r.setY(newY);
        }


        /*
        if((s.pos[0] || s.pos[2]) && !(s.pos[0] && s.pos[2])){
            if(s.pos[0]){
                s.c.setCenterY(Math.max(s.c.getRadius()/2, s.c.getCenterY() - speed));
            }else{
                s.c.setCenterY(Math.min(height - s.c.getRadius()/2, s.c.getCenterY() + speed));
            }
        }
        if((s.pos[1] || s.pos[3]) && !(s.pos[1] && s.pos[3])){
            if(s.pos[1]){
                s.c.setCenterX(Math.max(s.c.getRadius()/2, s.c.getCenterX() - speed));
            }else{
                s.c.setCenterX(Math.min(width - s.c.getRadius()/2, s.c.getCenterX() + speed));
            }
        }

        if((l.pos[0] || l.pos[2]) && !(l.pos[0] && l.pos[2])){
            if(l.pos[0]){
                l.r.setY(Math.max(l.r.getWidth()/2, l.r.getY() - speed));
            }else{
                l.r.setY(Math.min(height - l.r.getWidth()/2, l.r.getY() + speed));
            }
        }
        if((l.pos[1] || l.pos[3]) && !(l.pos[1] && l.pos[3])){
            if(l.pos[1]){
                l.r.setX(Math.max(l.r.getHeight()/2, l.r.getX() - speed));
            }else{
                l.r.setX(Math.min(width - l.r.getHeight()/2, l.r.getX() + speed));
            }
        }
         */


        if(s.c.intersects(l.r.getBoundsInParent())){
            if(!contact)
                ++l.points;
            contact = true;
        }else{
            if(contact)
                contact = false;
        }
    }


}
