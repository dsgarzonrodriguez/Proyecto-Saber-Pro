/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.saberpro.Controlador;


import com.mycompany.saberpro.Modelo.ConsultasRoles;
import com.mycompany.saberpro.Modelo.Roles;
import com.mycompany.saberpro.Vista.InterRoles;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

/**
 *
 * @author juanf
 */
public class CtrlRoles implements ActionListener{
    
    private Roles mod;
    private ConsultasRoles modC;
    private InterRoles frm;
    
    public CtrlRoles(){}
    
    public CtrlRoles(Roles mod, ConsultasRoles modC, InterRoles frm){
        this.mod = mod;
        this.modC = modC;
        this.frm = frm;
        this.frm.btnGuardar.addActionListener(this);
        this.frm.btnEliminar.addActionListener(this);
        this.frm.btnEditar.addActionListener(this);
        this.frm.btnBuscar.addActionListener(this);
        this.frm.btnLimpiar.addActionListener(this);
    }

    public void iniciar(){
        frm.txtCodigo.setVisible(false);
    }
    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == frm.btnGuardar){
            mod.setNombre(frm.txtNombre.getText());
            if(modC.registrar(mod)){
                JOptionPane.showMessageDialog(null, "Registro Guardado");
                frm.cargarTablaRoles();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(null, "Error al Guardar");
                limpiar();  
            }
        }
        
        if(e.getSource() == frm.btnEditar){
            int idOriginal = Integer.parseInt(frm.txtId.getText());
            mod.setId_roles(Integer.parseInt(frm.txtId.getText()));
            mod.setNombre(frm.txtNombre.getText());
            if(modC.editar(mod, idOriginal)){
                JOptionPane.showMessageDialog(null, "Registro Editado");
                frm.cargarTablaRoles();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(null, "Error al Editar");
                limpiar();  
            }
        }
        
        if(e.getSource() == frm.btnEliminar){
            mod.setId_roles(Integer.parseInt(frm.txtId.getText()));
            if(modC.eliminar(mod)){
                JOptionPane.showMessageDialog(null, "Registro Eliminado");
                frm.cargarTablaRoles();
                limpiar();
            } else {
                JOptionPane.showMessageDialog(null, "Error al Eliminar");
                limpiar();  
            }
        }
        
        if(e.getSource() == frm.btnBuscar){
            mod.setId_roles(Integer.parseInt(frm.txtId.getText()));
            if(modC.buscar(mod)){
                frm.txtCodigo.setText(String.valueOf(mod.getId_roles()));
                frm.txtNombre.setText(mod.getNombre());
                
            } else {
                JOptionPane.showMessageDialog(null, "No se encontro registro");
                limpiar();  
            }
        }
        if(e.getSource() == frm.btnLimpiar){
            limpiar();
        }   
    }
    
    public void limpiar(){
        frm.txtCodigo.setText(null);
        frm.txtId.setText(null);
        frm.txtNombre.setText(null);
    }
    
    
    
}
