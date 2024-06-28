package com.example.befort.model;

public class Maquinas {
    private String id;
     private String  Nombre,Tipo, Descripcion;


    public String getId() { return id;}

    public void setId(String id) {this.id = id;}

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getTipo() {
        return Tipo;
    }

    public void setTipo(String tipo) {
        Tipo = tipo;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

}
