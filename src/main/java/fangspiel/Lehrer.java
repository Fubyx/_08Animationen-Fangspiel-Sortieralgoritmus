package fangspiel;

import javafx.scene.shape.Rectangle;

public class Lehrer extends Person {

    Rectangle r;
    int points = 0;

    public Lehrer(String vorname, String nachname) {
        super(vorname, nachname);
    }

    @Override
    public void shapeDefinition() {
        r = new Rectangle(10,10);
    }

    @Override
    public void whoAmI() {
        System.out.println("bin Lehrer: " + toString());
    }


}
