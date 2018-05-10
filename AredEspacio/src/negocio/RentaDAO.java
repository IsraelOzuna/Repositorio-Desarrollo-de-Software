/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package negocio;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import persistencia.RentaJpaController;

/**
 *
 * @author Irdevelo
 */
public class RentaDAO implements IRenta {

    @Override
    public List<persistencia.Renta> obtenerRentas() {
        List<persistencia.Renta> listaRentas = null;
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
        RentaJpaController rentaJpaController = new RentaJpaController(entityManagerFactory);

        persistencia.Renta nuevaRenta = new persistencia.Renta();

        try {

            listaRentas = rentaJpaController.findRentaEntities();

        } catch (Exception ex) {

            Logger.getLogger(RentaDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return listaRentas;

    }
}