package com.example.ejercicio1pmo1.transacciones;

public class usuarios {

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getFechanac() {
        return fechanac;
    }

    public void setFechanac(String fechanac) {
        this.fechanac = fechanac;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getUrlImagen() {
        return urlImagen;
    }

    public void setUrlImagen(String urlImagen) {
        this.urlImagen = urlImagen;
    }



    public usuarios( String nombre, String fechanac, String apellido, String urlImagen, String id) {

        this.nombre = nombre;
        this.fechanac = fechanac;
        this.apellido = apellido;
        this.urlImagen = urlImagen;
        this.id = id;
    }

    public usuarios() {

    }

    String nombre;
    String fechanac;
    String apellido;
    String urlImagen;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    String id;
}