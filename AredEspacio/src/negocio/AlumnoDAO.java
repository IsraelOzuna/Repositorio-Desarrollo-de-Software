package negocio;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import persistencia.AlumnoJpaController;

public class AlumnoDAO implements IAlumno {

    @Override
    public boolean registrarAlumno(Alumno alumno) {
        boolean alumnoRegistradoExitosamente = true;

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
        AlumnoJpaController alumnoJpaController = new AlumnoJpaController(entityManagerFactory);

        persistencia.Alumno nuevoAlumno = new persistencia.Alumno();

        nuevoAlumno.setNombre(alumno.getNombre());
        nuevoAlumno.setApellidos(alumno.getApellidos());
        nuevoAlumno.setCorreoElectronico(alumno.getCorreoElectronico());
        nuevoAlumno.setFechaNacimiento(alumno.getFechaNacimiento());
        nuevoAlumno.setTelefono(alumno.getTelefono());
        nuevoAlumno.setRutaFoto(alumno.getRutaFoto());

        try {
            alumnoJpaController.create(nuevoAlumno);
        } catch (Exception ex) {
            alumnoRegistradoExitosamente = false;
            Logger.getLogger(AlumnoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return alumnoRegistradoExitosamente;
    }

    @Override
    public List<persistencia.Alumno> buscarAlumno(String nombre) {
        List<persistencia.Alumno> alumnosEncontrados = null;

        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("AredEspacioPU", null);
        AlumnoJpaController alumnoJpaController = new AlumnoJpaController(entityManagerFactory);

        persistencia.Alumno alumno = new persistencia.Alumno();
        
        alumno.setNombre(nombre);
        
        try{
            alumnosEncontrados = alumnoJpaController.obtenerAlumnos(nombre);
                        
        }catch(Exception ex){            
            Logger.getLogger(AlumnoDAO.class.getName()).log(Level.SEVERE, null, ex);
        }                
        return alumnosEncontrados;
    }

}
