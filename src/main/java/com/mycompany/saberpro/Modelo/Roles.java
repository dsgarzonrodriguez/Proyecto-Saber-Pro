/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.saberpro.Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

/**
 *
 * @author juanf
 */
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
    
    public Vector<Roles> mostrarRoles(){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Conexion conn = new Conexion();
        Connection con = conn.getConexion();
        Vector<Roles> datos = new Vector<Roles>();
        Roles dat = null;
        
        try{
            String sql = "SELECT * FROM roles";
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            
            dat = new Roles();
            dat.setId_roles(0);
            dat.setNombre("Selecciona un rol");
            datos.add(dat);
            
            while(rs.next()){
                dat = new Roles();
                dat.setId_roles(rs.getInt("id_roles"));
                dat.setNombre(rs.getString("nombre"));
                datos.add(dat);
            }
            rs.close();
            
        } catch(SQLException e){
            System.err.println(e.toString());
        }
        return datos;
    }
    
}
