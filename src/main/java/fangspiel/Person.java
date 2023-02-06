package fangspiel;

import javafx.scene.layout.StackPane;


public abstract class Person implements GraphikObject{
    String vorname;
    String nachname;

    boolean [] pos = new boolean[4];

    public Person(String vorname, String nachname) {
        this.vorname = vorname;
        this.nachname = nachname;
        shapeDefinition();
        for(boolean i : pos){
            i = false;
        }
    }

    public String toString(){
        return vorname + ", " + nachname;
    }

}


