package controlador;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import negocio.CuentaDAO;
import negocio.GrupoDAO;
import negocio.MaestroDAO;
import persistencia.Cuenta;
import persistencia.Grupo;
import persistencia.Maestro;

/**
 * Este controlador es usado para establecer los procedimientos que se seguiran 
 * para la edición exitosa de un grupo existente
 *
 * @author Renato Vargas
 * @version 1.0 / 5 de junio de 2018
 */
public class VentanaEditarGrupoController implements Initializable {

    @FXML
    private Label etiquetaEditarGrupo;
    @FXML
    private Label etiquetaNombre;
    @FXML
    private Label etiquetaMensualidad;
    @FXML
    private Label etiquetaMaestro;
    @FXML
    private Label etiquetaInscripcion;
    private String nombreUsuario;
    @FXML
    private TextField campoInscripcion;
    @FXML
    private TextField campoMensualidad;
    @FXML
    private TextField campoNombre;
    @FXML
    private JFXButton botonGuardar;
    @FXML
    private JFXButton botonCancelar;
    @FXML
    private ComboBox<String> comboMaestro;
    @FXML
    private AnchorPane panelEditarGrupo;
    private String nombreInicial;
    private int idGrupoActual;
    private String unidadPersistencia="AredEspacioPU";
    List<Maestro> listaMaestros=new ArrayList();
   
    
    /**
     * Este método establece los datos que se mostraán para ser editados
     * 
     * @param idGrupo el id del grupo que será editado
     * @param nombreUsuarioActual el nombre del usuario para añadir su nombre
     * @since 1.0 / 5 de junio de 2018
     */
    public void establecerGrupo(int idGrupo, String nombreUsuarioActual){
        nombreUsuario=nombreUsuarioActual;
        ObservableList<String> maestros =FXCollections.observableArrayList();
        List<Cuenta> listaUsuarios=null;
        MaestroDAO maestroDAO = new MaestroDAO();
        Grupo grupo;
        GrupoDAO grupoDAO = new GrupoDAO(unidadPersistencia);
        grupo=grupoDAO.adquirirGrupo(idGrupo);
        listaUsuarios=grupoDAO.adquirirCuentas();
        nombreInicial=grupo.getNombreGrupo();
        idGrupoActual=idGrupo;
        campoNombre.setText(nombreInicial);
        campoInscripcion.setText(grupo.getInscripcion().toString());
        campoMensualidad.setText(grupo.getMensualidad().toString());
        listaMaestros=maestroDAO.adquirirMaestros();
        for(int i=0; i<listaMaestros.size(); i++){///////
                maestros.add(listaMaestros.get(i).getNombre());
        }
        maestros.add(nombreUsuarioActual+" (Director)");
        comboMaestro.setItems(maestros);
        comboMaestro.setValue(maestroDAO.adquirirNombreMaestroPorNombreDeUsuario(grupo.getUsuario().getUsuario()));
        
        campoInscripcion.textProperty().addListener((observable, viejoValor, nuevoValor) -> {
            if (!nuevoValor.matches("\\d+\\.\\d*")) {
                campoInscripcion.setText(nuevoValor.replaceAll("[^\\d]", ""));
            }
        });
        campoMensualidad.textProperty().addListener((observable, viejoValor, nuevoValor) -> {
            if (!nuevoValor.matches("\\d+\\.\\d*")) {
                campoMensualidad.setText(nuevoValor.replaceAll("[^\\d]", ""));
            }
        });  
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void guardarGrupoEditado(ActionEvent event) throws IOException {
        if(!existenCamposVacios()){
            if(!existenCamposExcedidos()){
                if(!nombreGrupoRepetido()){
                    if(!nombreGrupoRepetidoInactivo()){
                        GrupoDAO nuevoGrupoDAO = new GrupoDAO(unidadPersistencia);
                        MaestroDAO maestroDAO = new MaestroDAO();
                        CuentaDAO cuentaDAO = new CuentaDAO();
                        Cuenta nuevaCuenta=new Cuenta();
                        if(comboMaestro.getValue().equals(nombreUsuario+" (Director)")){
                            nuevaCuenta=cuentaDAO.obtenerCuenta(nombreUsuario);
                            nuevaCuenta.setUsuario(nuevaCuenta.getUsuario());
                        }else{
                            nuevaCuenta=cuentaDAO.obtenerCuentaMaestro(comboMaestro.getValue());
                            nuevaCuenta.setUsuario(nuevaCuenta.getUsuario());
                            Maestro maestroEditar=new Maestro();
                            for(int i=0; i<listaMaestros.size(); i++){
                                if(listaMaestros.get(i).getNombre().equals(comboMaestro.getValue())){
                                    maestroEditar=listaMaestros.get(i);
                                }
                            }
                            if(maestroEditar.getEstaActivo()==0){
                                maestroEditar.setFechaCorte(new Date());
                            }
                            maestroEditar.setEstaActivo(1);
                            maestroDAO.editarMaestro(maestroEditar);
                        }
                        Grupo grupoEditar = new Grupo();

                        grupoEditar=nuevoGrupoDAO.adquirirGrupo(idGrupoActual);
                        grupoEditar.setNombreGrupo(campoNombre.getText());
                        grupoEditar.setUsuario(nuevaCuenta);
                        grupoEditar.setInscripcion(Double.parseDouble(campoInscripcion.getText()));
                        grupoEditar.setMensualidad(Double.parseDouble(campoMensualidad.getText()));
                        if(nuevoGrupoDAO.editarGrupo(grupoEditar)){
                            DialogosController.mostrarMensajeInformacion("Editado", "El grupo ha sido editado", "El grupo fué editado correctamente");
                            FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaConsultarGrupos.fxml"));
                            Parent root = (Parent) loader.load();
                            VentanaConsultarGruposController ventanaConsultarGruposController = loader.getController();
                            ventanaConsultarGruposController.iniciarVentana(nombreUsuario);
                            panelEditarGrupo.getChildren().add(root); 
                        }else{
                            DialogosController.mostrarMensajeInformacion("Error", "Parece haber ocurrido un error", "Por favor comuniquese con un el encargado del sistema");
                        }
                    }else{
                        DialogosController.mostrarMensajeInformacion("Error", "Parece que ese nombre de grupo ya lo tiene un grupo que se encuentra inactivo", "Para evitar conflictos y si lo que desea es reactivar dicho grupo, le recomendamos que cree un nuevo grupo con ese nombre para reactivarlo");
                    }
                }else{
                    DialogosController.mostrarMensajeInformacion("Grupo repetido", "Un grupo con ese nombre ya existe", "por favor cambie el nombre del grupo");
                }
            }else{
                DialogosController.mostrarMensajeInformacion("Campos Excedidos", "Existen Campos que exceden el numero de caracteres validos", "El nombre del grupo no puede rebasar los 100 caracteres, mientras que los campos de inscripcion y mensualidad no deben exceder los 10 caracteres");
            }
            
        }else{
             DialogosController.mostrarMensajeInformacion("Campos Vacios", "Existen Campos Vacios", "Por favor llene los campos con la información requerida");
        }
        
        
        
    }

    @FXML
    private void cancelarEdicion(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaConsultarGrupos.fxml"));
        Parent root = (Parent) loader.load();
        VentanaConsultarGruposController ventanaConsultarGruposController = loader.getController();
        ventanaConsultarGruposController.iniciarVentana(nombreUsuario);
        panelEditarGrupo.getChildren().add(root);
    }
    
    
    /**
     * Este método verifica la existencia de campos excedios
     *
     * @return boolean que comprueba la existencia de los campos excedidos
     * @since 1.0 / 5 de junio de 2018
     */
    public boolean existenCamposExcedidos() {
        boolean campoExcedido = false;

        if (campoNombre.getText().length() > 100) {
            campoExcedido = true;
        } else if (campoMensualidad.getText().length() > 10) {
            campoExcedido = true;
        } else if (campoInscripcion.getText().length() > 10) {
            campoExcedido = true;
        } 
        return campoExcedido;
    }
    
    /**
     * Este método verifica la existencia de campos vacios
     *
     * @return boolean que comprueba la existencia de los campos vacios
     * @since 1.0 / 5 de junio de 2018
     */
    public boolean existenCamposVacios(){
        boolean camposVacios=false;
        if(campoInscripcion.getText().isEmpty()){
            camposVacios=true;
        }
        if(campoMensualidad.getText().isEmpty()){
            camposVacios=true;
        }
        if(campoNombre.getText().isEmpty()){
            camposVacios=true;
        }
        if(comboMaestro.getValue()==null){
            camposVacios=true;
        }
        
        return camposVacios;
    }
    
    /**
     * Este método verifica si el nombre del grupo ya existe
     *
     * @return boolean que comprueba la existencia del grupo
     * @since 1.0 / 5 de junio de 2018
     */
    public boolean nombreGrupoRepetido(){
        boolean repetido=false;
        GrupoDAO grupoDAO = new GrupoDAO(unidadPersistencia);
        Cuenta usuario = new Cuenta();
        List<Grupo> listaGrupos;
        String grupoActual;
        listaGrupos=grupoDAO.adquirirGrupos(usuario);
        for(int i=0;i<listaGrupos.size();i++){
            grupoActual=listaGrupos.get(i).getNombreGrupo();
            if(listaGrupos.get(i).getEstaActivo()==1){
                if(campoNombre.getText().equals(nombreInicial)){
                    repetido=false;
                }else{
                    System.out.println("Grupo Actual: "+grupoActual+" campo nombre: "+campoNombre.getText());
                    repetido = grupoActual.equals(campoNombre.getText());
                    if(repetido){
                        break;
                    }
                }
            }
            
        }
        return repetido;
    }
    
    /**
     * Este método verifica si el nombre del grupo ya existe pero está inactivo
     *
     * @return boolean que comprueba la existencia del grupo
     * @since 1.0 / 5 de junio de 2018
     */
    public boolean nombreGrupoRepetidoInactivo(){
        boolean repetido=false;
        GrupoDAO grupoDAO = new GrupoDAO(unidadPersistencia);
        Cuenta usuario = new Cuenta();
        List<Grupo> listaGrupos;
        String grupoActual;
        listaGrupos=grupoDAO.adquirirGrupos(usuario);
        for(int i=0;i<listaGrupos.size();i++){
            if(listaGrupos.get(i).getEstaActivo()==0){
                grupoActual=listaGrupos.get(i).getNombreGrupo();
                repetido = grupoActual.equals(campoNombre.getText());
                if(repetido){
                    break;
                }
            }
        }
        return repetido;
    }
    
}
