module com.example.demo1part3 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.demo1part3 to javafx.fxml;
    exports com.example.demo1part3;
}