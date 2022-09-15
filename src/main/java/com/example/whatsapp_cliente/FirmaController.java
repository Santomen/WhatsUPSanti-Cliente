package com.example.whatsapp_cliente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import paquete.Paquete;

import java.io.IOException;

public class FirmaController {

    @FXML
    void escogerMiguel(ActionEvent event) throws IOException {
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        cambiar_chat(stage,3);
    }

    @FXML
    void escogerPilar(ActionEvent event) throws IOException {
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        cambiar_chat(stage,2);
    }

    @FXML
    void escogerSantiago(ActionEvent event) throws IOException {
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        cambiar_chat(stage,1);
    }
    void cambiar_chat(Stage stage,int opcion ) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        root=fxmlLoader.load();
        HelloController helloController=fxmlLoader.getController();
        helloController.asignarPuertos( opcion);
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    private Stage stage;
    private Scene scene;
    private Parent root;


}
