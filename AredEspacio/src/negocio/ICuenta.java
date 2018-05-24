/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

/**
 *
 * @author Irdevelo
 */
public interface ICuenta {    
    public boolean crearCuenta(Cuenta cuenta);
    public boolean verificarNombreUsuarioRepetido(String nombreUsuario);
    public String iniciarSesion(String nombreUsuario, String contrasena);
    public persistencia.Cuenta obtenerCuentaMaestro(String nombreMaestro);
}
