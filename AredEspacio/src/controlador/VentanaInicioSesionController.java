package controlador;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import negocio.CuentaDAO;
import negocio.Utileria;

/**
 * Este controlador es usado para manipular la vista del inicio de sesión de los
 * usuarios
 *
 * @author Israel Reyes Ozuna
 * @version 1.0 / 5 de junio de 2018
 */
public class VentanaInicioSesionController implements Initializable {

    @FXML
    private JFXTextField campoUsuario;
    @FXML
    private JFXPasswordField campoContrasena;
    @FXML
    private Label etiquetaErrorUsuario;
    @FXML
    private Label etiquetaErrorContrasena;
    @FXML
    private JFXButton botonCerrar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }

    @FXML
    private void ingresar(ActionEvent event) {
        CuentaDAO cuentaDAO = new CuentaDAO();
        limpiarEtiquetas();
        if (!existenCamposVacios() && !existenCamposExcedidos()) {
            try {
                switch (cuentaDAO.iniciarSesion(campoUsuario.getText().trim(), Utileria.cifrarContrasena(campoContrasena.getText()))) {
                    case "Director":
                        desplegarMenuDirector(event);
                        break;
                    case "Maestro":
                        desplegarMenuMaestro(event);
                        break;
                    default:
                        DialogosController.mostrarMensajeInformacion("No encontrado", "No se puede iniciar", "El usuario y la contraseña no coinciden");
                        break;
                }
            } catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(VentanaInicioSesionController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @FXML
    private void limitarUsuario(KeyEvent event) {
        char caracter = event.getCharacter().charAt(0);

        if (campoUsuario.getText().trim().length() >= 50 || !(Character.isDigit(caracter) || Character.isLetter(caracter))) {
            event.consume();
        }
    }

    /**
     * Este método permite que no se dejen campos obligatorios del cliente en
     * blanco
     *
     * @return boolean que verifica si existen o no campos vacíos
     * @since 1.0 / 5 de junio de 2018
     */
    public boolean existenCamposVacios() {
        boolean camposVacios = false;

        if (campoUsuario.getText().trim().isEmpty()) {
            camposVacios = true;
            etiquetaErrorUsuario.setText("Llena este campo");
        }

        if (campoContrasena.getText().trim().isEmpty()) {
            camposVacios = true;
            etiquetaErrorContrasena.setText("Llena este campo");
        }

        return camposVacios;
    }

    /**
     * Este método verifica que los datos ingresados por el usuario no excedan
     * el limite para poder buscarlos en el sistema
     *
     * @return boolean que verifica si existen o no campos excedidos
     * @since 1.0 / 5 de junio de 2018
     */
    public boolean existenCamposExcedidos() {
        boolean camposExedidos = false;
        if (campoUsuario.getText().trim().length() > 50) {
            etiquetaErrorUsuario.setText("El usuario solo puede tener 50 caracteres");
        }
        return camposExedidos;
    }

    /**
     * Este método quita el contenido de las etiquetas que se muestran en caso
     * de algún error
     *
     * @since 1.0 / 5 de junio de 2018
     */
    public void limpiarEtiquetas() {
        etiquetaErrorUsuario.setText("");
        etiquetaErrorContrasena.setText("");
    }

    /**
     * Este método lanza la ventana del menú cuando se ingresa al sistema como
     * director
     *
     * @param event
     * @since 1.0 / 5 de junio de 2018
     */
    public void desplegarMenuDirector(ActionEvent event) {
        FXMLLoader loger = new FXMLLoader(getClass().getResource("/vista/VentanaMenuDirector.fxml"));
        Parent root = null;
        try {
            root = (Parent) loger.load();
        } catch (IOException ex) {
            Logger.getLogger(VentanaInicioSesionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        VentanaMenuDirectorController menuDirector = loger.getController();
        menuDirector.obtenerUsuario(campoUsuario.getText());
        Stage menu = new Stage();
        menu.setScene(new Scene(root));
        menu.show();
        Stage ventanaAnterior = (Stage) ((Node) event.getSource()).getScene().getWindow();
        ventanaAnterior.close();
    }

    /**
     * Este método lanza la ventana del menú cuando se ingresa al sistema como
     * maestro
     *
     * @param event
     * @since 1.0 / 5 de junio de 2018
     */
    public void desplegarMenuMaestro(ActionEvent event) {
        FXMLLoader loger = new FXMLLoader(getClass().getResource("/vista/VentanaMenuDirector.fxml"));
        Parent root = null;
        try {
            root = (Parent) loger.load();
        } catch (IOException ex) {
            Logger.getLogger(VentanaInicioSesionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        VentanaMenuDirectorController menuMaestro = loger.getController();
        menuMaestro.obtenerUsuario(campoUsuario.getText());
        menuMaestro.ocultarBotones();
        Stage menu = new Stage();
        menu.setScene(new Scene(root));
        menu.show();
        Stage ventanaAnterior = (Stage) ((Node) event.getSource()).getScene().getWindow();
        ventanaAnterior.close();
    }

    @FXML
    private void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) botonCerrar.getScene().getWindow();
        stage.close();
    }
}
