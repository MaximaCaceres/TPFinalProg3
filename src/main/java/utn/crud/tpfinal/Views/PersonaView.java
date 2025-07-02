package utn.crud.tpfinal.Views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.orderedlayout.FlexComponent;

import utn.crud.tpfinal.Models.Persona;
import utn.crud.tpfinal.Services.PersonaService;
import utn.crud.tpfinal.Services.TareaService;

import java.util.List;
import java.util.stream.Collectors;

@Route("/personas") //vista principal
@PageTitle("Gestión de Personas")
public class PersonaView extends VerticalLayout {

    private final PersonaService personaService;
    private final TareaService tareaService;

    private Grid<Persona> grid = new Grid<>(Persona.class);
    private TextField filterText = new TextField();
    private Button addPersonButton;
    private Button editButton;
    private Button deleteButton;
    private Button organizeTasksButton;


    private Dialog editorDialog;
    private TextField nombreField;
    private TextField apellidoField;
    private IntegerField edadField;
    private TextField dniField;
    private EmailField emailField;
    private PasswordField passwordField;
    private Binder<Persona> binder = new Binder<>(Persona.class);
    private Persona currentPerson;


    public PersonaView(PersonaService personaService, TareaService tareaService) {
        this.personaService = personaService;
        this.tareaService = tareaService;

        addClassName("persona-view");
        setSizeFull();

        configureGrid();
        configureFormDialog();

        HorizontalLayout toolbar = getToolbar();
        add(toolbar, grid);

        updateList();
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filtrar por nombre, apellido o DNI...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        addPersonButton = new Button("Añadir Persona");
        addPersonButton.addClickListener(e -> openEditorDialog(new Persona()));

        editButton = new Button("Editar");
        editButton.setEnabled(false);
        editButton.addClickListener(e -> {
            if (currentPerson != null) {
                openEditorDialog(currentPerson);
            }
        });

        deleteButton = new Button("Eliminar");
        deleteButton.setEnabled(false);
        deleteButton.addClickListener(e -> {
            if (currentPerson != null) {
                confirmDelete(currentPerson);
            }
        });

        organizeTasksButton = new Button("Organizar Tareas");
        organizeTasksButton.addClickListener(e -> {
            UI.getCurrent().navigate(TareaView.class);
        });

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addPersonButton, editButton, deleteButton, organizeTasksButton);
        toolbar.addClassName("toolbar");
        toolbar.setPadding(true);
        toolbar.setSpacing(true);
        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE); // Para alinear correctamente
        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("persona-grid");
        grid.setSizeFull();
        grid.setColumns(); // Limpia columnas automáticas

        grid.addColumn(Persona::getId).setHeader("ID").setAutoWidth(true);
        grid.addColumn(Persona::getNombre).setHeader("Nombre").setAutoWidth(true);
        grid.addColumn(Persona::getApellido).setHeader("Apellido").setAutoWidth(true);
        grid.addColumn(Persona::getDni).setHeader("DNI").setAutoWidth(true);
        grid.addColumn(Persona::getEdad).setHeader("Edad").setAutoWidth(true);


        grid.addColumn(persona -> {
            long count = tareaService.countByPersona(persona); // Obtiene la cantidad de tareas
            return count;
        }).setHeader("Tareas").setAutoWidth(true).setSortable(true);




        grid.getColumns().forEach(col -> col.setAutoWidth(true));

        grid.asSingleSelect().addValueChangeListener(event -> {
            currentPerson = event.getValue();
            boolean isSelected = currentPerson != null;
            editButton.setEnabled(isSelected);
            deleteButton.setEnabled(isSelected);
        });
    }

    private void configureFormDialog() {
        editorDialog = new Dialog();
        editorDialog.setCloseOnEsc(true);
        editorDialog.setCloseOnOutsideClick(true);
        editorDialog.setWidth("400px");
        editorDialog.setHeight("auto");

        nombreField = new TextField("Nombre");
        apellidoField = new TextField("Apellido");
        edadField = new IntegerField("Edad");
        edadField.setStepButtonsVisible(true);
        edadField.setMin(0);
        dniField = new TextField("DNI");

        emailField = new EmailField("Email");
        passwordField = new PasswordField("Contraseña");
        passwordField.setHelperText("La contraseña se actualizará solo si ingresas un nuevo valor.");

        binder.bind(nombreField, Persona::getNombre, Persona::setNombre);
        binder.bind(apellidoField, Persona::getApellido, Persona::setApellido);
        binder.bind(edadField, Persona::getEdad, Persona::setEdad);
        binder.bind(dniField, Persona::getDni, Persona::setDni);

        binder.forField(emailField)
                .withValidator(email -> email != null && !email.isEmpty(), "Email no puede estar vacío")
                .withValidator(email -> email.matches("^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$"), "Formato de email inválido")
                .bind(Persona::getEmail, Persona::setEmail);
        binder.forField(passwordField)
                .withNullRepresentation("")
                .bind(Persona::getPassword, Persona::setPassword);

        Button saveButton = new Button("Guardar");
        saveButton.addClickListener(event -> savePerson());

        Button cancelButton = new Button("Cancelar");
        cancelButton.addClickListener(event -> closeEditorDialog());

        FormLayout formLayout = new FormLayout(
                nombreField, apellidoField, edadField, dniField,
                emailField, passwordField
        );
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonsLayout.setSpacing(true);

        VerticalLayout dialogContent = new VerticalLayout(formLayout, buttonsLayout);
        dialogContent.setPadding(true);
        dialogContent.setSpacing(true);

        editorDialog.add(dialogContent);
    }

    private void openEditorDialog(Persona person) {
        this.currentPerson = person;
        binder.readBean(person);

        if (person.getId() != null) {
            editorDialog.setHeaderTitle("Editar Persona");
            emailField.setValue(person.getEmail()); // Carga el email existente al editar
            passwordField.setValue(""); // La contraseña no se carga por seguridad
            passwordField.setHelperText("Deja vacío para mantener la contraseña actual.");
        } else {
            editorDialog.setHeaderTitle("Nueva Persona");
            emailField.setValue("");
            passwordField.setValue("");
            passwordField.setHelperText("La contraseña inicial para el login.");
        }
        editorDialog.open();
    }

    private void closeEditorDialog() {
        editorDialog.close();
        binder.setBean(null);
        currentPerson = null;
        grid.asSingleSelect().clear();
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    private void savePerson() {
        try {
            if (currentPerson == null) {
                currentPerson = new Persona();
            }

            binder.writeBean(currentPerson);

            Persona personaToSave = currentPerson;

            personaService.save(personaToSave);
            Notification.show("Persona guardada con éxito.", 3000, Notification.Position.MIDDLE);
            closeEditorDialog();
            updateList();
        } catch (com.vaadin.flow.data.binder.ValidationException e) {
            Notification.show("Hay errores en el formulario: " + e.getValidationErrors().stream()
                    .map(v -> v.getErrorMessage())
                    .collect(Collectors.joining(", ")), 5000, Notification.Position.MIDDLE);
            e.printStackTrace();
        } catch (Exception ex) {
            Notification.show("Error al guardar la persona: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            ex.printStackTrace();
        }
    }

    private void confirmDelete(Persona person) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirmar eliminación");
        dialog.setText("¿Estás seguro de que quieres eliminar a " + person.getNombre() + " " + person.getApellido() + "? Esta acción eliminará también sus tareas asociadas y no se puede deshacer.");
        dialog.setConfirmText("Eliminar");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(event -> deletePerson(person));
        dialog.addCancelListener(event -> Notification.show("Eliminación cancelada.", 2000, Notification.Position.MIDDLE));

        dialog.open();
    }

    private void deletePerson(Persona person) {
        try {
            personaService.delete(person.getId());
            Notification.show("Persona eliminada con éxito.", 3000, Notification.Position.MIDDLE);
            updateList();
            currentPerson = null;
            grid.asSingleSelect().clear();
            editButton.setEnabled(false);
            deleteButton.setEnabled(false);
        } catch (Exception ex) {
            Notification.show("Error al eliminar la persona: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            ex.printStackTrace();
        }
    }

    private void updateList() {
        List<Persona> personas;
        if (filterText.isEmpty()) {
            personas = personaService.getAll();
        } else {
            String filter = filterText.getValue().toLowerCase();
            personas = personaService.getAll().stream()
                    .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(filter)) ||
                            (p.getApellido() != null && p.getApellido().toLowerCase().contains(filter)) ||
                            (p.getDni() != null && p.getDni().toLowerCase().contains(filter)))
                    .collect(Collectors.toList());
        }
        grid.setItems(personas);
    }
}