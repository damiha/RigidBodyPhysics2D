module com.example.rigidbody2d {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.example.rigidbody2d to javafx.fxml;
    exports com.example.rigidbody2d;
}