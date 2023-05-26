package dynamics.views.controllers.popups;

import dynamics.model.DynamicsException;
import dynamics.model.entities.Department;
import dynamics.views.controllers.common.Dialog;
import dynamics.views.controllers.common.ErrorDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

import static dynamics.utils.FilePathsUtils.*;
import static dynamics.utils.TitlesUtils.*;

public class DepartmentEdit {
    @FXML
    private TextField departmentNameTF;


    @FXML
    private Label header;

    @FXML
    private Label message;

    @FXML
    private Label nameFieldTitle;

    @FXML
    private Button saveBtn;
    @FXML
    private Button exitBtn;

    private Department department;
    private Consumer<Department> saveHandler;

    public static void addDepartment(Consumer<Department> saveHandler) {
        editDepartment(null, saveHandler);
    }

    public static void editDepartment(Department department, Consumer<Department> saveHandler) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(DepartmentEdit.class.getClassLoader().getResource(DEPARTMENT_EDIT_FXML));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            DepartmentEdit controller = loader.getController();
            controller.init(department, saveHandler);
            controller.attachEvent();

            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {

            ErrorDialog.ErrorDialogBuilder.builder().title(ERROR_DIALOG_TITLE).message(e.getMessage()).build().show();
            e.printStackTrace();
        }
    }

    private void init(Department department, Consumer<Department> saveHandler) {
        this.department = department;
        this.saveHandler = saveHandler;

        if (department == null) {
            header.setText(CREATE_DEPARTMENT_HEADER_ADD);
            this.department = new Department();
        } else {
            header.setText(CREATE_DEPARTMENT_HEADER_EDIT);
            this.department = department;
        }
        nameFieldTitle.setText(CREATE_DEPARTMENT_NAME_FIELD_TITLE);
        departmentNameTF.setPromptText(CREATE_DEPARTMENT_TEXT_FIELD_PROMPT_TEXT);
        departmentNameTF.setText(this.department.getName());
        saveBtn.setText(CREATE_DEPARTMENT_SAVE_BTN);
        exitBtn.setText(CREATE_DEPARTMENT_EXIT_BTN);
    }

    @FXML
    private void close() {
        departmentNameTF.getScene().getWindow().hide();
    }

    @FXML
    private void save() {
        try {
            if (departmentNameTF.getText().trim().isEmpty())
                message.setText(CREATE_DEPARTMENT_MESSAGE);
            else {
                this.department.setName(departmentNameTF.getText().trim());
                saveHandler.accept(this.department);
                close();
                Dialog.DialogBuilder.builder().title(CREATE_DEPARTMENT_DIALOG_TITLE).message(CREATE_DEPARTMENT_DIALOG_MESSAGE).build().show();
            }
        } catch (DynamicsException e) {
            message.setText(e.getMessage());
        } catch (Exception e) {
            ErrorDialog.ErrorDialogBuilder.builder().title(ERROR_DIALOG_TITLE).message(e.getMessage()).build().show();
            e.printStackTrace();
        }
    }


    private void attachEvent() {
        saveBtn.getScene().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.SPACE) {
                if (exitBtn.isFocused()) close();
                else save();
            }
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                close();
            }
        });
    }


}
