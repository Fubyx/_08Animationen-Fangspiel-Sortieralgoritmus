package PacMan;
import javafx.scene.shape.Ellipse;

public class Entity {
    public Ellipse ellipse;
    int direction = 0;
    int hp;
    int damage;
    boolean toRemove = false;

    public Entity(Ellipse ellipse, int hp, int damage) {
        this.ellipse = ellipse;
        this.hp = hp;
        this.damage = damage;
    }

}
