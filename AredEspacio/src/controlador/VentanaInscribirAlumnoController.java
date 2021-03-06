package controlador;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import negocio.AlumnoDAO;
import negocio.CuentaDAO;
import negocio.GrupoDAO;
import negocio.PagoInscripcionAlumnoDAO;
import negocio.PromocionDAO;
import persistencia.Alumno;
import persistencia.Cuenta;
import persistencia.Grupo;
import persistencia.Promocion;

/**
 * Este controlador es usado para la inscripción de un alumno a un grupo, 
 * tomando en cuenta los datos del alumno, del grupo y de las promociones
 *
 * @author Renato Vargas
 * @version 1.0 / 5 de junio de 2018
 */
public class VentanaInscribirAlumnoController implements Initializable {

    @FXML
    private Label etiquetaNombreAlumno;
    @FXML
    private ComboBox<String> comboGrupo;
    @FXML
    private ComboBox<String> comboPromocion;
    @FXML
    private JFXButton botonCrearPromo;
    @FXML
    private Label etiquetaMontoInscripcion;
    @FXML
    private JFXButton botonInscribir;
    @FXML
    private JFXButton botonCancelar;    
    @FXML
    private AnchorPane panelPrincipal;
    private double montoInscripcion;
    private double montoFijoInscripcion;
    private String nombreUsuarioActual;
    private String unidadPersistencia = "AredEspacioPU";
    private int idAlumnoInscribir;
    private Alumno alumnoInscribir;

    @Override
    public void initialize(URL url, ResourceBundle rb) {}

    /**
     * Este método establece los datos que se tendrán cuando se despliegue la
     * ventana, incluyendo grupos y promociones
     *
     * @param alumno sobre el que se llenarán los campos
     * @param nombreUsuario para determinar que tipo de usuario realiza el proceso
     * @since 1.0 / 5 de junio de 2018
     */
    public void llenarCampos(Alumno alumno, String nombreUsuario) {
        nombreUsuarioActual = nombreUsuario;
        etiquetaNombreAlumno.setText(alumno.getNombre() + " " + alumno.getApellidos());
        idAlumnoInscribir = alumno.getIdAlumno();
        alumnoInscribir = alumno;
        ObservableList<String> grupos = FXCollections.observableArrayList();
        List<Grupo> listaGrupos = null;
        CuentaDAO cuentaDAO = new CuentaDAO();
        Cuenta usuario = cuentaDAO.obtenerCuenta(nombreUsuario);
        GrupoDAO grupoDAO = new GrupoDAO(unidadPersistencia);
        listaGrupos = grupoDAO.adquirirGrupos(usuario);
        for (int i = 0; i < listaGrupos.size(); i++) {
            if (listaGrupos.get(i).getEstaActivo() == 1) {
                grupos.add(listaGrupos.get(i).getNombreGrupo());
            }
        }
        comboGrupo.setItems(grupos);

        PromocionDAO promocionDAO = new PromocionDAO();
        List<Promocion> promociones = new ArrayList();
        promociones = promocionDAO.consultarPromociones("Inscripcion");
        ObservableList<String> promocionesCombo = FXCollections.observableArrayList();
        promocionesCombo.add(0, comboPromocion.getPromptText());
        for (int i = 0; i < promociones.size(); i++) {
            promocionesCombo.add(promociones.get(i).getNombrePromocion());
        }
        comboPromocion.setItems(promocionesCombo);
    }

    @FXML
    private void desplegarVentanaCrearPromocion(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaCrearPromocion.fxml"));
        Parent root = (Parent) loader.load();
        VentanaCrearPromocionController ventanaPromocion = loader.getController();
        ventanaPromocion.iniciarVentanaDesdeInscripcion("inscripcion", alumnoInscribir, nombreUsuarioActual);
        panelPrincipal.getChildren().add(root);
    }

    @FXML
    private void inscribirAlumno(ActionEvent event) throws IOException {
        boolean realizarInscripcion = false;
        if (comboGrupo.getValue() != null) {
            GrupoDAO grupoDAO = new GrupoDAO(unidadPersistencia);
            AlumnoDAO alumnoDAO = new AlumnoDAO();
            String nombreGrupoElegido = comboGrupo.getValue();
            Grupo grupoElegido = obtenerGrupo(nombreGrupoElegido);
            List<Alumno> listaAlumnos = grupoDAO.obtenerAlumnos(grupoElegido.getIdGrupo());
            if (listaAlumnos.size() > 0) {
                for (int i = 0; i < listaAlumnos.size(); i++) {
                    if (Objects.equals(listaAlumnos.get(i).getIdAlumno(), alumnoInscribir.getIdAlumno())) {
                        realizarInscripcion = false;
                        break;
                    } else {
                        realizarInscripcion = true;
                    }
                }
            } else {
                realizarInscripcion = true;
            }

            if (realizarInscripcion == true) {
                listaAlumnos.add(alumnoInscribir);
                grupoElegido.setAlumnoCollection(listaAlumnos);
                if (grupoDAO.editarGrupo(grupoElegido)) {
                    if (registrarPagoInscripcion(montoInscripcion, nombreGrupoElegido)) {
                        DialogosController.mostrarMensajeInformacion("Inscrito", "El alumno fue inscrito de forma correcta al grupo", "");
                        FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaBuscar.fxml"));
                        Parent root = (Parent) loader.load();
                        VentanaBuscarController ventanaBuscar = loader.getController();
                        ventanaBuscar.obtenerSeccion("Alumnos", panelPrincipal, nombreUsuarioActual);
                        panelPrincipal.getChildren().add(root);
                    } else {
                        DialogosController.mostrarMensajeInformacion("Error", "Ocurrio un error y no se pudo realizar el pago de la isncripción", "");
                    }
                } else {
                    DialogosController.mostrarMensajeInformacion("Error", "Ocurrio un error y no se pudo realizar la inscripción", "");
                }
            } else {
                DialogosController.mostrarMensajeInformacion("Alumno ya inscrito", "El alumno ya esta inscrito a ese grupo, por favor elija otro grupo", "");
            }
        } else {
            DialogosController.mostrarMensajeInformacion("Error", "Por favor seleccione un grupo", "");
        }
    }

    @FXML
    private void cancelarInscripcion(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaBuscar.fxml"));
        Parent root = (Parent) loader.load();
        VentanaBuscarController ventanaBuscar = loader.getController();
        ventanaBuscar.obtenerSeccion("Alumnos", panelPrincipal, nombreUsuarioActual);
        panelPrincipal.getChildren().add(root);
    }

    @FXML
    private void cambiarMonto(ActionEvent event) {
        comboPromocion.setValue(null);
        String nombreGrupoElegido = comboGrupo.getValue();
        Grupo grupoElegido = obtenerGrupo(nombreGrupoElegido);
        etiquetaMontoInscripcion.setText(grupoElegido.getInscripcion().toString());
        montoInscripcion = grupoElegido.getInscripcion();
        montoFijoInscripcion = grupoElegido.getInscripcion();

    }

    /**
     * Este método obtiene los datos de un grupo con solo su nombre
     *
     * @param nombreGrupo el nombre del grupo cuyos datos se desean obtener
     * @return Grupo con todos los datos del grupo
     * @since 1.0 / 5 de junio de 2018
     */
    public Grupo obtenerGrupo(String nombreGrupo) {
        GrupoDAO grupoDAO = new GrupoDAO(unidadPersistencia);
        List<Grupo> listaGrupos;
        Grupo grupoObtenido = new Grupo();
        Cuenta usuario = new Cuenta();
        listaGrupos = grupoDAO.adquirirGrupos(usuario);
        for (int i = 0; i < listaGrupos.size(); i++) {
            if ((listaGrupos.get(i).getNombreGrupo().equals(nombreGrupo)) && (listaGrupos.get(i).getEstaActivo() == 1)) {
                grupoObtenido = listaGrupos.get(i);
            }
        }
        return grupoObtenido;
    }

    /**
     * Este realiza el registro de un pago de inscripcion
     *
     * @param monto monto que se pagará por la inscripción
     * @param nombreGrupo grupo al que se inscribirá al alumno 
     * @return boolean que valida que una pago se haya registrado exitosamente
     * @since 1.0 / 5 de junio de 2018
     */
    public boolean registrarPagoInscripcion(double monto, String nombreGrupo) {
        boolean pagoRegistrado = false;
        PagoInscripcionAlumnoDAO pagoInscripcion = new PagoInscripcionAlumnoDAO();
        pagoInscripcion.registrarInscripcion(monto, idAlumnoInscribir, nombreGrupo, new Date());
        pagoRegistrado = true;
        return pagoRegistrado;
    }

    @FXML
    private void aplicarPromocion(ActionEvent event) {
        if (comboPromocion.getValue() != null && !comboPromocion.getSelectionModel().getSelectedItem().equals(comboPromocion.getPromptText())) {
            montoInscripcion = montoFijoInscripcion;
            PromocionDAO promocionDAO = new PromocionDAO();
            Promocion promocionSeleccionada = new Promocion();
            promocionSeleccionada = promocionDAO.adquirirPromocionPorNombre(comboPromocion.getValue());
            double valor = promocionSeleccionada.getPorcentajeDescuento();
            double descuento = montoInscripcion * (valor / 100);
            montoInscripcion = montoInscripcion - descuento;
            etiquetaMontoInscripcion.setText(Double.toString(montoInscripcion));
        }
    }

}
