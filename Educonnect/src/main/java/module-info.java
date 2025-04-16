module com.esprit {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires MaterialFX;

    // Export main application package
    exports com.esprit;
    opens com.esprit.Entities to javafx.base;
    opens com.esprit.Models to javafx.base;
    // Export controller packages
    exports com.esprit.Controllers;
    exports com.esprit.Controllers.Karim;
    exports com.esprit.Controllers.Mayssa;
    exports com.esprit.Controllers.Aziz;


    // Open packages to javafx.fxml
    opens com.esprit to javafx.fxml;
    opens com.esprit.Controllers to javafx.fxml;
    opens com.esprit.Controllers.Karim to javafx.fxml;
    opens com.esprit.Controllers.Mayssa to javafx.fxml;
    opens com.esprit.Controllers.Aziz to javafx.fxml;

    // Open Views package in resources
    opens Views to javafx.fxml;
}