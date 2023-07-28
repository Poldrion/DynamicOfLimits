package dynamics.views.controllers.popups;

import dynamics.model.DynamicsException;
import dynamics.model.entities.Corrections;
import dynamics.model.entities.Department;
import dynamics.model.entities.MoneyLimit;
import dynamics.model.services.CorrectionsService;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.function.Consumer;

import static dynamics.utils.FilePathConstants.MONEY_LIMIT_EDIT_FXML;
import static dynamics.utils.FormatUtils.formatNumber;
import static dynamics.utils.FormatUtils.parseNumber;
import static dynamics.utils.PropertiesUtils.*;
import static dynamics.utils.TitleConstants.*;


public class MoneyLimitEdit {
    @FXML
    private Label header, message, departmentComboBoxTitle, limitCostFieldTitle, yearFieldTitle;
    @FXML
    private ComboBox<Department> departmentComboBox;
    @FXML
    private VBox yearGroup, departmentGroup;
    @FXML
    private TextField limitCostTF;
    @FXML
    private ComboBox<Integer> yearCB;
    @FXML
    private Button saveBtn, exitBtn;

    private static MoneyLimit moneyLimit;
    private static Department department;
    private Consumer<MoneyLimit> saveHandler;
    private DepartmentService departmentService;
    private CorrectionsService correctionsService;

    public static void addMoneyLimit(Consumer<MoneyLimit> saveHandler, DepartmentService departmentService, CorrectionsService correctionsService) {
        editMoneyLimit(null, saveHandler, departmentService, correctionsService);
        Corrections correction = new Corrections();
        correction.setDepartment(department);
        correction.setYearCorrections(YEAR);
        correction.setDateCreate(LocalDate.of(getFirstYear(), Month.JANUARY, 1));
        correction.setLastCostLimit(moneyLimit.getCost());
        correction.setCost(BigDecimal.ZERO);
        correction.setCurrentCostLimit(moneyLimit.getCost());
        correction.setTimeCreate(LocalTime.of(0,0,0));
        correction.setRemark("Первичная передача лимита");
        correctionsService.save(correction);

    }

    public static void editMoneyLimit(MoneyLimit moneyLimit, Consumer<MoneyLimit> saveHandler, DepartmentService departmentService, CorrectionsService correctionsService) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(MoneyLimitEdit.class.getClassLoader().getResource(MONEY_LIMIT_EDIT_FXML));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            MoneyLimitEdit controller = loader.getController();
            controller.init(moneyLimit, saveHandler, departmentService, correctionsService);
            controller.attachEvent();

            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {
            ErrorDialog.ErrorDialogBuilder.builder().title(ERROR_DIALOG_TITLE).message(e.getMessage()).build().show();
            e.printStackTrace();
        }
    }

    private void init(MoneyLimit moneyLimit, Consumer<MoneyLimit> saveHandler, DepartmentService departmentService, CorrectionsService correctionsService) {
        MoneyLimitEdit.moneyLimit = moneyLimit;
        this.saveHandler = saveHandler;
        this.departmentService = departmentService;
        this.correctionsService = correctionsService;
        yearCB.getItems().addAll(GetYears());

        settingTitles();

        if (moneyLimit == null) {
            header.setText(CREATE_MONEY_LIMIT_ADD);
            MoneyLimitEdit.moneyLimit = new MoneyLimit();
            yearCB.getSelectionModel().selectFirst();

        } else {
            header.setText(CREATE_MONEY_LIMIT_EDIT);
            MoneyLimitEdit.moneyLimit = moneyLimit;
            yearCB.setValue(MoneyLimitEdit.moneyLimit.getYearLimit());
            this.changeEnable();
        }
        departmentComboBox.getItems().addAll(departmentService.findAll());
        departmentComboBox.setValue(MoneyLimitEdit.moneyLimit.getDepartment());
        if (MoneyLimitEdit.moneyLimit.getCost() == null) {
            limitCostTF.setText(formatNumber(BigDecimal.ZERO));
        } else {
            limitCostTF.setText(formatNumber(MoneyLimitEdit.moneyLimit.getCost()));
        }

        departmentComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> department = newValue);
    }

    @FXML
    private void save() {
        try {
            moneyLimit.setDepartment(departmentComboBox.getSelectionModel().getSelectedItem());
            moneyLimit.setYearLimit(yearCB.getValue());
            moneyLimit.setCost(parseNumber(limitCostTF.getText()));
            saveHandler.accept(moneyLimit);
            close();
            Dialog.DialogBuilder.builder().title(CREATE_MONEY_LIMIT_DIALOG_TITLE).message(CREATE_MONEY_LIMIT_DIALOG_MESSAGE).build().show();
        } catch (DynamicsException e) {
            message.setText(e.getMessage());
        } catch (Exception e) {
            ErrorDialog.ErrorDialogBuilder.builder().title(ERROR_DIALOG_TITLE).message(e.getMessage()).build().show();
            e.printStackTrace();
        }
    }

    @FXML
    private void close() {
        exitBtn.getScene().getWindow().hide();
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
        departmentComboBoxTitle.setText(CREATE_MONEY_LIMIT_DEPARTMENT_TITLE);
        limitCostFieldTitle.setText(CREATE_MONEY_LIMIT_COST_TITLE);
        yearFieldTitle.setText(CREATE_MONEY_LIMIT_YEAR_TITLE);
        saveBtn.setText(CREATE_MONEY_LIMIT_SAVE_BTN);
        exitBtn.setText(CREATE_MONEY_LIMIT_EXIT_BTN);
    }
}
