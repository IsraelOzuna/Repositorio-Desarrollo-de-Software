package controlador;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import negocio.CuentaDAO;
import negocio.MaestroDAO;
import negocio.Notificador;
import org.controlsfx.control.PopOver;
import persistencia.Cuenta;

/**
 * Este controlador es usado para manipular la vista en la que el director
 * realiza diferentes tareas. También la usa el maestro pero sin funcionalidades
 * que son exclusivas del director
 *
 * @author Israel Reyes Ozuna
 * @version 1.0 / 5 de junio de 2018
 */
public class VentanaMenuDirectorController implements Initializable {

    @FXML
    private JFXButton botonRecursosReportes;
    @FXML
    private JFXButton botonMaestros;
    @FXML
    private JFXButton botonRenta;
    @FXML
    private Label etiquetaNombreUsuario;
    @FXML
    private Pane panelPrincipal;
    @FXML
    private JFXButton botonClientes;
    @FXML
    private JFXButton botonAnuncios;
    @FXML
    private Button botonNotificaciones;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    @FXML
    private void desplegarVentanaBusquedaAlumno(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaBuscar.fxml"));
        Parent root = (Parent) loader.load();
        VentanaBuscarController ventanaBuscar = loader.getController();
        ventanaBuscar.obtenerSeccion("Alumnos", panelPrincipal, etiquetaNombreUsuario.getText());
        panelPrincipal.getChildren().clear();
        panelPrincipal.getChildren().add(root);
    }

    @FXML
    private void desplegarVentanaBusquedaMaestro(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaBuscar.fxml"));
        Parent root = (Parent) loader.load();
        VentanaBuscarController ventanaBuscar = loader.getController();
        ventanaBuscar.obtenerSeccion("Maestros", panelPrincipal, etiquetaNombreUsuario.getText());
        panelPrincipal.getChildren().clear();
        panelPrincipal.getChildren().add(root);
    }

    @FXML
    private void desplegarVentanaBusquedaRenta(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaRentas.fxml"));
        Parent root = (Parent) loader.load();
        VentanaRentasController ventanaRentas = loader.getController();
        ventanaRentas.obtenerPanel(panelPrincipal);
        ventanaRentas.llenarTablaRentas();
        panelPrincipal.getChildren().clear();
        panelPrincipal.getChildren().add(root);
    }

    @FXML
    private void desplegarVentanaBusquedaGrupos(ActionEvent event) throws IOException {
        MaestroDAO maestroDAO = new MaestroDAO();
        if (maestroDAO.obtenerNumeroMaestros() > 0) {
            FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaConsultarGrupos.fxml"));
            Parent root = (Parent) loader.load();
            VentanaConsultarGruposController ventanaConsultarGruposController = loader.getController();
            ventanaConsultarGruposController.iniciarVentana(etiquetaNombreUsuario.getText());
            panelPrincipal.getChildren().clear();
            panelPrincipal.getChildren().add(root);
        } else {
            DialogosController.mostrarMensajeInformacion("No hay maestros registrados", "No hay maestros registrados", "Debe haber al menos un maestro registrado para realizar esta acción");
        }
    }

    @FXML
    private void desplegarVentanaBusquedaClientes(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaBuscar.fxml"));
        Parent root = (Parent) loader.load();
        VentanaBuscarController ventanaBuscar = loader.getController();
        ventanaBuscar.obtenerSeccion("Clientes", panelPrincipal, etiquetaNombreUsuario.getText());
        panelPrincipal.getChildren().clear();
        panelPrincipal.getChildren().add(root);
    }

    @FXML
    private void desplegarVentanaFacebook(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaEgresosFb.fxml"));
        Parent root = (Parent) loader.load();
        VentanaEgresosFbController ventanaEgresosFb = loader.getController();
        panelPrincipal.getChildren().clear();
        panelPrincipal.getChildren().add(root);
    }

    @FXML
    private void desplegarVentanaRecursos(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaGenerarReporte.fxml"));
        Parent root = (Parent) loader.load();
        VentanaGenerarReporteController ventanaReporte = loader.getController();
        ventanaReporte.iniciarVentana();
        panelPrincipal.getChildren().clear();
        panelPrincipal.getChildren().add(root);
    }

    @FXML
    private void desplegarVentanaPromociones(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaBuscar.fxml"));
        Parent root = (Parent) loader.load();
        VentanaBuscarController ventanaBuscar = loader.getController();
        ventanaBuscar.obtenerSeccion("Promociones", panelPrincipal, etiquetaNombreUsuario.getText());
        ventanaBuscar.ocultarComponentes();
        panelPrincipal.getChildren().clear();
        panelPrincipal.getChildren().add(root);
    }

    /**
     * Este método permite obtener el usuario que ingresó al sistema para poner
     * su nombre y además otorgar o quitar permisos en el sistema de acuerdo a
     * su rol
     *
     * @param usuario quien ingresó al sistema
     * @since 1.0 / 5 de junio de 2018
     */
    public void obtenerUsuario(String usuario) {
        etiquetaNombreUsuario.setText(usuario);
    }

    /**
     * Este método oculta funcionalidades a los maestros que son exclusivas del
     * director
     *
     * @since 1.0 / 5 de junio de 2018
     */
    public void ocultarBotones() {
        botonClientes.setVisible(false);
        botonMaestros.setVisible(false);
        botonRecursosReportes.setVisible(false);
        botonRenta.setVisible(false);
        botonAnuncios.setVisible(false);
    }

    @FXML
    private void mostrarNotificaciones(ActionEvent event) {
        CuentaDAO cuentaDAO = new CuentaDAO();
        Cuenta cuentaUsuario = cuentaDAO.obtenerCuenta(etiquetaNombreUsuario.getText());

        Notificador notificador = new Notificador();
        PopOver pop = new PopOver();

        VBox box = new VBox();
        box.setStyle("-fx-background-color: white;");

        if (cuentaUsuario.getTipoCuenta().equals("Director")) {
            box.getChildren().addAll(notificador.generarNotificacionesDirector());
        } else {
            box.getChildren().addAll(notificador.generarNotificacionesMaestro(cuentaUsuario));
        }

        pop.setContentNode(box);
        pop.setArrowLocation(PopOver.ArrowLocation.TOP_CENTER);
        pop.show(botonNotificaciones);
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        FXMLLoader loger = new FXMLLoader(getClass().getResource("/vista/VentanaInicioSesion.fxml"));
        Parent root = null;
        try {
            root = (Parent) loger.load();
        } catch (IOException ex) {
            Logger.getLogger(VentanaMenuDirectorController.class.getName()).log(Level.SEVERE, null, ex);
        }
        Stage iniciarSesion = new Stage();
        iniciarSesion.setScene(new Scene(root));
        iniciarSesion.initStyle(StageStyle.UNDECORATED);
        iniciarSesion.show();
        Stage ventanaRegistrar = (Stage) ((Node) event.getSource()).getScene().getWindow();
        ventanaRegistrar.close();
    }

}
