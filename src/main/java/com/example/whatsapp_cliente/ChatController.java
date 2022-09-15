package com.example.whatsapp_cliente;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import paquete.Paquete;
import javax.swing.JOptionPane;
import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

import static java.lang.Integer.parseInt;
//import static sun.net.www.http.KeepAliveCache.result;

public class ChatController implements Runnable{
    //Queue<Paquete> mensajes_cifrados=new PriorityQueue<>();
    List<Paquete>mensajes_base=new ArrayList<>();
    List<Paquete> mensajes_base_externosAlChatActual=new ArrayList<>();
    @FXML
    private VBox VB;

    @FXML
    private Pane PANEChat;

    @FXML
    private ScrollPane SCChat;

    @FXML
    private Button btnAsimetrico;

    @FXML
    private Button btnDesci;

    @FXML
    private Button btnDescifrar;

    @FXML
    private Button btnPlano;

    @FXML
    private Button btnRegresar;

    @FXML
    private Button btnSimetrico;

    @FXML
    private Label lblChat;

    @FXML
    private Label lblLave;


    @FXML
    private TextField txfMensaje;
    @FXML
    private TextField txfLLave;

    private Paquete datos;//datos proporciona la información del emisor osea la persona del chat y del receptor con quien se habla en este chat
//es la variable que se va a mandar por la red ya que contiene el mensaje, si es cifrado, la firma quien es el que lo manda y el receptor
   public LogicaNegocio usuario;//esta variable esta aqui para recordar quien es el usuario que ha iniciado la sesión

    public void Establecer_Paquete(Paquete recibido){
        this.datos=recibido;
        cargar_mensajes();
    }
    public void Esteblecer_usuario(LogicaNegocio us){usuario=us;}

    private Stage stage;
    private Scene scene;
    private Parent root;
    public void cambiar_nombre(String Persona){
        lblChat.setText(Persona);
    }
    public void initialize() {

        Thread hilo1=new Thread(this);
        //cargar_mensajes();
        hilo1.start();


    }
    void agregar_archivo(int PuertoAmigo, Paquete p){
        try{
            String archivo=String.valueOf(datos.getPuerto_emisor())+String.valueOf(PuertoAmigo);//es puerto emisor si se recibe el mensaje, es puerto receptor si este se manda desde aqui
            archivo=archivo+".txt";
            FileOutputStream file=new FileOutputStream(archivo);
            ObjectOutputStream flujo_salida=new ObjectOutputStream(file);
            mensajes_base.add(new Paquete(p.getMensaje(),p.getPuerto_emisor(),p.getPuerto_receptor()));
            flujo_salida.writeObject(mensajes_base);
            flujo_salida.close();
            file.close();
        }
        catch(IOException e){
            System.out.println(e);
        }
    }

    void cargar_mensajes(){
        try{
            String archivo=String.valueOf(datos.getPuerto_emisor())+String.valueOf(datos.getPuerto_receptor());
            archivo=archivo+".txt";
            FileInputStream file=new FileInputStream(archivo);
            ObjectInputStream flujo_entrada=new ObjectInputStream(file);
            List<Paquete> p=(List<Paquete>)flujo_entrada.readObject();

            mensajes_base=p;
            for(Paquete paq:mensajes_base){
                System.out.println(paq.getMensaje());
            }
            cargarMensajesPantalla();
            flujo_entrada.close();
            file.close();
        }
        catch(IOException e){
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    void cargarMensajesPantalla(){
        for(Paquete paq:mensajes_base){
            if(datos.getPuerto_emisor()==paq.getPuerto_emisor()){
                mensaje_derecha(paq.getMensaje());
            }
            else{
                VB.getChildren().add(new Label(paq.getMensaje()));
            }
        }
    }
    @FXML
    void Regresar(ActionEvent event) throws IOException {
        servidor.close();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root=fxmlLoader.load();
        HelloController helloController=fxmlLoader.getController();
        helloController.recuperar_puertos(usuario);
        stage=(Stage)((Node)event.getSource()).getScene().getWindow();
        scene=new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    @FXML
    void enviarFirmaDigital(ActionEvent event) {
         String mensaje=txfMensaje.getText();
         String resumen=SHA1(mensaje);
         System.out.println("SHA "+resumen);
         String resumen_cifrado=cifrador_cesar(resumen,Integer.parseInt(txfLLave.getText()));
         datos.setResumen_cifrado(resumen_cifrado);
        System.out.println("Resumen cifrado "+datos.getResumen_cifrado());
         datos.setMensaje(mensaje);
        datos.setIScifrado(true);
        send_cifrado(mensaje);
        //String mensajeE=datos.getMensaje()+"\n Firma:"+datos.getResumen_cifrado();
       // agregar_archivo(datos.getPuerto_receptor(),new Paquete(mensajeE,datos.getPuerto_emisor(), datos.getPuerto_receptor()));
    }
    @FXML
    void comprobarFirmaDigital(ActionEvent event) {
       /* //Paquete dato=mensajes_cifrados.get(0);
        //mensajes_cifrados.remove(0);
        System.out.println(dato.getResumen_cifrado());
        String hashResumen=descifrador_cesar(dato.getResumen_cifrado(),Integer.parseInt(txfLLave.getText()));
        System.out.println("hash resumen "+hashResumen);
        String hashMensaje=SHA1(dato.getMensaje());
        System.out.println("hash mensaje "+hashMensaje);
        String mensajeE=dato.getMensaje()+"\n Firma:"+dato.getResumen_cifrado();
        agregar_archivo(dato.getPuerto_emisor(),new Paquete(mensajeE,dato.getPuerto_emisor(), dato.getPuerto_receptor()));
        if(hashResumen.equalsIgnoreCase(hashMensaje)){
            alerta("Confirmación exitosa",hashResumen+" equivale a "+hashMensaje);
        }else{
            alerta("ALERTA","No hubo coincidencia, el mensaje fue modificado o la llave es incorrecta");
        }*/


    }
    public String SHA1(String msm){
        String sha1="";
        String value=msm;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            digest.reset();
            digest.update(value.getBytes("utf8"));
            sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
        } catch (Exception e){
            e.printStackTrace();
        }
        System.out.println();
        return sha1;
    }
    public void alerta(String titulo,String mensaje){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    String descifrarSimetrico(Paquete pack,int llave) {

        String mensaje_descifrado=descifrador_cesar(pack.getMensaje(),llave);
        agregar_archivo(pack.getPuerto_emisor(),new Paquete(mensaje_descifrado, pack.getPuerto_emisor(), pack.getPuerto_receptor()));
        return mensaje_descifrado;
    }
    @FXML
    String descifrarAsimetrico(Paquete pack,int llave) {

        String mensaje_descifrado=cifrador_cesar(pack.getMensaje(),llave);
        agregar_archivo(pack.getPuerto_emisor(),new Paquete(mensaje_descifrado, pack.getPuerto_emisor(),pack.getPuerto_receptor()));
        return mensaje_descifrado;

    }
    @FXML
    void send_serverAsimetrico(ActionEvent event) {

        String mensaje_original=txfMensaje.getText();
        int llave=Integer.parseInt(JOptionPane.showInputDialog("Mete la llave"));
        datos.setMensaje(cifrador_cesar(mensaje_original,llave));
        datos.setIScifrado(true);
        datos.setTipo_cifrado("asimetrico");
        send_cifrado(mensaje_original);

    }
    @FXML
    void send_serverSimetrico(ActionEvent event) {
        String mensaje_original=txfMensaje.getText();
        int llave=Integer.parseInt(JOptionPane.showInputDialog("Mete la llave"));
        datos.setMensaje(cifrador_cesar(mensaje_original,llave));
        datos.setIScifrado(true);
        datos.setTipo_cifrado("simetrico");
        send_cifrado(mensaje_original);

    }
    void send_cifrado(String original){
        try{
            Socket misocket=new Socket("127.0.0.1",9020);
            ObjectOutputStream  paquete_datos=new ObjectOutputStream(misocket.getOutputStream());//FLUJO DE DATOS DE SALIDA QUE SIRVE PARA MANDAR OBJETOS
           mensaje_derecha(original);
            //envio a servidor
            paquete_datos.writeObject(datos);
            paquete_datos.close();
            misocket.close();
        }
        catch(IOException e1){
            System.out.println(e1.getMessage());
        }

        agregar_archivo(datos.getPuerto_receptor(),new Paquete(original,datos.getPuerto_emisor(), datos.getPuerto_receptor()));

    }
    @FXML
    void send_serverPlano(ActionEvent event) {
        try{
            Socket misocket=new Socket("127.0.0.1",9020);
            ObjectOutputStream  paquete_datos=new ObjectOutputStream(misocket.getOutputStream());//FLUJO DE DATOS DE SALIDA QUE SIRVE PARA MANDAR OBJETOS
            //Mensajes
            mensaje_derecha(txfMensaje.getText());
            //envio a servidor
            datos.setMensaje(txfMensaje.getText());
            datos.setIScifrado(false);
            agregar_archivo(datos.getPuerto_receptor(),datos);//se manda el receptor porque se manda desde aqui y no es mensaje de
            //para que se manden nuevo objetos ya que si se manda el mismo solo se guarda un mensaje
            paquete_datos.writeObject(datos);
            paquete_datos.close();
            misocket.close();
        }
        catch(IOException e1){
            System.out.println(e1.getMessage());
        }
    }
    void mensaje_derecha(String texto){
        HBox hb=new HBox();
        hb.setAlignment(Pos.CENTER_RIGHT);
        TextFlow txt =new TextFlow(new Text(texto));
        txt.setPadding(new Insets(5,10,5,10));
        hb.getChildren().add(txt);
        VB.getChildren().add(hb);
    }
    String descifrador_cesar(String mensajeOriginal,int llave){
        String conjunto = "abcdefghijklmnñopqrstuvwxyz1234567890 ";
        String textoDescodificado = "";
        mensajeOriginal = mensajeOriginal.toLowerCase();
        char caracter;
        for (int i = 0; i < mensajeOriginal.length(); i++) {
            caracter = mensajeOriginal.charAt(i);
            int p = conjunto.indexOf(caracter);
            //System.out.println("letra "+caracter+" posicion "+pos);
            if(p == -1){
                textoDescodificado += caracter;
            }
            else{
                if(p - llave < 0){
                    textoDescodificado += conjunto.charAt( conjunto.length() + (p - llave) );
                }else{
                    textoDescodificado += conjunto.charAt( (p - llave) % conjunto.length() );
                }
            }

        }
        return textoDescodificado;
    }
    String cifrador_cesar(String mensajeOriginal,int llave){
        String conjunto = "abcdefghijklmnñopqrstuvwxyz1234567890 ";
        String mensajeCifrado = "";
        mensajeOriginal = mensajeOriginal.toLowerCase();
        char caracter;
        for (int i = 0; i < mensajeOriginal.length(); i++) {
            caracter = mensajeOriginal.charAt(i);
            int p = conjunto.indexOf(caracter);
            mensajeCifrado += conjunto.charAt( (p + llave) % conjunto.length() );
        }
        return mensajeCifrado;
    }
    ServerSocket servidor;
    public void run(){
        //Server socket pondra a la app a la escucha de un puerto
        boolean ban=true;
        try{
            servidor=new ServerSocket(datos.getPuerto_emisor());
            //ahora que acepte cualquier conexion que venga del exterior con el metodo accept
            System.out.println("estoy corriendo 1");
            while(ban){

                Socket misocket=servidor.accept();//aceptara las conexiones que vengan del exterior
                ObjectInputStream flujo_entrada=new ObjectInputStream(misocket.getInputStream());
                Paquete data=(Paquete)flujo_entrada.readObject();
                if(data.getPuerto_emisor()==datos.getPuerto_receptor()){
                    String mensaje=data.getMensaje();
                    if(data.getIScifrado()==true){
                        //mensajes_cifrados.offer(data);
                        //mensajes_cifrados.add(data);
                        System.out.println("mensaje cifrado nuevo");
                        mensaje=data.getMensaje();
                        if(!data.getResumen_cifrado().equals("")){
                            mensaje= data.getMensaje()+" \nFirma digital: "+data.getResumen_cifrado();
                        }

                        String finalMensaje1 = mensaje;
                        Platform.runLater(()->{
                            mensaje_izquierdo(data);
                        });

                    }else{
                        agregar_archivo(data.getPuerto_emisor(),data);
                        System.out.println(data.getMensaje());
                        String finalMensaje = mensaje;
                        Platform.runLater(()->{
                            VB.getChildren().add(new Label(finalMensaje));
                        });
                    }
                    //CONTENIDO CHAT
                }
                else{
                    System.out.println("Mensaje de otro chat");
                   // agregar_archivo(data.getPuerto_emisor(),data);
                    try{
                        String archivo=String.valueOf(data.getPuerto_receptor())+String.valueOf(data.getPuerto_emisor());//es puerto emisor si se recibe el mensaje, es puerto receptor si este se manda desde aqui
                        archivo=archivo+".txt";
                        FileInputStream file_i=new FileInputStream(archivo);
                        ObjectInputStream flujo_e=new ObjectInputStream(file_i);
                        mensajes_base_externosAlChatActual=(List<Paquete>)flujo_e.readObject();
                        //debo caragar antes la conversacion porque sino se pierden los mensajes anteriores
                       file_i.close();
                       flujo_e.close();
                        FileOutputStream file_o=new FileOutputStream(archivo);
                        ObjectOutputStream flujo_salida=new ObjectOutputStream(file_o);

                        mensajes_base_externosAlChatActual.add(new Paquete(data.getMensaje(),data.getPuerto_emisor(),data.getPuerto_receptor()));
                        flujo_salida.writeObject(mensajes_base_externosAlChatActual);
                        flujo_salida.close();
                        file_o.close();
                    }
                    catch(IOException e){
                        System.out.println(e);
                    }

                }

                // misocket.close();
                flujo_entrada.close();
                misocket.close();
            }

        }
        catch(IOException|ClassNotFoundException e){
            System.out.println(e);
        }
    }
    public void mensaje_izquierdo(Paquete mensaje){
        HBox h=new HBox();
        Label l=new Label(mensaje.getMensaje());
        Button btn=new Button("Descifrar");
        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int llave=Integer.parseInt(JOptionPane.showInputDialog("Mete la llave"));
                if(mensaje.getTipo_cifrado().equals("simetrico")){
                    String respuesta=descifrarSimetrico(mensaje,llave);
                    l.setText(respuesta);
                }
                else{
                    String respuesta=descifrarAsimetrico(mensaje,llave);
                    l.setText(respuesta);
                }

            }
        });
        h.getChildren().addAll(l,btn);
        VB.getChildren().add(h);

    }

}
