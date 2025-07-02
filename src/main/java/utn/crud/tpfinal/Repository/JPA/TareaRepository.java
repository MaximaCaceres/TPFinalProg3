package utn.crud.tpfinal.Repository.JPA;

import org.springframework.data.jpa.repository.JpaRepository;
import utn.crud.tpfinal.Models.Persona;
import utn.crud.tpfinal.Models.Tarea;

import java.util.List;

// JpaRepository<TipoDeEntidad, TipoDeIdDeLaEntidad>
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findByPersonaId(Long personaId);

    long countByPersona(Persona persona);

}
