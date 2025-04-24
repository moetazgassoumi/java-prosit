module com.example.educonnect.educonnect {
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    requires com.almasb.fxgl.all;
    requires java.sql;
    requires mysql.connector.java;
    requires MaterialFX;
    requires javafx.swing;
    requires java.mail;

    opens com.example.educonnect.educonnect.Entities to javafx.fxml;
    exports com.example.educonnect.educonnect.Entities;
    opens com.example.educonnect.educonnect.Controllers to javafx.fxml;
    exports com.example.educonnect.educonnect.Controllers ;
    opens com.example.educonnect.educonnect to javafx.fxml;
    exports  com.example.educonnect.educonnect ;
}