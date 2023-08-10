package dynamics.views.controllers;

import dynamics.model.entities.Corrections;
import dynamics.model.entities.MoneyLimit;
import dynamics.model.services.CorrectionsService;
import dynamics.model.services.DepartmentService;
import dynamics.model.services.MoneyLimitService;
import dynamics.utils.FormatUtils;
import dynamics.views.controllers.popups.CorrectionsEdit;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Translate;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import static dynamics.utils.FilePathConstants.APPLICATION_ICON;
import static dynamics.utils.FilePathConstants.DETAILS_FXML;
import static dynamics.utils.FormatUtils.*;
import static dynamics.utils.FormatUtils.formatNumber;
import static dynamics.utils.PropertiesUtils.*;
import static dynamics.utils.TableViewUtils.SettingPlaceholder;
import static dynamics.utils.TitleConstants.*;

public class DetailsController {
    @FXML
    private Button backBtn, addCorrectionsBtn, deleteCorrectionsBtn, editCorrectionsBtn;
    @FXML
    private Label yearTitle, departmentTitle, correctionsTitle;
    @FXML
    private TableView<Corrections> detailsTableView;
    @FXML
    private TableColumn<Corrections, String> dateCorrectionsCol, lastCostLimitCol, correctionsCostCol, currentLimitCostCol, remarkCol;
    @FXML
    private StackedBarChart<String, Number> correctionBarChart;
    @FXML
    private ComboBox<Integer> yearCB;


    private Consumer<Corrections> saveHandler;
    private CorrectionsService correctionsService;
    private DepartmentService departmentService;
    private MoneyLimitService moneyLimitService;

    public static void showDetails(Consumer<Corrections> saveHandler, CorrectionsService correctionsService, DepartmentService departmentService, MoneyLimitService moneyLimitService) {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(DetailsController.class.getClassLoader().getResource(DETAILS_FXML));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.initModality(Modality.WINDOW_MODAL);

            DetailsController controller = loader.getController();
            controller.init(saveHandler, correctionsService, departmentService, moneyLimitService);

            stage.setTitle(DETAILS_WINDOW_TITLE + CurrentDepartment.getName());
            stage.getIcons().add(new Image(APPLICATION_ICON));
            stage.setMaximized(true);
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void init(Consumer<Corrections> saveHandler, CorrectionsService correctionsService, DepartmentService departmentService, MoneyLimitService moneyLimitService) {
        this.saveHandler = saveHandler;
        this.correctionsService = correctionsService;
        this.departmentService = departmentService;
        this.moneyLimitService = moneyLimitService;

        settingsYearComboBox();
        settingsTitles();
        settingsTableView();
        settingsStackedBarChart();
    }

    @FXML
    private void backToMainWindow() {
        backBtn.getScene().getWindow().hide();
    }

    @FXML
    private void addCorrection() {
        CorrectionsEdit.addCorrection(this::saveCorrection, CurrentDepartment, moneyLimitService, this::saveChangeLimit);
        reload();
    }

    @FXML
    private void deleteCorrection() {
        Corrections correction = detailsTableView.getItems().get(detailsTableView.getItems().size() - 1);
        if (correction != null) {
            MoneyLimit limit = moneyLimitService.findMoneyLimitByDepartmentAndYear(correction.getDepartment(), correction.getYearCorrections());
            limit.setCost(correction.getLastCostLimit());
            saveChangeLimit(limit);
            correctionsService.delete(correction.getId());
            reload();
        }
    }

    @FXML
    private void editCorrection() {
        Corrections correction = detailsTableView.getSelectionModel().getSelectedItem();
        if (correction != null) {
            CorrectionsEdit.editCorrection(correction, this::saveCorrection, CurrentDepartment, moneyLimitService, this::saveChangeLimit);
            reload();
        }
    }

    private void settingsYearComboBox() {
        yearCB.getItems().addAll(GetYears());
        yearCB.setValue(YEAR);
        yearCB.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> reload());
    }

    private void settingsTitles() {
        departmentTitle.setText(CurrentDepartment.getName());
        yearTitle.setText(DETAILS_YEAR_TITLE);
        correctionsTitle.setText(DETAILS_CORRECTIONS_TITLE);
        addCorrectionsBtn.setText(DETAILS_CORRECTIONS_ADD_BTN_TITLE);
        editCorrectionsBtn.setText(DETAILS_CORRECTIONS_EDIT_BTN_TITLE);
        deleteCorrectionsBtn.setText(DETAILS_CORRECTIONS_DELETE_BTN_TITLE);
        backBtn.setText(DETAILS_BACK_BTN_TITLE);
        dateCorrectionsCol.setText(DETAILS_DATE_CORRECTIONS_COL_TITLE);
        lastCostLimitCol.setText(DETAILS_LOST_COST_LIMIT_COL_TITLE);
        correctionsCostCol.setText(DETAILS_CORRECTIONS_COST_COL_TITLE);
        currentLimitCostCol.setText(DETAILS_CURRENT_COST_LIMIT_COL_TITLE);
        remarkCol.setText(DETAILS_REMARK_COL_TITLE);
        SettingPlaceholder(detailsTableView);
    }

    private void settingsTableView() {
        initDataDetailsTableView();
        dateCorrectionsCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateCreate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        lastCostLimitCol.setCellValueFactory(cellData -> new SimpleStringProperty(formatNumber(cellData.getValue().getLastCostLimit())));
        correctionsCostCol.setCellValueFactory(cellData -> new SimpleStringProperty(formatNumber(cellData.getValue().getCost())));
        currentLimitCostCol.setCellValueFactory(cellData -> new SimpleStringProperty(formatNumber(cellData.getValue().getCurrentCostLimit())));
        remarkCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRemark()));
    }

    private void settingsStackedBarChart() {

        // ------------------------------------------------------- Настройки диаграммы --------------------------------------------------------------
        correctionBarChart.setTitle(DETAILS_STACKED_BAR_CHART_TITLE);       // наименование гистограммы
        correctionBarChart.setTitleSide(Side.TOP);                          // расположение наименования гистограммы
        correctionBarChart.setLegendSide(Side.BOTTOM);                      // расположение легенды
        correctionBarChart.setLegendVisible(false);                         // отображение легенды
        correctionBarChart.setAnimated(true);                               // отображение анимации изменений
        // ------------------------------------------------------------------------------------------------------------------------------------------
        initDataStackedBarChart();
        correctionBarChart.getXAxis().setTickLabelRotation(270);            // настройка поворота текста по оси категорий
    }

    private void initDataStackedBarChart() {

        // Задаем формат отображения даты и времени
        String localDateTimeFormat = "dd.MM.yyyy";
        String localTimeFormat = "HH:mm:ss";

        // Получаем список корректировок для выбранного структурного подразделения и года
        Collection<? extends Corrections> corrections = correctionsService.findByDepartmentAndYear(CurrentDepartment, yearCB.getValue());

        // Создаем промежуточный временный список корректировок, полученных на предыдущем шаге
        ArrayList<Corrections> temp = new ArrayList<>(corrections);

        // Создаем последнюю корректировку в периоде (без добавления в БД)
        addLastCorrectionBar(corrections, temp);

        // Очищаем данные в диаграмме
        correctionBarChart.getData().clear();

        // Создаем столбцы лимитов и делаем их прозрачными
        addLimitBars(temp, localDateTimeFormat, localTimeFormat);

        // Настраиваем отображение первой и последней корректировок/лимитов (цвет, подпись данных)
        settingFirstAndLastCorrectionBars();

        // Добавляем положительные отклонения и настраиваем отображение (цвет, подпись данных)
        addPositiveOffsets(temp, localDateTimeFormat, localTimeFormat);

        // Добавляем отрицательные отклонения и настраиваем отображение (цвет, подпись данных)
        addNegativeOffsets(temp, localDateTimeFormat, localTimeFormat);
    }

    private void settingFirstAndLastCorrectionBars() {
        correctionBarChart.getData().get(0).getData().get(0).getNode().setStyle("-fx-bar-fill: orange;");
        StackPane firstElementBar = (StackPane) correctionBarChart.getData().get(0).getData().get(0).getNode();
        final Text firstElementDataText = new Text(formatNumber(correctionBarChart.getData().get(0).getData().get(0).getYValue()));
        firstElementDataText.setRotate(-90.0);
        firstElementBar.getChildren().add(firstElementDataText);


        correctionBarChart.getData().get(0).getData().get(correctionBarChart.getData().get(0).getData().size() - 1).getNode().setStyle("-fx-bar-fill: orange;");
        StackPane lastElementBar = (StackPane) correctionBarChart.getData().get(0).getData().get(correctionBarChart.getData().get(0).getData().size() - 1).getNode();
        final Text lastElementDataText = new Text(formatNumber(correctionBarChart.getData().get(0).getData().get(correctionBarChart.getData().get(0).getData().size() - 1).getYValue()));
        lastElementDataText.setRotate(-90.0);
        lastElementBar.getChildren().add(lastElementDataText);
    }

    private void addLimitBars(ArrayList<Corrections> temp, String localDateTimeFormat, String localTimeFormat) {
        XYChart.Series<String, Number> limitSeries = new XYChart.Series<>();
        limitSeries.setName(DETAILS_STACKED_BAR_CHART_LIMIT_SERIES_TITLE);

        for (Corrections c : temp) {
            String dateCreate = c.getDateCreate().format(DateTimeFormatter.ofPattern(localDateTimeFormat))
                    + "\n"
                    + c.getTimeCreate().format(DateTimeFormatter.ofPattern(localTimeFormat));
            if (c.getCost().compareTo(BigDecimal.ZERO) >= 0) {
                limitSeries.getData().add(new XYChart.Data<>(dateCreate, c.getLastCostLimit()));
            } else
                limitSeries.getData().add(new XYChart.Data<>(dateCreate, c.getCurrentCostLimit()));
        }
        correctionBarChart.getData().add(limitSeries);
        for (var element : correctionBarChart.getData().get(0).getData()) {
            element.getNode().setStyle("-fx-bar-fill: transparent;");
        }
    }

    private void addLastCorrectionBar(Collection<? extends Corrections> corrections, ArrayList<Corrections> temp) {
        Corrections lastCorrection = new Corrections();
        lastCorrection.setDepartment(CurrentDepartment);
        lastCorrection.setYearCorrections(yearCB.getValue());
        lastCorrection.setDateCreate(LocalDate.of(yearCB.getValue(), Month.DECEMBER, 31));
        lastCorrection.setLastCostLimit(temp.get(corrections.size() - 1).getCurrentCostLimit());
        lastCorrection.setCost(BigDecimal.ZERO);
        lastCorrection.setCurrentCostLimit(temp.get(corrections.size() - 1).getCurrentCostLimit());
        lastCorrection.setTimeCreate(LocalTime.of(23, 59, 59));
        lastCorrection.setRemark("");
        temp.add(lastCorrection);
    }

    private void addPositiveOffsets(ArrayList<Corrections> temp, String localDateTimeFormat, String localTimeFormat) {
        XYChart.Series<String, Number> positiveOffsetSeries = new XYChart.Series<>();
        positiveOffsetSeries.setName("Positive Offset");

        for (Corrections c : temp) {
            String dateCreate = c.getDateCreate().format(DateTimeFormatter.ofPattern(localDateTimeFormat))
                    + "\n"
                    + c.getTimeCreate().format(DateTimeFormatter.ofPattern(localTimeFormat));
            if (c.getCost().compareTo(BigDecimal.ZERO) >= 0)
                positiveOffsetSeries.getData().add(new XYChart.Data<>(dateCreate, c.getCost()));
            else
                positiveOffsetSeries.getData().add(new XYChart.Data<>(dateCreate, BigDecimal.ZERO));
        }

        correctionBarChart.getData().add(positiveOffsetSeries);
        for (var element : correctionBarChart.getData().get(1).getData()) {
            element.getNode().setStyle("-fx-bar-fill: green;");
        }
        for (XYChart.Data<String, Number> data : positiveOffsetSeries.getData()) {
            if (data.getYValue().doubleValue() != 0.0) {
                StackPane bar = (StackPane) data.getNode();
                final Text dataText = new Text("+" + formatNumber(data.getYValue()));
                dataText.setRotate(-90.0);
//                Bounds bound = bar.localToScene(bar.getBoundsInLocal());
                dataText.setStyle("-fx-font: 10 arial;");
//                dataText.setTranslateY(dataText.getBoundsInParent().getHeight());
                bar.getChildren().add(dataText);
                Translate translate = new Translate(-(bar.getBoundsInParent().getMaxY() * 1.5), 0);
                dataText.getTransforms().add(translate);
            }
        }
        addTooltipForOffsets(temp, positiveOffsetSeries);
    }

    private void addNegativeOffsets(ArrayList<Corrections> temp, String localDateTimeFormat, String localTimeFormat) {
        XYChart.Series<String, Number> negativeOffsetSeries = new XYChart.Series<>();
        negativeOffsetSeries.setName("Negative Offset");

        for (Corrections c : temp) {
            String dateCreate = c.getDateCreate().format(DateTimeFormatter.ofPattern(localDateTimeFormat))
                    + "\n"
                    + c.getTimeCreate().format(DateTimeFormatter.ofPattern(localTimeFormat));
            if (c.getCost().compareTo(BigDecimal.ZERO) < 0)
                negativeOffsetSeries.getData().add(new XYChart.Data<>(dateCreate, c.getCost().abs()));
            else
                negativeOffsetSeries.getData().add(new XYChart.Data<>(dateCreate, BigDecimal.ZERO));
        }
        correctionBarChart.getData().add(negativeOffsetSeries);
        for (var element : correctionBarChart.getData().get(2).getData()) {
            element.getNode().setStyle("-fx-bar-fill: red;");
        }
        for (XYChart.Data<String, Number> data : negativeOffsetSeries.getData()) {
            if (data.getYValue().doubleValue() != 0.0) {
                StackPane bar = (StackPane) data.getNode();
                final Text dataText = new Text("-" + formatNumber(data.getYValue()));
                dataText.setRotate(-90.0);
//                Bounds bound = bar.localToScene(bar.getBoundsInLocal());
                dataText.setStyle("-fx-font: 10 arial;");
                bar.getChildren().add(dataText);
                //TODO
//                dataText.setTranslateY(bar.getBoundsInParent().getMaxY() + dataText.getBoundsInParent().getHeight()/2);
                Translate translate = new Translate(-(bar.getBoundsInParent().getMaxY() * 1.5), 0);
                dataText.getTransforms().add(translate);
            }
        }
        addTooltipForOffsets(temp, negativeOffsetSeries);
    }

    private void addTooltipForOffsets(ArrayList<Corrections> temp, XYChart.Series<String, Number> offsetSeries) {
        for (int i = 0; i < correctionBarChart.getData().get(1).getData().size(); i++) {
            if (offsetSeries.getData().get(i).getYValue().doubleValue() != 0.0) {
                Tooltip.install(offsetSeries.getData().get(i).getNode(),
                        new Tooltip(temp.get(i).getRemark()));
            }
        }
    }

    private void initDataDetailsTableView() {
        detailsTableView.getItems().clear();
        detailsTableView.getItems().addAll(correctionsService.findByDepartmentAndYear(CurrentDepartment, yearCB.getValue()));
    }

    private void reload() {
        initDataDetailsTableView();
        initDataStackedBarChart();
    }

    private void saveCorrection(Corrections correction) {
        correctionsService.save(correction);
        reload();
    }

    private void saveChangeLimit(MoneyLimit moneyLimit) {
        moneyLimitService.saveChangeLimit(moneyLimit);
        reload();
    }
}
