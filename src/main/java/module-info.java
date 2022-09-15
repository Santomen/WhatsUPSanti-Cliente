module com.example.whatsapp_cliente {
    requires javafx.controls;
    requires javafx.fxml;
    //opens controller to javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires java.desktop;

    opens com.example.whatsapp_cliente to javafx.fxml;
    exports com.example.whatsapp_cliente;
}