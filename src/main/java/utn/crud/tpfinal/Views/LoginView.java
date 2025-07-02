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
import utn.crud.tpfinal.Models.LoginCredential;
import utn.crud.tpfinal.Services.PersonaService;

import java.util.Optional;

@Route("") //vista raíz
public class LoginView extends VerticalLayout {

    private final PersonaService personaService;

    // Componentes de la UI
    private final TextField email = new TextField("Email");
    private final PasswordField password = new PasswordField("Contraseña");
    private final Button loginButton = new Button("Iniciar Sesión");

    // Enlace para navegar a la vista de registro completo
    private final RouterLink registerLink = new RouterLink("¿No tienes cuenta? Regístrate aquí", RegisterView.class);

    @Autowired
    public LoginView(PersonaService personaService) {
        this.personaService = personaService;

        // Centra los componentes horizontalmente y verticalmente
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        setSizeFull();

        // Añadir los componentes al layout en el orden deseado
        add(
                new H2("Bienvenido"), // Título
                email,                // Campo de email
                password,             // Campo de contraseña
                loginButton,          // Botón para iniciar sesión
                registerLink          // Enlace para ir al registro
        );

        //evento para el boton iniciar sesión
        loginButton.addClickListener(e -> {
            String userEmail = email.getValue();
            String userPassword = password.getValue();

            //servicio login
            Optional<LoginCredential> login = personaService.login(userEmail, userPassword);

            if (login.isPresent()) {

                Notification.show("Inicio de sesión exitoso para: " + login.get().getEmail(), 3000, Notification.Position.MIDDLE);
                getUI().ifPresent(ui -> ui.navigate("/personas"));
            } else {
                Notification.show("Email o contraseña incorrectos.", 3000, Notification.Position.MIDDLE);
            }
        });
    }
    private void limpiarCampos() {
        email.clear();
        password.clear();
    }
}
