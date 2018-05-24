/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import persistencia.AsistenciaJpaController;
import persistencia.Grupo;

/**
 *
 * @author Renato
 */
public class AsistenciaDAO implements IAsistenciaDAO{
    private String unidadPersistencia="AredEspacioPU";
    
    public AsistenciaDAO(){
    }
    
    public AsistenciaDAO(String unidadPersistencia){
        this.unidadPersistencia=unidadPersistencia;
    }
    
    @Override
    public List<persistencia.Asistencia> obtenerAsistencia(Date fecha, int idGrupo) {
        boolean asistio=false;
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
        AsistenciaJpaController asistenciaJpaController = new AsistenciaJpaController(entityManagerFactory);
        List<persistencia.Asistencia> listaAsistencias;
        
        listaAsistencias=asistenciaJpaController.obtenerAsistenciasPorFecha(fecha);
        listaAsistencias=obtenerListaPorGrupo(listaAsistencias, idGrupo);
        
        
        return listaAsistencias;
    }
    
    public List<persistencia.Asistencia> obtenerListaPorGrupo(List<persistencia.Asistencia> listaPorFecha, int idGrupo){
        List<persistencia.Asistencia> listaPorGrupo= new ArrayList();
        for(int i=0; i<listaPorFecha.size(); i++){
            if(listaPorFecha.get(i).getIdGrupo().getIdGrupo() == idGrupo){
                listaPorGrupo.add(listaPorFecha.get(i));
            }
        }
        return listaPorGrupo;
    }
    public List<persistencia.Asistencia> obtenerListaPorAlumno(List<persistencia.Asistencia> listaPorGrupo, int idAlumno){
        List<persistencia.Asistencia> listaPorAlumno= new ArrayList();
        for(int i=0; i<listaPorGrupo.size(); i++){
            if(listaPorGrupo.get(i).getIdAlumno().getIdAlumno() == idAlumno){
                listaPorGrupo.add(listaPorGrupo.get(i));
            }
        }
        return listaPorAlumno;
    }

    @Override
    public boolean RegistrarAsistencia(int idAlumno, int idGrupo, Date fecha, String asistio) {
        boolean registrada=false;
        boolean asistenciaRepetida=false;
        boolean alumnoEncontrado=false;
        int contador=0;
        int idAsistencia=0;
        GrupoDAO grupoDAO = new GrupoDAO(unidadPersistencia);
        Grupo grupo = grupoDAO.adquirirGrupo(idGrupo);
        AlumnoDAO alumnoDAO = new AlumnoDAO();
        persistencia.Alumno alumno = alumnoDAO.adquirirAlumno(idAlumno);
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
        AsistenciaJpaController asistenciaJpaController = new AsistenciaJpaController(entityManagerFactory);
        persistencia.Asistencia asistencia= new persistencia.Asistencia();
        asistencia.setIdAlumno(alumno);
        asistencia.setIdGrupo(grupo);
        asistencia.setFecha(fecha);
        asistencia.setAsistio(asistio);
        ///////////////////////////////////////Validar que la asistencia no se repita
        List<persistencia.Asistencia> listaAsistencias = new ArrayList();
        listaAsistencias=obtenerAsistencia(fecha, idGrupo);
        while(!alumnoEncontrado || contador>listaAsistencias.size()){
            if(listaAsistencias.get(contador).getIdAlumno().getIdAlumno()==idAlumno){
                alumnoEncontrado=true;
                asistenciaRepetida=true;
                idAsistencia=listaAsistencias.get(contador).getIdAsistencia();
            }else{
                alumnoEncontrado=false;
                asistenciaRepetida=false;
                contador++;
            }
        }
        
        if(asistenciaRepetida){
            try {
                asistencia.setIdAsistencia(idAsistencia);
                asistenciaJpaController.edit(asistencia);
                } catch (Exception ex) {
                    Logger.getLogger(AsistenciaDAO.class.getName()).log(Level.SEVERE, null, ex);
                }

        }else{
            asistenciaJpaController.create(asistencia);
        }
        return registrada;
    }
}
