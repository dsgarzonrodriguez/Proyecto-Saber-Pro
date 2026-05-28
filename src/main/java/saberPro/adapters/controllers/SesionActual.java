package saberPro.adapters.controllers;

import saberPro.entities.Usuario;

public class SesionActual {

    private static Usuario usuario;

    public static Usuario getUsuario() { return usuario; }
    public static void setUsuario(Usuario usuario) { SesionActual.usuario = usuario; }
    public static void cerrarSesion() { SesionActual.usuario = null; }
}