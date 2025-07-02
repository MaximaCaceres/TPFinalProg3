package utn.crud.tpfinal.Views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.springframework.beans.factory.annotation.Autowired;
import utn.crud.tpfinal.Models.Persona;
import utn.crud.tpfinal.Services.PersonaService;

@Route("register") //vista de registro
public class RegisterView extends VerticalLayout {

    private final PersonaService personaService;

    private final TextField dni = new TextField("DNI");
    private final TextField nombre = new TextField("Nombre");
    private final TextField apellido = new TextField("Apellido");
    private final TextField edad = new TextField("Edad");
    private final TextField email = new TextField("Email");
    private final PasswordField password = new PasswordField("Contraseña");
    private final Button registerButton = new Button("Confirmar Registro");
    private final RouterLink backToLoginLink = new RouterLink("Volver al Login", LoginView.class);

    @Autowired
    public RegisterView(PersonaService personaService) {
        this.personaService = personaService;


        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();

        add(
                new H2("Registro de Nueva Cuenta"),
                dni,
                nombre,
                apellido,
                edad,
                email,
                password,
                registerButton,
                backToLoginLink
        );


        registerButton.addClickListener(e -> {
            // Validaciones básicas de campos
            if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || edad.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Notification.show("Por favor, rellena todos los campos.");
                return;
            }

            Persona newPersona = new Persona();
            newPersona.setDni(dni.getValue());
            newPersona.setNombre(nombre.getValue());
            newPersona.setApellido(apellido.getValue());
            try {
                newPersona.setEdad(Integer.parseInt(edad.getValue()));
            } catch (NumberFormatException ex) {
                Notification.show("La edad debe ser un número válido.");
                return;
            }
            newPersona.setEmail(email.getValue());
            newPersona.setPassword(password.getValue());

            try {
                personaService.save(newPersona);
                Notification.show("Registro exitoso. Ahora puedes iniciar sesión.");
                limpiarFormulario();
                getUI().ifPresent(ui -> ui.navigate("")); // Navega a LoginView
            } catch (Exception ex) {
                Notification.show("Error al registrar: " + ex.getMessage());

            }
        });
    }

    private void limpiarFormulario() {
        dni.clear();
        nombre.clear();
        apellido.clear();
        edad.clear();
        email.clear();
        password.clear();
    }
}