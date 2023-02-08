package PacMan;
import javafx.scene.shape.Ellipse;

public class Entity {
    public Ellipse ellipse;
    int direction = 0;

    public Entity(Ellipse ellipse) {
        this.ellipse = ellipse;
    }

}
