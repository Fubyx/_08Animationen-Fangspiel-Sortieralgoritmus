module com.example._08animationenfangspielsortieralgoritmus {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example._08animationenfangspielsortieralgoritmus to javafx.fxml;
    opens fangspiel to javafx.fxml;
    exports com.example._08animationenfangspielsortieralgoritmus;
    exports fangspiel;
}