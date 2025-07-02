package utn.crud.tpfinal.Models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "logins")
public class LoginCredential implements Serializable {
    @Id
    private String id; // ID de MongoDB
    private String email;
    private String password;
    private Long personaId; //campo para vinclar a la persona de postgre

    public LoginCredential() {
    }

    public LoginCredential(String email, String password, Long personaId) {
        this.email = email;
        this.password = password;
        this.personaId = personaId;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getPersonaId() { // <-- Getter para el nuevo campo
        return personaId;
    }

    public void setPersonaId(Long personaId) { // <-- Setter para el nuevo campo
        this.personaId = personaId;
    }
}
