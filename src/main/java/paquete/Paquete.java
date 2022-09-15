package paquete;

import java.io.Serializable;

//serializacion indicar qye laclase se puede convertir en una serie de datos para poder ser enviada por la red
public class Paquete implements Serializable   {
    private String mensaje;
    private int puerto_emisor;
    private int puerto_receptor;

    private String resumen_cifrado="";

    private String tipo_cifrado="";

    public String getTipo_cifrado() {
        return tipo_cifrado;
    }

    public void setTipo_cifrado(String tipo_cifrado) {
        this.tipo_cifrado = tipo_cifrado;
    }

    public boolean isIScifrado() {
        return IScifrado;
    }

    private boolean IScifrado=false;//para saber si el mensaje viene cifrado o no

    public boolean getIScifrado() {
        return IScifrado;
    }

    public void setIScifrado(boolean IScifrado) {
        this.IScifrado = IScifrado;
    }

    public Paquete(String mensaje, int puerto_emisor, int puerto_receptor) {
        this.mensaje = mensaje;
        this.puerto_emisor = puerto_emisor;
        this.puerto_receptor = puerto_receptor;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public int getPuerto_emisor() {
        return puerto_emisor;
    }

    public void setPuerto_emisor(int puerto_emisor) {
        this.puerto_emisor = puerto_emisor;
    }

    public int getPuerto_receptor() {
        return puerto_receptor;
    }

    public void setPuerto_receptor(int puerto_receptor) {
        this.puerto_receptor = puerto_receptor;
    }
    public String getResumen_cifrado() {
        return resumen_cifrado;
    }

    public void setResumen_cifrado(String resumen_cifrado) {
        this.resumen_cifrado = resumen_cifrado;
    }
}