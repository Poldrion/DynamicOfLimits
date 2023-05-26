package dynamics.views.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;

public class DetailsController {
    @FXML
    private Button addCorrections;

    @FXML
    private Button backBtn;

    @FXML
    private TableColumn<?, ?> correctionsCostCol;

    @FXML
    private Label correctionsTitle;

    @FXML
    private TableColumn<?, ?> currentLimitCostCol;

    @FXML
    private TableColumn<?, ?> dateCorrectionsCol;

    @FXML
    private Button deleteCorrections;

    @FXML
    private Label departmentTitle;

    @FXML
    private Button editCorrections;

    @FXML
    private TableColumn<?, ?> lastCostLimitCol;

    @FXML
    private TableColumn<?, ?> remarkCol;

    @FXML
    private ComboBox<?> yearCB;

    @FXML
    private Label yearTitle;

}
