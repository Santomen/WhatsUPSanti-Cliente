package com.example.whatsapp_cliente;

import java.util.HashMap;

// Patrón Singleton para sólo tener una instancia de la clase
public class LogicaNegocio {


    public int opcion;

    public int puerto_emi;
    public int puerto_receptor1;
    public int puerto_receptor2;
    public String Nombreemi;
    public String Nombrereceptor1;
    public String Nombrereceptor2;

    private static LogicaNegocio INSTANCE;

    private LogicaNegocio(int opcion) {
        this.opcion=opcion;
        if(opcion==1){
            this.Nombreemi="Santiago";
            this.puerto_emi=10004;
            this.Nombrereceptor1="Pilar";
            this.puerto_receptor1=10000;
            this.Nombrereceptor2="Miguel";
            this.puerto_receptor2=10002;

        } else if (opcion==2) {
            this.Nombreemi="Pilar";
            this.puerto_emi=10000;
            this.Nombrereceptor1="Santiago";
            this.puerto_receptor1=10004;
            this.Nombrereceptor2="Miguel";
            this.puerto_receptor2=10002;
        }
        else {
            this.Nombreemi="Miguel";
            this.puerto_emi=10002;
            this.Nombrereceptor1="Santiago";
            this.puerto_receptor1=10004;
            this.Nombrereceptor2="Pilar";
            this.puerto_receptor2=10000;
        }

    }

    public static LogicaNegocio getInstance(int opcion) {
        if (INSTANCE == null) {
            INSTANCE = new LogicaNegocio(opcion);
        }
        return INSTANCE;
    }
}