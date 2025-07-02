package utn.crud.tpfinal.Models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity // Indica que esta clase es una entidad JPA y se mapeará a una tabla de base de datos
@Table(name = "persona") // Especifica el nombre de la tabla en la base de datos
public class Persona implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id //clave primaria de la tabla
    @GeneratedValue(strategy = GenerationType.IDENTITY) //(auto-incremento)
    private Long id;

    private String dni;
    private String nombre;
    private String apellido;
    private int edad;

    @Transient //no persistirá en PostgreSQL
    private String email;
    @Transient //no persistirá PostgreSQL
    private String password;

    // Relación One-to-Many: Una Persona puede tener muchas Tareas
    // mappedBy = "persona": Indica que el mapeo de esta relación está en el campo 'persona' de la clase Tarea.
    // cascade = CascadeType.ALL: Las operaciones (persistir, eliminar) realizadas en Persona se propagarán a sus Tareas.
    // orphanRemoval = true: Si una Tarea se desvincula de una Persona, se eliminará de la base de datos.
    // fetch = FetchType.LAZY: Las Tareas asociadas solo se cargarán de la BD cuando se acceda a la lista 'tareas'.
    @OneToMany(mappedBy = "persona", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Tarea> tareas = new ArrayList<>();

    //JPA lo usa para crear instancias.
    public Persona() {
    }

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Getters y setters para la lista
    public List<Tarea> getTareas() {
        return tareas;
    }
    public void setTareas(List<Tarea> tareas) {
        this.tareas = tareas;
    }

    // Métodos de utilidad para manejar la bidireccionalidad de la relación
    public void addTarea(Tarea tarea) {
        this.tareas.add(tarea);
        tarea.setPersona(this); // nos asegura que la Tarea también conoce a su Persona
    }
    public void removeTarea(Tarea tarea) {
        this.tareas.remove(tarea);
        tarea.setPersona(null); // Desvincula la Tarea de esta Persona
    }
}