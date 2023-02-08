package PacMan;
import javafx.scene.shape.Ellipse;

public class Entity {
    public Ellipse ellipse;

    public Entity(Ellipse ellipse) {
        this.ellipse = ellipse;
    }

    public void move(boolean[] keysPressed) {
        if (keysPressed[0]) {
            ellipse.setCenterY(ellipse.getCenterY()-3);
        }
        if (keysPressed[1]) {
            ellipse.setCenterX(ellipse.getCenterX()+3);
        }
        if (keysPressed[2]) {
            ellipse.setCenterY(ellipse.getCenterY()+3);
        }
        if (keysPressed[3]) {
            ellipse.setCenterX(ellipse.getCenterX()-3);
        }

    }
    public void moveTowardsEntity(Entity entity) {

    }
}
