module com.javaintellij.examenjava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires static lombok;


    opens com.javaintellij.examenjava to javafx.fxml;
    exports com.javaintellij.examenjava;
}