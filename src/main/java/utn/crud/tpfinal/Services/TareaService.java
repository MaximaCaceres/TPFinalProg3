package utn.crud.tpfinal.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import utn.crud.tpfinal.Models.Persona; // <-- ¡NUEVO! Importar Persona
import utn.crud.tpfinal.Models.Tarea;
import utn.crud.tpfinal.Repository.JPA.TareaRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TareaService {

    private final TareaRepository tareaRepository;

    @Autowired
    public TareaService(TareaRepository tareaRepository) {
        this.tareaRepository = tareaRepository;
    }

    public List<Tarea> getAllTareas() {
        return tareaRepository.findAll();
    }

    public Optional<Tarea> getTareaById(Long id) {
        return tareaRepository.findById(id);
    }

    public Tarea saveTarea(Tarea tarea) {
        return tareaRepository.save(tarea);
    }

    public void deleteTarea(Long id) {
        tareaRepository.deleteById(id);
    }


    public List<Tarea> getTareasByPersonaId(Long personaId) {
        return tareaRepository.findByPersonaId(personaId);
    }

    public Tarea updateTareaCompletadaStatus(Long tareaId, boolean completada) {
        Optional<Tarea> optionalTarea = tareaRepository.findById(tareaId);
        if (optionalTarea.isPresent()) {
            Tarea tarea = optionalTarea.get();
            tarea.setCompletada(completada);
            return tareaRepository.save(tarea);
        }
        return null;
    }

    //Cuenta el número de tareas asociadas a una persona específica.
    public long countByPersona(Persona persona) {
        return tareaRepository.countByPersona(persona);
    }
}