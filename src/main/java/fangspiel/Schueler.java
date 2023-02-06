package fangspiel;

import javafx.scene.shape.Circle;

public class Schueler extends Person implements SoundObjekt{
    Circle c;
    public Schueler(String vorname, String nachname) {
        super(vorname, nachname);
    }

    @Override
    public void shapeDefinition() {
        c = new Circle(5);
    }

    @Override
    public void whoAmI() {
        System.out.println("bin Schüler: " + toString());
        playSound();
    }

    @Override
    public void playSound() {
        System.out.println("habe super sound!!!");
    }
}
