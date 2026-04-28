/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.saberpro.Modelo;

/**
 *
 * @author juanf
 */
public class Usuario{
    private int id_usuario;
    private String nombre;
    private String apellido;
    private String telefono;
    private String cc;
    private String correo;
    private String contrasena;
    private Roles rol;
    private boolean habilitado;
    
    public Usuario(){}
    public static Usuario usuarioActual;

    
    public Usuario(int id_usuario, String nombre, String apellido, String telefono, String cc, String correo, String contrasena, Roles rol, boolean habilitado){
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.cc = cc;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.habilitado = habilitado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public Roles  getRol(){
        return rol;
    }
    
    public void setRol(Roles rol){
        this.rol = rol;
    }
    
}
