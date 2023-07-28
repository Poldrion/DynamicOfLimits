package dynamics.views.controllers;


import dynamics.Dynamics;
import dynamics.utils.Contents;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

import java.io.IOException;

import static dynamics.utils.FilePathConstants.APPLICATION_ICON;
import static dynamics.utils.FilePathConstants.MAIN_FRAME_FXML;
import static dynamics.utils.TitleConstants.*;

@Controller
public class MainFrameController {

    @FXML
    private VBox businessPlan, generalInformation, detailInformation;
    @FXML
    private Label generalInformationTitle, businessPlanTitle, detailInformationTitle;
    @FXML
    private StackPane contentView;
    @FXML
    private HBox topBar;

    @FXML
    private void initialize() {
        settingTitles();
        loadView(Contents.Main);
    }

    public static void show() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(MainFrameController.class.getClassLoader().getResource(MAIN_FRAME_FXML));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(APPLICATION_TITLE_NAME);
            stage.getIcons().add(new Image(APPLICATION_ICON));
            stage.setMaximized(true);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadView(@SuppressWarnings("SameParameterValue") Contents content) {
        try {
            for (Node node : topBar.getChildren()) {
                node.getStyleClass().remove("active");
                if (node.getId().equals(content.name())) {
                    node.getStyleClass().add("active");
                } else {
                    node.getStyleClass().remove("active");
                }
            }

            contentView.getChildren().clear();

            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(content.getFxml()));
            loader.setControllerFactory(Dynamics.getApplicationContext()::getBean);
            Parent view = loader.load();
            contentView.getChildren().add(view);
            for (Node node : topBar.getChildren()) {
                if (!node.getStyleClass().contains("active"))
                    node.setVisible(false);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void settingTitles() {
        generalInformationTitle.setText(GENERAL_INFORMATION_TITLE);
        businessPlanTitle.setText(BUSINESS_PLAN_TITLE);
        detailInformationTitle.setText(DETAIL_INFORMATION_TITLE);
    }
}