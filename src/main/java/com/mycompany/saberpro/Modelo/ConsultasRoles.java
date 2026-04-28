/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.saberpro.Modelo;

import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 *
 * @author juanf
 */
public class ConsultasRoles extends Conexion{
    
    public boolean registrar(Roles rol){
        PreparedStatement ps = null;
        Connection con = getConexion();
        String sql = "INSERT INTO roles (nombre) VALUES (?)";
        
        try{
            ps = con.prepareStatement(sql);
            ps.setString(1, rol.getNombre());
            ps.execute();
            return true;
        } catch(SQLException e){
            System.err.println(e);
            return false;
        } finally{
            try{
                con.close();
            } catch(SQLException e){
                System.err.println(e);
            }
        }
    }
    
    public boolean editar(Roles rol, int idOriginal){
        PreparedStatement ps = null;
        Connection con = getConexion();
        String sql = "UPDATE roles SET id_roles=?, nombre=? WHERE id_roles=?";
        
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, rol.getId_roles());
            ps.setString(2, rol.getNombre());
            ps.setInt(3, idOriginal);
            ps.execute();   
            return true;
        } catch(SQLException e){
            System.err.println(e);
            return false;
        } finally{
            try{
                con.close();
            } catch(SQLException e){
                System.err.println(e);
            }
        }
    }
    
    public boolean eliminar(Roles rol){
        PreparedStatement ps = null;
        Connection con = getConexion();
        String sql = "DELETE FROM roles WHERE id_roles=?";
        
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, rol.getId_roles());
            ps.execute();
            return true;
        } catch(SQLException e){
            System.err.println(e);
            return false;
        } finally{
            try{
                con.close();
            } catch(SQLException e){
                System.err.println(e);
            }
        }
    }
    
    public boolean buscar(Roles rol){
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection con = getConexion();
        String sql = "SELECT * FROM roles WHERE id_roles=?";
        
        try{
            ps = con.prepareStatement(sql);
            ps.setInt(1, rol.getId_roles());
            rs = ps.executeQuery();
            if (rs.next()){
                rol.setId_roles(Integer.parseInt(rs.getString("id_roles")));
                rol.setNombre(rs.getString("nombre"));
                return true;
            }
            return false;
        } catch(SQLException e){
            System.err.println(e);
            return false;
        } finally{
            try{
                con.close();
            } catch(SQLException e){
                System.err.println(e);
            }
        }
    }
    
}
