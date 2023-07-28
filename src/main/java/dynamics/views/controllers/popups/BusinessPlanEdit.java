package dynamics.views.controllers.popups;

import dynamics.model.DynamicsException;
import dynamics.model.entities.BusinessPlan;
import dynamics.model.entities.Department;
import dynamics.model.services.DepartmentService;
import dynamics.views.controllers.common.Dialog;
import dynamics.views.controllers.common.ErrorDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.function.Consumer;

import static dynamics.utils.FilePathConstants.BUSINESS_PLAN_EDIT_FXML;
import static dynamics.utils.FormatUtils.formatNumber;
import static dynamics.utils.FormatUtils.parseNumber;
import static dynamics.utils.PropertiesUtils.GetYears;
import static dynamics.utils.TitleConstants.*;

public class BusinessPlanEdit {
    @FXML
    private Label departmentComboBoxTitle, header, message, businessPlanCostFieldTitle, yearFieldTitle;
    @FXML
    private ComboBox<Department> departmentComboBox;
    @FXML
    private VBox departmentGroup;

    @FXML
    private TextField businessPlanCostTF;

    @FXML
    private ComboBox<Integer> yearCB;
    @FXML
    private VBox yearGroup;

    @FXML
    private Button saveBtn, exitBtn;

    private BusinessPlan businessPlan;
    private Consumer<BusinessPlan> saveHandler;
    private DepartmentService departmentService;

    public static void addBusinessPlan(Consumer<BusinessPlan> saveHandler, DepartmentService departmentService) {
        editBusinessPlan(null, saveHandler, departmentService);
    }

    public static void editBusinessPlan(BusinessPlan businessPlan, Consumer<BusinessPlan> saveHandler, DepartmentService departmentService) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(BusinessPlanEdit.class.getClassLoader().getResource(BUSINESS_PLAN_EDIT_FXML));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            BusinessPlanEdit controller = loader.getController();
            controller.init(businessPlan, saveHandler, departmentService);
            controller.attachEvent();

            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            ErrorDialog.ErrorDialogBuilder.builder().title(ERROR_DIALOG_TITLE).message(e.getMessage()).build().show();
            e.printStackTrace();
        }
    }

    private void init(BusinessPlan businessPlan, Consumer<BusinessPlan> saveHandler, DepartmentService departmentService) {
        this.businessPlan = businessPlan;
        this.saveHandler = saveHandler;
        this.departmentService = departmentService;
        yearCB.getItems().addAll(GetYears());

        if (businessPlan == null) {
            header.setText(CREATE_BUSINESS_PLAN_ADD);
            this.businessPlan = new BusinessPlan();
            yearCB.getSelectionModel().selectFirst();
        } else {
            header.setText(CREATE_BUSINESS_PLAN_EDIT);
            this.businessPlan = businessPlan;
            yearCB.setValue(this.businessPlan.getYearBP());
            this.changeEnable();
        }
        settingTitles();
        departmentComboBox.getItems().addAll(departmentService.findAll());
        departmentComboBox.setValue(this.businessPlan.getDepartment());

        if (this.businessPlan.getCost() == null) {
            businessPlanCostTF.setText(formatNumber(BigDecimal.ZERO));
        } else {
            businessPlanCostTF.setText(formatNumber(this.businessPlan.getCost()));
        }
    }

    @FXML
    private void close() {
        exitBtn.getScene().getWindow().hide();
    }

    @FXML
    private void save() {
        try {
            this.businessPlan.setDepartment(departmentComboBox.getSelectionModel().getSelectedItem());
            this.businessPlan.setYearBP(yearCB.getValue());
            this.businessPlan.setCost(parseNumber(businessPlanCostTF.getText()));
            saveHandler.accept(this.businessPlan);
            close();
            Dialog.DialogBuilder.builder().title(CREATE_BUSINESS_PLAN_DIALOG_TITLE).message(CREATE_BUSINESS_PLAN_DIALOG_MESSAGE).build().show();
        } catch (DynamicsException e) {
            message.setText(e.getMessage());
        } catch (Exception e) {
            ErrorDialog.ErrorDialogBuilder.builder().title(ERROR_DIALOG_TITLE).message(e.getMessage()).build().show();
            e.printStackTrace();
        }
    }

    private void changeEnable() {
        yearGroup.setDisable(!yearGroup.isDisable());
        departmentGroup.setDisable(!departmentGroup.isDisable());
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

    private void settingTitles() {
        departmentComboBoxTitle.setText(CREATE_BUSINESS_PLAN_DEPARTMENT_TITLE);
        businessPlanCostFieldTitle.setText(CREATE_BUSINESS_PLAN_COST_TITLE);
        yearFieldTitle.setText(CREATE_BUSINESS_PLAN_YEAR_TITLE);
        saveBtn.setText(CREATE_BUSINESS_PLAN_SAVE_BTN);
        exitBtn.setText(CREATE_BUSINESS_PLAN_EXIT_BTN);
    }
}



