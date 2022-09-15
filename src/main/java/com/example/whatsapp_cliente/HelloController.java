package com.example.whatsapp_cliente;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import paquete.Paquete;

import java.io.IOException;

public class HelloController {

    @FXML
    private Pane PANE;

    @FXML
    private ScrollPane SC;

    @FXML
    private VBox VB;

    @FXML
    private Button btnPersona1;

    @FXML
    private Button btnPersona2;

    @FXML
    private Label lblC;

  LogicaNegocio usuarios;//Esta variable nos indica una de las combinaciones eleccionadas del chat y es el que nos permite inicializar los datos con los que se va a trabar
    //esta variable se manda de una pantalla a otra para que no se pierda su valor
  public void asignarPuertos(int opcion){
      usuarios=LogicaNegocio.getInstance(opcion);
      lblC.setText("Contactos de "+ usuarios.Nombreemi);
      btnPersona1.setText(usuarios.Nombrereceptor1);
      btnPersona2.setText(usuarios.Nombrereceptor2);
  }
  public void recuperar_puertos(LogicaNegocio us){
      usuarios=us;
      lblC.setText("Contactos de "+ usuarios.Nombreemi);
      btnPersona1.setText(usuarios.Nombrereceptor1);
      btnPersona2.setText(usuarios.Nombrereceptor2);
  }



    @FXML
    void toPersona1(ActionEvent event) throws IOException {
        Paquete datos=new Paquete("p1",usuarios.puerto_emi,usuarios.puerto_receptor1);
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        cambiar_chat(stage,usuarios.Nombrereceptor1, datos);
    }

    @FXML
    void toPersona2(ActionEvent event) throws IOException {
        Paquete datos=new Paquete("p2",usuarios.puerto_emi,usuarios.puerto_receptor2);
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        cambiar_chat(stage,usuarios.Nombrereceptor2, datos);
    }


     void cambiar_chat(Stage stage, String persona, Paquete datos) throws IOException {
         FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("Chat.fxml"));
         root=fxmlLoader.load();
         ChatController chatController=fxmlLoader.getController();
         chatController.cambiar_nombre(persona);
         chatController.Establecer_Paquete(datos);
         chatController.Esteblecer_usuario(usuarios);
         //stage=(Stage)((Node)event.getSource()).getScene().getWindow();
         scene=new Scene(root);
         stage.setScene(scene);
         stage.show();
     }
    private Stage stage;
    private Scene scene;
    private Parent root;




}
