package utn.crud.tpfinal.Controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utn.crud.tpfinal.Models.LoginCredential;
import utn.crud.tpfinal.Models.Persona;
import utn.crud.tpfinal.Services.PersonaService;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/personas")
public class PersonaController {

    @Autowired
    private PersonaService personaService;

    @PostMapping("/login")
    public LoginCredential login(@RequestBody Map<String, String> datos) {
        return personaService.login(datos.get("email"), datos.get("password")).orElse(null);
    }

    @GetMapping
    public List<Persona> listar() {
        return personaService.getAll();
    }

    @PostMapping
    public Persona crear(@RequestBody Persona persona) {
        return personaService.save(persona);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        personaService.delete(id);
    }
}
