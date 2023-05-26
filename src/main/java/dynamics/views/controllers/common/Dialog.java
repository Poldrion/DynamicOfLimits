package dynamics.views.controllers.common;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import static dynamics.utils.FilePathsUtils.DIALOG_FXML;
import static dynamics.utils.TitlesUtils.DIALOG_CLOSE_BTN;
import static dynamics.utils.TitlesUtils.DIALOG_OK_BTN;

public class Dialog {

    @FXML
    private Label title;
    @FXML
    private Label message;
    @FXML
    private Button okBtn;
    @FXML
    private Button closeBtn;


    private Stage stage;

    public void show() {
        stage.showAndWait();
    }

    @FXML
    private void cancel() {
        okBtn.getScene().getWindow().hide();
    }

    public static class DialogBuilder {
        private String title;
        private String message;

        private ActionListener okActionListener;

        private DialogBuilder() {
        }

        public DialogBuilder okActionListener(ActionListener okActionListener) {
            this.okActionListener = okActionListener;
            return this;
        }

        public DialogBuilder message(String message) {
            this.message = message;
            return this;
        }

        public DialogBuilder title(String title) {
            this.title = title;
            return this;
        }

        public Dialog build() {
            try {
                Stage stage = new Stage(/*StageStyle.UNDECORATED*/);
                FXMLLoader loader = new FXMLLoader(Dialog.class.getClassLoader().getResource(DIALOG_FXML));
                Parent view = loader.load();
                Scene scene = new Scene(view);
                stage.setScene(scene);

                Dialog controller = loader.getController();
                controller.stage = stage;
                controller.attachEvent();

                controller.title.setText(this.title);
                controller.message.setText(this.message);
                controller.okBtn.setText(DIALOG_OK_BTN);

                if (okActionListener != null) {
                    controller.okBtn.setOnAction(event -> {
                        controller.cancel();
                        okActionListener.doAction();
                    });
                } else {
                    controller.okBtn.setVisible(false);
                    controller.closeBtn.setText(DIALOG_CLOSE_BTN);
                }
                return controller;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        public static DialogBuilder builder() {
            return new DialogBuilder();
        }
    }

    public static interface ActionListener {
        void doAction();
    }

    private void attachEvent() {
        message.getScene().setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER || keyEvent.getCode() == KeyCode.ESCAPE) {
                cancel();
            }
        });
    }
}
