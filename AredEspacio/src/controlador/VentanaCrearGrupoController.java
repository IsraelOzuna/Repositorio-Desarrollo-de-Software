package controlador;

import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import negocio.CuentaDAO;
import negocio.GrupoDAO;
import persistencia.Maestro;
import negocio.MaestroDAO;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import persistencia.Cuenta;
import persistencia.Grupo;
import persistencia.Horario;

public class VentanaCrearGrupoController implements Initializable {

    @FXML
    private JFXButton botonGurdar;
    @FXML
    private JFXButton botonCancelar;
    @FXML
    private Label etiquetaCrearGrupo;
    @FXML
    private Label etiquetaNombre;
    @FXML
    private Label etiquetaPrecioMensualidad;
    @FXML
    private Label etiquetaInscripción;
    @FXML
    private TextField campoMensualidad;
    @FXML
    private TextField campoInscripcion;
    private String nombreUsuario;
    @FXML
    private TextField campoNombreGrupo;
    @FXML
    private ComboBox<String> comboBoxMaestro;
    @FXML
    private AnchorPane panelCrearGrupos;
    @FXML
    private Label etiquetaMaestro;
    private String unidadPersistencia="AredEspacioPU";
    List<Maestro> listaMaestros=new ArrayList();
   

    
    
    public void iniciarVentana(String nombreUsuarioActual){
        nombreUsuario=nombreUsuarioActual;
        ObservableList<String> maestros =FXCollections.observableArrayList();
        MaestroDAO maestroDAO = new MaestroDAO();
        GrupoDAO grupoDAO = new GrupoDAO(unidadPersistencia);/////////
        listaMaestros=maestroDAO.adquirirMaestros();
        for(int i=0; i<listaMaestros.size(); i++){///////
                maestros.add(listaMaestros.get(i).getNombre());
        }
        
        comboBoxMaestro.setItems(maestros);
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
    private void registrarGrupo(ActionEvent event) throws IOException {
        
        if(!existenCamposVacios()){
            if(!existenCamposExcedidos()){
                if(!nombreGrupoRepetido()){
                    GrupoDAO nuevoGrupoDAO = new GrupoDAO(unidadPersistencia);
                    MaestroDAO maestroDAO = new MaestroDAO();
                    CuentaDAO cuentaDAO = new CuentaDAO();
                    Cuenta nuevaCuenta=new Cuenta();
                    nuevaCuenta=cuentaDAO.obtenerCuentaMaestro(comboBoxMaestro.getValue());
                    nuevaCuenta.setUsuario(nuevaCuenta.getUsuario());
                    Maestro maestroEditar=new Maestro();
                    for(int i=0; i<listaMaestros.size(); i++){
                        if(listaMaestros.get(i).getNombre().equals(comboBoxMaestro.getValue())){
                            maestroEditar=listaMaestros.get(i);
                        }
                    }
                    maestroEditar.setEstaActivo(1);
                    maestroDAO.editarMaestro(maestroEditar);
                    Grupo nuevoGrupo = new Grupo();
                    nuevoGrupo.setNombreGrupo(campoNombreGrupo.getText());
                    nuevoGrupo.setUsuario(nuevaCuenta);
                    nuevoGrupo.setInscripcion(Double.parseDouble(campoInscripcion.getText()));
                    nuevoGrupo.setMensualidad(Double.parseDouble(campoMensualidad.getText()));
                    nuevoGrupo.setFechaPago(new Date());
                    nuevoGrupo.setEstaActivo(1);
                    if(nuevoGrupoDAO.crearGrupo(nuevoGrupo)){
                        DialogosController.mostrarMensajeInformacion("Creado", "El grupo ha sido creado", "Grupo creado exitosamente");
                        FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaConsultarGrupos.fxml"));
                        Parent root = (Parent) loader.load();
                        VentanaConsultarGruposController ventanaConsultarGruposController = loader.getController();
                        ventanaConsultarGruposController.iniciarVentana(nombreUsuario);
                        panelCrearGrupos.getChildren().add(root); 
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
    private void cancelarRegistro(ActionEvent event) throws IOException {
            FXMLLoader loader = new FXMLLoader(VentanaMenuDirectorController.class.getResource("/vista/VentanaConsultarGrupos.fxml"));
            Parent root = (Parent) loader.load();
            VentanaConsultarGruposController ventanaConsultarGruposController = loader.getController();
            ventanaConsultarGruposController.iniciarVentana(nombreUsuario);
            panelCrearGrupos.getChildren().add(root); 
    }
    
    public boolean existenCamposVacios(){
        boolean camposVacios=false;
        if(campoInscripcion.getText().isEmpty()){
            camposVacios=true;
        }
        if(campoMensualidad.getText().isEmpty()){
            camposVacios=true;
        }
        if(campoNombreGrupo.getText().isEmpty()){
            camposVacios=true;
        }
        if(comboBoxMaestro.getValue()==null){
            camposVacios=true;
        }
        
        return camposVacios;
    }
    
    public boolean existenCamposExcedidos() {
        boolean campoExcedido = false;

        if (campoNombreGrupo.getText().length() > 100) {
            campoExcedido = true;
        } else if (campoMensualidad.getText().length() > 10) {
            campoExcedido = true;
        } else if (campoInscripcion.getText().length() > 10) {
            campoExcedido = true;
        } 
        return campoExcedido;
    }
    
    public boolean nombreGrupoRepetido(){
        boolean repetido=true;
        GrupoDAO grupoDAO = new GrupoDAO(unidadPersistencia);
        Cuenta usuario = new Cuenta();
        List<Grupo> listaGrupos;
        String grupoActual;
        listaGrupos=grupoDAO.adquirirGrupos(usuario);
        for(int i=0;i<listaGrupos.size();i++){
            if(listaGrupos.get(i).getEstaActivo()==1){
                grupoActual=listaGrupos.get(i).getNombreGrupo();
                repetido = grupoActual.equals(campoNombreGrupo.getText());
                if(repetido){
                    break;
                }
            }
        }
        return repetido;
    }
    
    
}
