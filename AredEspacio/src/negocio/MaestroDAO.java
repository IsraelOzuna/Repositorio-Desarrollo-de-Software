/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import persistencia.MaestroJpaController;

/**
 *
 * @author Irdevelo
 */
public class MaestroDAO implements IMaestro {

    @Override
    public boolean registrarMaestro(Maestro maestro) {
        boolean maestroRegistradoExitosamente = false;

        if (maestro.getUsuario() != null) {
            maestroRegistradoExitosamente = true;
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
            MaestroJpaController maestroJpaController = new MaestroJpaController(entityManagerFactory);

            persistencia.Maestro maestroNuevo = new persistencia.Maestro();

            maestroNuevo.setNombre(maestro.getNombre());
            maestroNuevo.setApellidos(maestro.getApellidos());
            maestroNuevo.setCorreoElectronico(maestro.getCorreoElectronico());
            maestroNuevo.setTelefono(maestro.getTelefono());
            maestroNuevo.setEstaActivo(maestro.getEstaActivo());
            maestroNuevo.setFechaCorte(maestro.getFechaCorte());
            maestroNuevo.setRutaFoto(maestro.getRutaFoto());
            maestroNuevo.setMensualidad(maestro.getMensualidad());
            maestroNuevo.setUsuario(maestro.getUsuario());

            try {
                maestroJpaController.create(maestroNuevo);

            } catch (Exception ex1) {
                maestroRegistradoExitosamente = false;
                Logger.getLogger(MaestroDAO.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } else {
            maestroRegistradoExitosamente = false;
        }
        return maestroRegistradoExitosamente;
    }

    @Override
    public List<persistencia.Maestro> buscarMaestro(String nombre) {
        List<persistencia.Maestro> maestrosEncontrados = null;

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
        MaestroJpaController maestroJpaController = new MaestroJpaController(entityManagerFactory);

        persistencia.Maestro maestro = new persistencia.Maestro();

        maestro.setNombre(nombre);

        try {
            maestrosEncontrados = maestroJpaController.obtenerMaestros(nombre);

        } catch (Exception ex) {
            Logger.getLogger(AlumnoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return maestrosEncontrados;
    }

    @Override
    public int obtenerNumeroMaestros() {
        int numeroMaestros = 0;
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
        MaestroJpaController maestroJpaController = new MaestroJpaController(entityManagerFactory);
        numeroMaestros = maestroJpaController.getMaestroCount();
        return numeroMaestros;
    }

    @Override
    public List<persistencia.Maestro> adquirirMaestros() {
        List<persistencia.Maestro> listaMaestros = null;
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
        MaestroJpaController maestroJpaController = new MaestroJpaController(entityManagerFactory);
        listaMaestros = maestroJpaController.findMaestroEntities();
        return listaMaestros;
    }

    @Override
    public boolean editarMaestro(persistencia.Maestro maestro) {
        boolean datosModificacdosExitosamente = false;

        if (maestro.getUsuario() != null) {
            datosModificacdosExitosamente = true;
            EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
            MaestroJpaController maestroJpaController = new MaestroJpaController(entityManagerFactory);

            persistencia.Maestro nuevoMaestro = maestro;

            nuevoMaestro.setApellidos(maestro.getApellidos());
            nuevoMaestro.setCorreoElectronico(maestro.getCorreoElectronico());
            nuevoMaestro.setEstaActivo(maestro.getEstaActivo());
            nuevoMaestro.setFechaCorte(maestro.getFechaCorte());
            nuevoMaestro.setMensualidad(maestro.getMensualidad());
            nuevoMaestro.setNombre(maestro.getNombre());
            nuevoMaestro.setRutaFoto(maestro.getRutaFoto());
            nuevoMaestro.setTelefono(maestro.getTelefono());
            nuevoMaestro.setUsuario(maestro.getUsuario());

            try {
                maestroJpaController.edit(nuevoMaestro);
            } catch (Exception ex) {
                datosModificacdosExitosamente = true;
                Logger.getLogger(MaestroDAO.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return datosModificacdosExitosamente;
    }

    @Override
    public String adquirirNombreMaestroPorNombreDeUsuario(String nombreUsuario) {
        String nombreMaestro="";
        List<persistencia.Maestro> maestros = new ArrayList();
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
        MaestroJpaController maestroJpaController = new MaestroJpaController(entityManagerFactory);
        maestros=maestroJpaController.findMaestroEntities();
        for(int i = 0; i<maestros.size(); i++){
            if(maestros.get(i).getUsuario().equals(nombreUsuario)){
                nombreMaestro=maestros.get(i).getNombre();
            }
        }
        return nombreMaestro;
    }

    @Override
    public persistencia.Maestro adquirirMaestro(String nombreMaestro) {
        persistencia.Maestro maestroAdquirido=new persistencia.Maestro();
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
        MaestroJpaController maestroJpaController = new MaestroJpaController(entityManagerFactory);
        maestroAdquirido=maestroJpaController.findMaestro(nombreMaestro);
        return maestroAdquirido;
    }

    @Override
    public List<persistencia.Maestro> adquirirMaestrosPorFechaCorte(Date fechaCorte) {
        List<persistencia.Maestro> listaMaestros = null;
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
        MaestroJpaController maestroJpaController = new MaestroJpaController(entityManagerFactory);

        try {

            listaMaestros = maestroJpaController.obtenerMaestrosPorFechaCorte(fechaCorte);

        } catch (Exception ex) {

            Logger.getLogger(RentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listaMaestros;
    }
}
