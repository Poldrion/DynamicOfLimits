package dynamics.views.controllers;

import dynamics.model.entities.BusinessPlan;
import dynamics.model.entities.Department;
import dynamics.model.entities.MoneyLimit;
import dynamics.model.services.BusinessPlanService;
import dynamics.model.services.CorrectionsService;
import dynamics.model.services.DepartmentService;
import dynamics.model.services.MoneyLimitService;
import dynamics.views.controllers.popups.BusinessPlanEdit;
import dynamics.views.controllers.popups.DepartmentEdit;
import dynamics.views.controllers.popups.DepartmentView;
import dynamics.views.controllers.popups.MoneyLimitEdit;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;

import static dynamics.utils.FormatUtils.formatNumber;
import static dynamics.utils.PropertiesUtils.GetYears;
import static dynamics.utils.TableViewUtils.SettingsPlaceholder;
import static dynamics.utils.TitlesUtils.MAIN_CHART_TITLE;

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
    private TableColumn<Department, String> departmentCol, businessPlanCol, currentLimitCol, percentCol;

    @FXML
    private PieChart mainChart;

    @FXML
    private TableView<Department> withoutLimitDepTableView;
    @FXML
    private TableColumn<Department, String> departmentWithoutLimitCol, nullableLimitCol, businessPlanWithoutLimitCol;

    @FXML
    private ComboBox<Integer> yearLimitCB;
    @FXML
    private Label limitTitle;

    private static Integer YEAR;


    @FXML
    private void initialize() {
        settingsYearComboBox();
        settingsBPTableView();
        settingsWithoutLimitDepartmentTableView();
        settingsPieChart();
        settingsLimitTitle();
    }

    @FXML
    private void addDepartment() {
        DepartmentEdit.addDepartment(this::saveDepartment);
        reload();
    }

    @FXML
    private void editDepartment() {
        Department department = BPTableView.getSelectionModel().getSelectedItem();
        if (department != null) {
            DepartmentEdit.editDepartment(department, this::saveDepartment);
        } else {
            department = withoutLimitDepTableView.getSelectionModel().getSelectedItem();
            if (department != null) {
                DepartmentEdit.editDepartment(department, this::saveDepartment);
            }
        }
        reload();
    }

    @FXML
    private void viewDepartments() {
        DepartmentView.viewDepartment(this::saveDepartment, departmentService);
    }

    @FXML
    private void addLimit() {
        MoneyLimitEdit.addMoneyLimit(this::saveLimit, departmentService);
        reload();
    }

    @FXML
    private void editLimit() {
        Department department = BPTableView.getSelectionModel().getSelectedItem();
        if (department != null) {
            MoneyLimitEdit.editMoneyLimit(moneyLimitService.getMoneyLimitByDepartmentAndYear(department, yearLimitCB.getValue()), this::saveChangeLimit, departmentService);
        } else {
            department = withoutLimitDepTableView.getSelectionModel().getSelectedItem();
            if (department != null) {
                MoneyLimitEdit.editMoneyLimit(moneyLimitService.getMoneyLimitByDepartmentAndYear(department, yearLimitCB.getValue()), this::saveChangeLimit, departmentService);
            }
        }
        reload();
    }

    @FXML
    private void addCorrectionsLimit() {
        //TODO
    }

    @FXML
    private void addBusinessPlan() {
        BusinessPlanEdit.addBusinessPlan(this::saveBusinessPlan, departmentService);
        reload();
    }

    @FXML
    private void editBusinessPlan() {
        Department department = BPTableView.getSelectionModel().getSelectedItem();
        BusinessPlan businessPlan;
        if (department != null) {
            businessPlan = businessPlanService.getBusinessPlanByDepartmentAndYear(department, yearLimitCB.getValue());
            BusinessPlanEdit.editBusinessPlan(businessPlan, this::saveChangeBusinessPlan, departmentService);
        } else {
            department = withoutLimitDepTableView.getSelectionModel().getSelectedItem();
            if (department != null) {
                businessPlan = businessPlanService.getBusinessPlanByDepartmentAndYear(department, yearLimitCB.getValue());
                BusinessPlanEdit.editBusinessPlan(businessPlan, this::saveChangeBusinessPlan, departmentService);
            }
        }
        reload();
    }


    private void reload() {
        BPTableView.getItems().clear();
        BPTableView.getItems().addAll(departmentService.findDepartmentWithLimit());
        withoutLimitDepTableView.getItems().clear();
        withoutLimitDepTableView.getItems().addAll(departmentService.findDepartmentWithoutLimit());
        settingsLimitTitle();
        settingsPieChart();
    }

    public static Integer getYearLimit() {
        return YEAR;
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


    private void settingsYearComboBox() {
        yearLimitCB.getItems().addAll(GetYears());
        yearLimitCB.getSelectionModel().selectFirst();
        YEAR = yearLimitCB.getValue();
        yearLimitCB.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            YEAR = yearLimitCB.getValue();
            reload();
        });
    }

    private void settingsBPTableView() {
        SettingsPlaceholder(BPTableView);
        BPTableView.getItems().addAll(departmentService.findDepartmentWithLimit());
        departmentCol.setCellValueFactory(this::settingsDepartmentCol);
        businessPlanCol.setCellValueFactory(this::settingsBusinessPlanCol);
        currentLimitCol.setCellValueFactory(this::settingsCurrentLimitCol);
        percentCol.setCellValueFactory(cellData -> new SimpleStringProperty(getPercent(cellData)));
    }


    private void settingsWithoutLimitDepartmentTableView() {
        SettingsPlaceholder(withoutLimitDepTableView);
        departmentWithoutLimitCol.setCellValueFactory(this::settingsDepartmentCol);
        nullableLimitCol.setCellValueFactory(this::settingsCurrentLimitCol);
        businessPlanWithoutLimitCol.setCellValueFactory(this::settingsBusinessPlanCol);
    }


    private void settingsPieChart() {
        // --------------------------------- Получение данных для построения диаграммы --------------------------------------------------------------
        ObservableList<PieChart.Data> listForPieChart = FXCollections.observableArrayList();
        Collection<? extends MoneyLimit> moneyLimits = moneyLimitService.findAllByYearAndNotNullLimit(yearLimitCB.getValue());
        for (MoneyLimit limit : moneyLimits) {
            PieChart.Data data = new PieChart.Data(limit.getDepartment().getName() + "\n" + formatNumber(limit.getCost()), limit.getCost().doubleValue());
            listForPieChart.add(data);
        }
        // ------------------------------------------------------------------------------------------------------------------------------------------
        // ------------------------------------------------------- Настройки диаграммы --------------------------------------------------------------
        mainChart.setTitle(MAIN_CHART_TITLE);       // название диаграммы
        mainChart.setTitleSide(Side.TOP);           // расположение названия диаграммы
        mainChart.setLegendSide(Side.BOTTOM);       // расположение легенды диаграммы
        mainChart.setLegendVisible(false);          // отображение легенды диаграммы
        mainChart.setAnimated(true);                // отображение анимации изменений диаграммы
        mainChart.setData(listForPieChart);         // инициализация данных для диаграммы
        mainChart.setStartAngle(0.0);               // начальный угол элементов диаграммы
        mainChart.setClockwise(true);               // направление построения диаграммы (true - по часовой стрелке, false - против часовой стрелки)
        mainChart.setLabelsVisible(true);           // отображение подписи данных на диаграмме
        mainChart.setLabelLineLength(20.0);         // длина выносных линий при отображении подписи данных на диаграмме
        // ------------------------------------------------------------------------------------------------------------------------------------------
    }

    private void settingsLimitTitle() {
        limitTitle.setText(formatNumber(moneyLimitService.getGeneralMoneyLimitByYear(yearLimitCB.getValue())));
    }

    private ObservableValue<String> settingsCurrentLimitCol(TableColumn.CellDataFeatures<Department, String> cellData) {
        SimpleStringProperty simpleStringProperty;
        if (moneyLimitService.getMoneyLimitByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()) == null)
            simpleStringProperty = new SimpleStringProperty();
        else
            simpleStringProperty = new SimpleStringProperty(
                    formatNumber(moneyLimitService.getMoneyLimitByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()).getCost())
            );
        return simpleStringProperty;
    }

    private ObservableValue<String> settingsBusinessPlanCol(TableColumn.CellDataFeatures<Department, String> cellData) {
        SimpleStringProperty simpleStringProperty;
        if (businessPlanService.getBusinessPlanByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()) == null)
            simpleStringProperty = new SimpleStringProperty();
        else
            simpleStringProperty = new SimpleStringProperty(
                    formatNumber(businessPlanService.getBusinessPlanByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()).getCost())
            );
        return simpleStringProperty;
    }

    private ObservableValue<String> settingsDepartmentCol(TableColumn.CellDataFeatures<Department, String> cellData) {
        return new SimpleStringProperty(cellData.getValue().getName());
    }

    private String getPercent(TableColumn.CellDataFeatures<Department, String> cellData) {
        try {
            BigDecimal result =
                    moneyLimitService.getMoneyLimitByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()).getCost()
                            .divide(businessPlanService.getBusinessPlanByDepartmentAndYear(cellData.getValue(), yearLimitCB.getValue()).getCost(), 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
            return formatNumber(result) + "%";
        } catch (ArithmeticException | NullPointerException e) {
            return "0,00%";
        }

    }
}
