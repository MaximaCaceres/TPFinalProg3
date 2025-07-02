package utn.crud.tpfinal.Views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import utn.crud.tpfinal.Models.Persona;
import utn.crud.tpfinal.Models.Tarea;
import utn.crud.tpfinal.Services.PersonaService;
import utn.crud.tpfinal.Services.TareaService;
import java.time.LocalDate;
import java.util.List;
import com.vaadin.flow.component.orderedlayout.FlexComponent;


@Route("/tareas")
@PageTitle("Gestión General de Tareas")
public class TareaView extends VerticalLayout {

    private final TareaService tareaService;
    private final PersonaService personaService;

    private Grid<Tarea> grid = new Grid<>(Tarea.class);
    private Button addTaskButton;
    private Button editTaskButton;
    private Button deleteTaskButton;
    private Button backButton;

    private Dialog editorDialog;
    private DatePicker fechaInicioField;
    private DatePicker fechaFinalizacionField;
    private TextArea descripcionField;
    private Checkbox completadaField;
    private ComboBox<Persona> personaComboBox;
    private Binder<Tarea> binder = new Binder<>(Tarea.class);
    private Tarea currentTarea;

    private TextField filterTaskText = new TextField();

    public TareaView(TareaService tareaService, PersonaService personaService) {
        this.tareaService = tareaService;
        this.personaService = personaService;

        addClassName("tarea-view");
        setSizeFull();

        configureGrid();
        configureFormDialog();

        H3 title = new H3("Gestión General de Tareas");
        HorizontalLayout toolbar = getToolbar();
        add(title, toolbar, grid);

        updateList();
    }

    private HorizontalLayout getToolbar() {
        filterTaskText.setPlaceholder("Filtrar por descripción...");
        filterTaskText.setClearButtonVisible(true);
        filterTaskText.setValueChangeMode(ValueChangeMode.LAZY);
        filterTaskText.addValueChangeListener(e -> updateList());

        addTaskButton = new Button("Asignar Tarea");
        addTaskButton.addClickListener(e -> openEditorDialog(new Tarea()));

        editTaskButton = new Button("Editar");
        editTaskButton.setEnabled(false);
        editTaskButton.addClickListener(e -> {
            if (currentTarea != null) {
                openEditorDialog(currentTarea);
            }
        });

        deleteTaskButton = new Button("Eliminar");
        deleteTaskButton.setEnabled(false);
        deleteTaskButton.addClickListener(e -> {
            if (currentTarea != null) {
                confirmDeleteTarea(currentTarea);
            }
        });


        backButton = new Button("Cargar Personas");
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(PersonaView.class)));



        HorizontalLayout toolbar = new HorizontalLayout(addTaskButton, filterTaskText, editTaskButton, deleteTaskButton, backButton);
        toolbar.addClassName("toolbar");
        toolbar.setPadding(true);
        toolbar.setSpacing(true);

        toolbar.setAlignItems(FlexComponent.Alignment.BASELINE);

        return toolbar;
    }

    private void configureGrid() {
        grid.addClassName("tarea-grid");
        grid.setSizeFull();
        grid.setColumns();

        grid.addColumn(tarea -> {
            Persona persona = tarea.getPersona();
            return persona != null ? persona.getNombre() + " " + persona.getApellido() : "N/A";
        }).setHeader("Persona").setAutoWidth(true);

        grid.addColumn(Tarea::getDescripcion).setHeader("Descripción").setAutoWidth(true);
        grid.addColumn(Tarea::getFechaInicio).setHeader("Fecha Inicio").setAutoWidth(true);
        grid.addColumn(Tarea::getFechaFinalizacion).setHeader("Fecha Fin").setAutoWidth(true);

        grid.addComponentColumn(tarea -> {
            Checkbox checkbox = new Checkbox(tarea.isCompletada());
            checkbox.addValueChangeListener(event -> {
                tarea.setCompletada(event.getValue());
                tareaService.saveTarea(tarea);
                Notification.show("Tarea '" + tarea.getDescripcion() + "' actualizada.", 2000, Notification.Position.MIDDLE);
            });
            return checkbox;
        }).setHeader("Completada").setAutoWidth(true);

        grid.asSingleSelect().addValueChangeListener(event -> {
            currentTarea = event.getValue();
            boolean isSelected = currentTarea != null;
            editTaskButton.setEnabled(isSelected);
            deleteTaskButton.setEnabled(isSelected);
        });
    }

    private void configureFormDialog() {
        editorDialog = new Dialog();
        editorDialog.setCloseOnEsc(true);
        editorDialog.setCloseOnOutsideClick(true);
        editorDialog.setWidth("450px");
        editorDialog.setHeight("auto");

        personaComboBox = new ComboBox<>("Asignar a Persona");
        personaComboBox.setItems(personaService.getAll());
        personaComboBox.setItemLabelGenerator(persona -> persona.getNombre() + " " + persona.getApellido());
        personaComboBox.setRequired(true);

        descripcionField = new TextArea("Descripción");
        descripcionField.setWidthFull();
        descripcionField.setMaxLength(255);
        descripcionField.setValueChangeMode(ValueChangeMode.EAGER);
        descripcionField.setHelperText("Max 255 caracteres");

        fechaInicioField = new DatePicker("Fecha de Inicio");
        fechaFinalizacionField = new DatePicker("Fecha de Finalización");
        completadaField = new Checkbox("Tarea Completada");

        binder.forField(descripcionField)
                .withValidator(desc -> !desc.trim().isEmpty(), "La descripción no puede estar vacía.")
                .bind(Tarea::getDescripcion, Tarea::setDescripcion);
        binder.bind(fechaInicioField, Tarea::getFechaInicio, Tarea::setFechaInicio);
        binder.bind(fechaFinalizacionField, Tarea::getFechaFinalizacion, Tarea::setFechaFinalizacion);
        binder.bind(completadaField, Tarea::isCompletada, Tarea::setCompletada);

        binder.forField(personaComboBox)
                .asRequired("Debe seleccionar una persona para la tarea.")
                .bind(Tarea::getPersona, Tarea::setPersona);

        binder.withValidator(tarea -> {
            if (tarea.getFechaInicio() != null && tarea.getFechaFinalizacion() != null) {
                return !tarea.getFechaFinalizacion().isBefore(tarea.getFechaInicio());
            }
            return true;
        }, "La fecha de finalización no puede ser anterior a la de inicio.");


        Button saveButton = new Button("Guardar");
        saveButton.addClickListener(event -> saveTarea());

        Button cancelButton = new Button("Cancelar");
        cancelButton.addClickListener(event -> closeEditorDialog());

        FormLayout formLayout = new FormLayout(
                personaComboBox,
                descripcionField,
                fechaInicioField,
                fechaFinalizacionField,
                completadaField
        );
        HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton, cancelButton);
        buttonsLayout.setSpacing(true);

        VerticalLayout dialogContent = new VerticalLayout(formLayout, buttonsLayout);
        dialogContent.setPadding(true);
        dialogContent.setSpacing(true);

        editorDialog.add(dialogContent);
    }

    private void openEditorDialog(Tarea tarea) {
        this.currentTarea = tarea;

        binder.readBean(currentTarea);

        if (currentTarea.getId() != null) {
            editorDialog.setHeaderTitle("Editar Tarea");
            personaComboBox.setValue(currentTarea.getPersona());
        } else {
            editorDialog.setHeaderTitle("Nueva Tarea");
            personaComboBox.clear();
            fechaInicioField.setValue(LocalDate.now());
            fechaFinalizacionField.setValue(LocalDate.now().plusDays(7));
            completadaField.setValue(false);
        }

        editorDialog.open();
    }

    private void closeEditorDialog() {
        editorDialog.close();
        binder.setBean(null);
        currentTarea = null;
        grid.asSingleSelect().clear();
    }

    private void saveTarea() {
        try {
            if (currentTarea == null) {
                currentTarea = new Tarea();
            }

            binder.writeBean(currentTarea);

            tareaService.saveTarea(currentTarea);

            Notification.show("Tarea guardada con éxito.", 3000, Notification.Position.MIDDLE);
            closeEditorDialog();
            updateList();
        } catch (com.vaadin.flow.data.binder.ValidationException e) {
            Notification.show("Hay errores en el formulario: " + e.getValidationErrors().stream()
                    .map(v -> v.getErrorMessage())
                    .collect(java.util.stream.Collectors.joining(", ")), 5000, Notification.Position.MIDDLE);
            e.printStackTrace();
        } catch (Exception ex) {
            Notification.show("Error al guardar la tarea: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            ex.printStackTrace();
        }
    }

    private void confirmDeleteTarea(Tarea tarea) {
        com.vaadin.flow.component.confirmdialog.ConfirmDialog dialog = new com.vaadin.flow.component.confirmdialog.ConfirmDialog();
        dialog.setHeader("Confirmar eliminación de Tarea");
        dialog.setText("¿Estás seguro de que quieres eliminar la tarea: '" + tarea.getDescripcion() + "' de " + tarea.getPersona().getNombre() + "? Esta acción no se puede deshacer.");
        dialog.setConfirmText("Eliminar");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(event -> deleteTarea(tarea));
        dialog.addCancelListener(event -> Notification.show("Eliminación de tarea cancelada.", 2000, Notification.Position.MIDDLE));

        dialog.open();
    }

    private void deleteTarea(Tarea tarea) {
        try {
            tareaService.deleteTarea(tarea.getId());
            Notification.show("Tarea eliminada con éxito.", 3000, Notification.Position.MIDDLE);
            updateList();
            currentTarea = null;
            grid.asSingleSelect().clear();
            editTaskButton.setEnabled(false);
            deleteTaskButton.setEnabled(false);
        } catch (Exception ex) {
            Notification.show("Error al eliminar la tarea: " + ex.getMessage(), 5000, Notification.Position.MIDDLE);
            ex.printStackTrace();
        }
    }

    private void updateList() {
        List<Tarea> tareas;
        if (filterTaskText.isEmpty()) {
            tareas = tareaService.getAllTareas();
        } else {
            String filter = filterTaskText.getValue().toLowerCase();
            tareas = tareaService.getAllTareas().stream()
                    .filter(t -> t.getDescripcion() != null && t.getDescripcion().toLowerCase().contains(filter))
                    .collect(java.util.stream.Collectors.toList());
        }
        grid.setItems(tareas);
        editTaskButton.setEnabled(false);
        deleteTaskButton.setEnabled(false);
    }
}