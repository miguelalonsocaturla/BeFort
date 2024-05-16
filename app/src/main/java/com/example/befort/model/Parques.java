package com.example.befort.model;

import java.util.List;

public class Parques {
    private String nombre;
    private double Latitud ;
    private double Longitud;

    private List<Maquinas> listaMaquinas;


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return Latitud;
    }

    public void setLatitud(double latitud) {
        this.Latitud = latitud;
    }

    public double getLongitud() {
        return Longitud;
    }

    public void setLongitud(double longitud) {
        this.Longitud = longitud;
    }

    public List<Maquinas> getListaMaquinas() {
        return listaMaquinas;
    }

    public void setListaMaquinas(List<Maquinas> listaMaquinas) {
        this.listaMaquinas = listaMaquinas;
    }
}
