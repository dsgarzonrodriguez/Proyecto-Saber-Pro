
package com.saberpro.entities;

public class Roles {

    private int id_roles;
    private String nombre;
    
    public Roles(){}
    
    public Roles(int id_roles, String nombre){
        this.id_roles = id_roles;
        this.nombre = nombre;
    } 
    
    public int getId_roles(){
        return id_roles;
    }
    
    public void setId_roles(int id_roles){
        this.id_roles = id_roles;
    }
    
    public String getNombre(){
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }
    
    public String toString(){
        return this.nombre; 
    }
}
