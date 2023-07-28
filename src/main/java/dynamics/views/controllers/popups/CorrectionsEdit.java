package dynamics.views.controllers.popups;

import dynamics.model.DynamicsException;
import dynamics.model.entities.Corrections;
import dynamics.model.entities.Department;
import dynamics.model.entities.MoneyLimit;
import dynamics.model.services.MoneyLimitService;
import dynamics.views.controllers.common.Dialog;
import dynamics.views.controllers.common.ErrorDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.function.Consumer;

import static dynamics.utils.FilePathConstants.CORRECTIONS_EDIT_FXML;
import static dynamics.utils.FormatUtils.formatNumber;
import static dynamics.utils.FormatUtils.parseNumber;
import static dynamics.utils.PropertiesUtils.GetYears;
import static dynamics.utils.TitleConstants.*;

public class CorrectionsEdit {

    @FXML
    private VBox yearGroup, lastCostLimitGroup, departmentGroup;
    @FXML
    private TextField lastCostLimitTF, costTF, currentCostLimitTF;
    @FXML
    private Label header, message, costTitle, currentCostLimitTitle, dateCreateTitle, departmentComboBoxTitle,
            lastCostLimitTitle, remarkTitle, yearFieldTitle;
    @FXML
    private ComboBox<Department> departmentComboBox;
    @FXML
    private ComboBox<Integer> yearCB;
    @FXML
    private DatePicker dateCreateDP;
    @FXML
    private Button saveBtn, exitBtn;
    @FXML
    private TextArea remark;

    private Corrections correction;
    private Department department;
    private MoneyLimit moneyLimit;
    private Consumer<Corrections> saveHandler;
    private Consumer<MoneyLimit> moneyLimitSaveHandler;
    private MoneyLimitService moneyLimitService;

    public static void addCorrection(Consumer<Corrections> saveHandler, Department department, MoneyLimitService moneyLimitService, Consumer<MoneyLimit> moneyLimitSaveHandler) {
        editCorrection(null, saveHandler, department, moneyLimitService, moneyLimitSaveHandler);
    }

    public static void editCorrection(Corrections correction, Consumer<Corrections> saveHandler, Department department, MoneyLimitService moneyLimitService, Consumer<MoneyLimit> moneyLimitSaveHandler) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(CorrectionsEdit.class.getClassLoader().getResource(CORRECTIONS_EDIT_FXML));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            CorrectionsEdit controller = loader.getController();
            controller.init(correction, saveHandler, department, moneyLimitService, moneyLimitSaveHandler);
            controller.attachEvent();

            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {

            ErrorDialog.ErrorDialogBuilder.builder().title(ERROR_DIALOG_TITLE).message(e.getMessage()).build().show();
            e.printStackTrace();
        }
    }

    private void init(Corrections correction, Consumer<Corrections> saveHandler, Department department, MoneyLimitService moneyLimitService, Consumer<MoneyLimit> moneyLimitSaveHandler) {
        this.correction = correction;
        this.department = department;
        this.saveHandler = saveHandler;
        this.moneyLimitService = moneyLimitService;
        this.moneyLimitSaveHandler = moneyLimitSaveHandler;

        yearCB.getItems().addAll(GetYears());
        departmentComboBox.getItems().addAll(department);

        settingTitles();

        if (correction == null) {
            header.setText(CORRECTIONS_ADD);
            this.correction = new Corrections();
            departmentComboBox.setValue(department);
            yearCB.getSelectionModel().selectFirst();
            dateCreateDP.setValue(LocalDate.now());
        } else {
            header.setText(CORRECTIONS_EDIT);
            this.correction = correction;
            departmentComboBox.setValue(this.correction.getDepartment());
            yearCB.setValue(this.correction.getYearCorrections());
            dateCreateDP.setValue(this.correction.getDateCreate());
        }
        this.changeEnable();
        this.moneyLimit = moneyLimitService.findMoneyLimitByDepartmentAndYear(department, yearCB.getValue());
        lastCostLimitTF.setText(formatNumber(this.moneyLimit.getCost()));
        remark.setText(this.correction.getRemark());
        costTF.setText(formatNumber(this.correction.getCost()));
        costTF.textProperty().addListener((options, oldValue, newValue) -> currentCostLimitTF.setText(formatNumber(getCurrentCostLimit(newValue))));
        currentCostLimitTF.setText(formatNumber(getCurrentCostLimit(costTF.getText())));
    }

    @NotNull
    private BigDecimal getCurrentCostLimit(String cost) {
        return parseNumber(lastCostLimitTF.getText())
                .add(parseNumber(cost));
    }

    @FXML
    void close() {
        exitBtn.getScene().getWindow().hide();
    }

    @FXML
    void save() {
        try {
            if (parseNumber(currentCostLimitTF.getText()).compareTo(BigDecimal.ZERO) >= 0) {
                this.correction.setDepartment(departmentComboBox.getSelectionModel().getSelectedItem());
                this.correction.setYearCorrections(yearCB.getValue());
                this.correction.setDateCreate(dateCreateDP.getValue());
                this.correction.setLastCostLimit(parseNumber(lastCostLimitTF.getText()));
                this.correction.setCost(parseNumber(costTF.getText()));
                this.correction.setCurrentCostLimit(parseNumber(currentCostLimitTF.getText()));
                this.correction.setTimeCreate(LocalTime.now());
                this.correction.setRemark(remark.getText());
                saveHandler.accept(this.correction);

                this.moneyLimit.setCost(parseNumber(currentCostLimitTF.getText()));
                moneyLimitSaveHandler.accept(this.moneyLimit);
                close();
                Dialog.DialogBuilder.builder().title(CORRECTIONS_DIALOG_TITLE).message(CORRECTIONS_DIALOG_MESSAGE).build().show();
            } else {
                message.setText(CORRECTIONS_WRONG_COST_ERROR);
            }
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
        lastCostLimitGroup.setDisable(!lastCostLimitGroup.isDisable());
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
        departmentComboBoxTitle.setText(CORRECTIONS_DEPARTMENT_TITLE);
        dateCreateTitle.setText(CORRECTIONS_DATE_CREATE_TITLE);
        lastCostLimitTitle.setText(CORRECTIONS_LAST_COST_LIMIT_TITLE);
        costTitle.setText(CORRECTIONS_COST_TITLE);
        currentCostLimitTitle.setText(CORRECTIONS_CURRENT_COST_LIMIT_TITLE);
        remarkTitle.setText(CORRECTIONS_REMARK_TITLE);
        yearFieldTitle.setText(CORRECTIONS_YEAR_TITLE);
        saveBtn.setText(CORRECTIONS_SAVE_BTN);
        exitBtn.setText(CORRECTIONS_EXIT_BTN);
    }

}
