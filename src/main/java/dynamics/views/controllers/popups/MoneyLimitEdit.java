package dynamics.views.controllers.popups;

import dynamics.model.DynamicsException;
import dynamics.model.entities.Department;
import dynamics.model.entities.MoneyLimit;
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

import static dynamics.utils.FilePathsUtils.MONEY_LIMIT_EDIT_FXML;
import static dynamics.utils.FormatUtils.formatNumber;
import static dynamics.utils.FormatUtils.parseNumber;
import static dynamics.utils.PropertiesUtils.GetYears;
import static dynamics.utils.TitlesUtils.*;


public class MoneyLimitEdit {
    @FXML
    private Label departmentComboBoxTitle;
    @FXML
    private ComboBox<Department> departmentComboBox;
    @FXML
    private VBox departmentGroup;

    @FXML
    private Label header;
    @FXML
    private Label message;

    @FXML
    private Label limitCostFieldTitle;
    @FXML
    private TextField limitCostTF;

    @FXML
    private Label yearFieldTitle;
    @FXML
    private ComboBox<Integer> yearCB;
    @FXML
    private VBox yearGroup;


    @FXML
    private Button saveBtn;
    @FXML
    private Button exitBtn;

    private MoneyLimit moneyLimit;
    private Consumer<MoneyLimit> saveHandler;
    private DepartmentService departmentService;

    public static void addMoneyLimit(Consumer<MoneyLimit> saveHandler, DepartmentService departmentService) {
        editMoneyLimit(null, saveHandler, departmentService);

    }

    public static void editMoneyLimit(MoneyLimit moneyLimit, Consumer<MoneyLimit> saveHandler, DepartmentService departmentService) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(MoneyLimitEdit.class.getClassLoader().getResource(MONEY_LIMIT_EDIT_FXML));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            MoneyLimitEdit controller = loader.getController();
            controller.init(moneyLimit, saveHandler, departmentService);
            controller.attachEvent();

            stage.setResizable(false);
            stage.showAndWait();
        } catch (Exception e) {

            ErrorDialog.ErrorDialogBuilder.builder().title(ERROR_DIALOG_TITLE).message(e.getMessage()).build().show();
            e.printStackTrace();
        }
    }


    private void init(MoneyLimit moneyLimit, Consumer<MoneyLimit> saveHandler, DepartmentService departmentService) {
        this.moneyLimit = moneyLimit;
        this.saveHandler = saveHandler;
        this.departmentService = departmentService;
        yearCB.getItems().addAll(GetYears());


        if (moneyLimit == null) {
            header.setText(CREATE_MONEY_LIMIT_ADD);
            this.moneyLimit = new MoneyLimit();
            yearCB.getSelectionModel().selectFirst();

        } else {
            header.setText(CREATE_MONEY_LIMIT_EDIT);
            this.moneyLimit = moneyLimit;
            yearCB.setValue(this.moneyLimit.getYearLimit());
            this.changeEnable();
        }
        departmentComboBox.getItems().addAll(departmentService.findAll());
        departmentComboBoxTitle.setText(CREATE_MONEY_LIMIT_DEPARTMENT_TITLE);
        departmentComboBox.setValue(this.moneyLimit.getDepartment());
        limitCostFieldTitle.setText(CREATE_MONEY_LIMIT_COST_TITLE);
        if (this.moneyLimit.getCost() == null) {
            limitCostTF.setText(formatNumber(BigDecimal.ZERO));
        } else {
            limitCostTF.setText(formatNumber(this.moneyLimit.getCost()));
        }
        yearFieldTitle.setText(CREATE_MONEY_LIMIT_YEAR_TITLE);

        saveBtn.setText(CREATE_MONEY_LIMIT_SAVE_BTN);
        exitBtn.setText(CREATE_MONEY_LIMIT_EXIT_BTN);
    }

    @FXML
    private void save() {
        try {
            this.moneyLimit.setDepartment(departmentComboBox.getSelectionModel().getSelectedItem());
            this.moneyLimit.setYearLimit(yearCB.getValue());
            this.moneyLimit.setCost(parseNumber(limitCostTF.getText()));
            saveHandler.accept(this.moneyLimit);
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
            if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode()==KeyCode.SPACE) {
                if (exitBtn.isFocused()) close();
                else save();
            }
            if (keyEvent.getCode()==KeyCode.ESCAPE) {
                close();
            }
        });
    }

}
