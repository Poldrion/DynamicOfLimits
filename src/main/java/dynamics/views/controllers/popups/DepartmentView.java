package dynamics.views.controllers.popups;

import dynamics.model.entities.Department;
import dynamics.model.services.DepartmentService;
import dynamics.views.controllers.common.ErrorDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.Consumer;

import static dynamics.utils.FilePathConstants.DEPARTMENT_VIEW_FXML;
import static dynamics.utils.TableViewUtils.SettingPlaceholder;
import static dynamics.utils.TitleConstants.*;

public class DepartmentView {
    @FXML
    private TableView<Department> departmentTableView;
    @FXML
    private TableColumn<Department, String> idCol, departmentCol;
    @FXML
    private Label header;
    @FXML
    private Button exit;

    private Department department;
    private DepartmentService departmentService;
    private Consumer<Department> saveHandler;

    public static void viewDepartment(Consumer<Department> saveHandler, DepartmentService departmentService) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(DepartmentView.class.getClassLoader().getResource(DEPARTMENT_VIEW_FXML));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            DepartmentView controller = loader.getController();
            controller.init(saveHandler, departmentService);

            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            ErrorDialog.ErrorDialogBuilder.builder().title(ERROR_DIALOG_TITLE).message(e.getMessage()).build().show();
            e.printStackTrace();
        }
    }

    private void init(Consumer<Department> saveHandler, DepartmentService departmentService) {
        this.saveHandler = saveHandler;
        this.departmentService = departmentService;
        departmentTableView.getItems().addAll(departmentService.findAll());
        settingTitles();
        SettingPlaceholder(departmentTableView);
        settingTableView();
        exit.setOnAction(event -> close());
    }

    private void close() {
        departmentTableView.getScene().getWindow().hide();
    }

    private void settingTitles() {
        header.setText(VIEW_DEPARTMENT_HEADER);
        exit.setText(VIEW_DEPARTMENT_EXIT_BTN);
        idCol.setText(VIEW_DEPARTMENT_ID_COL_TITLE);
        departmentCol.setText(VIEW_DEPARTMENT_DEPARTMENT_COL_TITLE);
    }

    private void settingTableView() {
        idCol.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        departmentCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
    }


}
