package dynamics.views.controllers;

import dynamics.model.entities.BusinessPlan;
import dynamics.model.entities.Corrections;
import dynamics.model.entities.Department;
import dynamics.model.entities.MoneyLimit;
import dynamics.model.services.BusinessPlanService;
import dynamics.model.services.CorrectionsService;
import dynamics.model.services.DepartmentService;
import dynamics.model.services.MoneyLimitService;
import dynamics.views.controllers.popups.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import static dynamics.utils.FormatUtils.formatNumber;
import static dynamics.utils.PropertiesUtils.*;
import static dynamics.utils.TableViewUtils.SettingPlaceholder;
import static dynamics.utils.TitleConstants.*;

@Controller
public class MainController {

    @Autowired
    private DepartmentService departmentService;
    @Autowired
    private MoneyLimitService moneyLimitService;
    @Autowired
    private BusinessPlanService businessPlanService;
    @Autowired
    private CorrectionsService correctionsService;

    @FXML
    private TableView<Department> BPTableView;
    @FXML
    private TableColumn<Department, String> departmentCol, businessPlanCol, currentLimitCol, percentCol, countCorrections;
    @FXML
    private PieChart mainChart;
    @FXML
    private TableView<Department> withoutLimitDepTableView;
    @FXML
    private TableColumn<Department, String> departmentWithoutLimitCol, nullableLimitCol, businessPlanWithoutLimitCol, countCorrectionsWithoutLimit;
    @FXML
    private ComboBox<Integer> yearLimitCB;
    @FXML
    private Label limitTitle, bpGroupTitle, bpTableTitle, correctionGroupTitle, currencyTitle, departmentGroupTitle,
            limitGroupTitle, limitPrefTitle, withoutLimitTableTitle, yearTitle;
    @FXML
    private Button addBusinessPlanBtn, addDepartmentBtn, addLimitBtn, addCorrectionsLimitBtn, editBusinessPlanBtn,
            editDepartmentBtn, editLimitBtn, viewDepartmentsBtn;

    @FXML
    private void initialize() {
        settingTitles();
        settingYearComboBox();
        settingBPTableView();
        settingWithoutLimitDepartmentTableView();
        settingPieChart();
        settingLimitTitle();
        settingTableViewMouseClickEvents(BPTableView);
        settingTableViewMouseClickEvents(withoutLimitDepTableView);
    }

    @FXML
    private void addDepartment() {
        DepartmentEdit.addDepartment(this::saveDepartment);
        reload();
    }

    @FXML
    private void editDepartment() {
        if (CurrentDepartment != null) {
            DepartmentEdit.editDepartment(CurrentDepartment, this::saveDepartment);
        }
        reload();
    }

    @FXML
    private void viewDepartments() {
        DepartmentView.viewDepartment(this::saveDepartment, departmentService);
    }

    @FXML
    private void addLimit() {
        MoneyLimitEdit.addMoneyLimit(this::saveLimit, departmentService, correctionsService);
        reload();
    }

    @FXML
    private void editLimit() {
        if (CurrentDepartment != null) {
            MoneyLimitEdit.editMoneyLimit(moneyLimitService.findMoneyLimitByDepartmentAndYear(CurrentDepartment, yearLimitCB.getValue()), this::saveChangeLimit, departmentService, correctionsService);
        }
        reload();
    }

    @FXML
    private void addCorrectionsLimit() {
        if (CurrentDepartment != null) {
            CorrectionsEdit.addCorrection(this::saveCorrections, CurrentDepartment, moneyLimitService, this::saveChangeLimit);
        }
        reload();
    }

    @FXML
    private void addBusinessPlan() {
        BusinessPlanEdit.addBusinessPlan(this::saveBusinessPlan, departmentService);
        reload();
    }

    @FXML
    private void editBusinessPlan() {
        BusinessPlan businessPlan;
        if (CurrentDepartment != null) {
            businessPlan = businessPlanService.findBusinessPlanByDepartmentAndYear(CurrentDepartment, yearLimitCB.getValue());
            BusinessPlanEdit.editBusinessPlan(businessPlan, this::saveChangeBusinessPlan, departmentService);
        }
        reload();
    }

    private void reload() {
        BPTableView.getItems().clear();
        initDataBPTableView();
        withoutLimitDepTableView.getItems().clear();
        initDataWithoutLimitDepTableView();
        settingLimitTitle();
        settingPieChart();
    }

    private void saveDepartment(Department department) {
        departmentService.save(department);
        reload();
    }

    private void saveLimit(MoneyLimit moneyLimit) {
        moneyLimitService.save(moneyLimit);
        reload();
    }

    private void saveChangeLimit(MoneyLimit moneyLimit) {
        moneyLimitService.saveChangeLimit(moneyLimit);
        reload();
    }

    private void saveBusinessPlan(BusinessPlan businessPlan) {
        businessPlanService.save(businessPlan);
        reload();
    }

    private void saveChangeBusinessPlan(BusinessPlan businessPlan) {
        businessPlanService.saveChangeBusinessPlan(businessPlan);
        reload();
    }

    private void saveCorrections(Corrections correction) {
        correctionsService.save(correction);
        reload();
    }

    private void initDataBPTableView() {
        BPTableView.getItems().addAll(departmentService.findDepartmentWithLimit());
    }

    private void initDataWithoutLimitDepTableView() {
        withoutLimitDepTableView.getItems().addAll(departmentService.findDepartmentWithoutLimit());
    }

    @NotNull
    private ObservableList<PieChart.Data> initDataPieChart() {
        ObservableList<PieChart.Data> listForPieChart = FXCollections.observableArrayList();
        Collection<? extends MoneyLimit> moneyLimits = moneyLimitService.findAllByYearAndNotNullLimit(yearLimitCB.getValue());
        for (MoneyLimit limit : moneyLimits) {
            PieChart.Data data = new PieChart.Data(limit.getDepartment().getName() /*+ "\n" + formatNumber(limit.getCost())*/, limit.getCost().doubleValue());
            listForPieChart.add(data);
        }
        return listForPieChart;
    }

    private String getPercent(TableColumn.CellDataFeatures<Department, String> cellData) {
        try {
            BigDecimal result =
                    moneyLimitService.findMoneyLimitByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()).getCost()
                            .divide(businessPlanService.findBusinessPlanByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()).getCost(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
            return formatNumber(result) + "%";
        } catch (ArithmeticException | NullPointerException e) {
            return "0,00%";
        }
    }

    private void changeTableViewFocus(TableView<?> tableView) {
        tableView.focusedProperty().addListener((options, oldValue, newValue) -> {
            if (oldValue && !newValue) {
                tableView.getSelectionModel().clearSelection();
            }
        });
    }

    private void settingYearComboBox() {
        yearLimitCB.getItems().addAll(GetYears());
        yearLimitCB.getSelectionModel().selectFirst();
        YEAR = yearLimitCB.getValue();
        yearLimitCB.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            YEAR = yearLimitCB.getValue();
            reload();
        });
    }

    private void settingBPTableView() {
        SettingPlaceholder(BPTableView);
        initDataBPTableView();
        departmentCol.setCellValueFactory(this::settingDepartmentCol);
        businessPlanCol.setCellValueFactory(this::settingBusinessPlanCol);
        currentLimitCol.setCellValueFactory(this::settingCurrentLimitCol);
        percentCol.setCellValueFactory(cellData -> new SimpleStringProperty(getPercent(cellData)));
        countCorrections.setCellValueFactory(this::settingsCountCorrectionsCol);
        changeTableViewFocus(BPTableView);
    }

    private void settingWithoutLimitDepartmentTableView() {
        SettingPlaceholder(withoutLimitDepTableView);
        initDataWithoutLimitDepTableView();
        departmentWithoutLimitCol.setCellValueFactory(this::settingDepartmentCol);
        nullableLimitCol.setCellValueFactory(this::settingCurrentLimitCol);
        businessPlanWithoutLimitCol.setCellValueFactory(this::settingBusinessPlanCol);
        countCorrectionsWithoutLimit.setCellValueFactory(this::settingsCountCorrectionsCol);
        changeTableViewFocus(withoutLimitDepTableView);
    }

    private void settingPieChart() {
        // --------------------------------- Получение данных для построения диаграммы --------------------------------------------------------------
        ObservableList<PieChart.Data> listForPieChart = initDataPieChart();
        // ------------------------------------------------------------------------------------------------------------------------------------------
        // ------------------------------------------------------- Настройки диаграммы --------------------------------------------------------------
        mainChart.setTitle(MAIN_CHART_TITLE);       // название диаграммы
        mainChart.setTitleSide(Side.TOP);           // расположение названия диаграммы
        mainChart.setLegendSide(Side.BOTTOM);       // расположение легенды диаграммы
        mainChart.setLegendVisible(true);          // отображение легенды диаграммы
        mainChart.setAnimated(true);                // отображение анимации изменений диаграммы
        mainChart.setData(listForPieChart);         // инициализация данных для диаграммы
        mainChart.setStartAngle(0.0);               // начальный угол элементов диаграммы
        mainChart.setClockwise(true);               // направление построения диаграммы (true - по часовой стрелке, false - против часовой стрелки)
        mainChart.setLabelsVisible(true);           // отображение подписи данных на диаграмме
        mainChart.setLabelLineLength(20.0);         // длина выносных линий при отображении подписи данных на диаграмме
        // ------------------------------------------------------------------------------------------------------------------------------------------

        for (int i = 0; i < listForPieChart.size(); i++) {
            Tooltip.install(mainChart.getData().get(i).getNode(),
                    new Tooltip(mainChart.getData().get(i).getName() + " - " + formatNumber(mainChart.getData().get(i).getPieValue())));
        }
    }

    private void settingLimitTitle() {
        limitTitle.setText(formatNumber(moneyLimitService.getGeneralMoneyLimitByYear(yearLimitCB.getValue())));
    }

    private void settingTableViewMouseClickEvents(TableView<Department> tableView) {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                if (tableView.getSelectionModel().getSelectedItem() != null) {
                    CurrentDepartment = tableView.getSelectionModel().getSelectedItem();
                    DetailsController.showDetails(this::saveCorrections, correctionsService, departmentService, moneyLimitService);
                    reload();
                }
            }
            if (event.getClickCount() == 1) {
                if (tableView.getSelectionModel().getSelectedItem() != null) {
                    CurrentDepartment = tableView.getSelectionModel().getSelectedItem();
                }
            }
        });
    }

    private void settingTitles() {
        bpGroupTitle.setText(BP_GROUP_TITLE);
        bpTableTitle.setText(BP_TABLE_TITLE);
        correctionGroupTitle.setText(CORRECTION_GROUP_TITLE);
        currencyTitle.setText(CURRENCY_TITLE);
        departmentGroupTitle.setText(DEPARTMENT_GROUP_TITLE);
        limitGroupTitle.setText(LIMIT_GROUP_TITLE);
        limitPrefTitle.setText(LIMIT_PREF_TITLE);
        withoutLimitTableTitle.setText(WITHOUT_LIMIT_TABLE_TITLE);
        yearTitle.setText(YEAR_TITLE);
        addBusinessPlanBtn.setText(ADD_BUSINESS_PLAN_BTN_TITLE);
        addDepartmentBtn.setText(ADD_DEPARTMENT_BTN_TITLE);
        addLimitBtn.setText(ADD_LIMIT_BTN_TITLE);
        addCorrectionsLimitBtn.setText(ADD_CORRECTIONS_LIMIT_BTN_TITLE);
        editBusinessPlanBtn.setText(EDIT_BUSINESS_PLAN_BTN_TITLE);
        editDepartmentBtn.setText(EDIT_DEPARTMENT_BTN_TITLE);
        editLimitBtn.setText(EDIT_LIMIT_BTN_TITLE);
        viewDepartmentsBtn.setText(VIEW_DEPARTMENTS_BTN_TITLE);
        departmentCol.setText(DEPARTMENT_COL_TITLE);
        businessPlanCol.setText(BUSINESS_PLAN_COL_TITLE);
        currentLimitCol.setText(CURRENT_LIMIT_COL_TITLE);
        percentCol.setText(PERCENT_COL_TITLE);
        departmentWithoutLimitCol.setText(DEPARTMENT_WITHOUT_LIMIT_COL_TITLE);
        nullableLimitCol.setText(NULLABLE_LIMIT_COL_TITLE);
        businessPlanWithoutLimitCol.setText(BUSINESS_PLAN_WITHOUT_LIMIT_COL_TITLE);
        countCorrections.setText(COUNT_CORRECTIONS);
        countCorrectionsWithoutLimit.setText(COUNT_CORRECTIONS);
    }

    private ObservableValue<String> settingCurrentLimitCol(TableColumn.CellDataFeatures<Department, String> cellData) {
        SimpleStringProperty simpleStringProperty;
        if (moneyLimitService.findMoneyLimitByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()) == null)
            simpleStringProperty = new SimpleStringProperty();
        else
            simpleStringProperty = new SimpleStringProperty(
                    formatNumber(moneyLimitService.findMoneyLimitByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()).getCost())
            );
        return simpleStringProperty;
    }

    private ObservableValue<String> settingsCountCorrectionsCol(TableColumn.CellDataFeatures<Department, String> cellData) {
        SimpleStringProperty simpleStringProperty;
        //TODO
        if (correctionsService.findByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()) == null) {
            simpleStringProperty = new SimpleStringProperty();
        } else {
            simpleStringProperty = new SimpleStringProperty(
                    String.valueOf(correctionsService.getCountCorrectionsByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()))
            );
        }
        return simpleStringProperty;
    }

    private ObservableValue<String> settingBusinessPlanCol(TableColumn.CellDataFeatures<Department, String> cellData) {
        SimpleStringProperty simpleStringProperty;
        if (businessPlanService.findBusinessPlanByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()) == null)
            simpleStringProperty = new SimpleStringProperty();
        else
            simpleStringProperty = new SimpleStringProperty(
                    formatNumber(businessPlanService.findBusinessPlanByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()).getCost())
            );
        return simpleStringProperty;
    }

    private ObservableValue<String> settingDepartmentCol(TableColumn.CellDataFeatures<Department, String> cellData) {
        return new SimpleStringProperty(cellData.getValue().getName());
    }
}
