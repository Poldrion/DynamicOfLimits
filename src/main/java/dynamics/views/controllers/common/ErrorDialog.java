package dynamics.views.controllers.common;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import static dynamics.utils.FilePathConstants.ERROR_DIALOG_FXML;
import static dynamics.utils.TitleConstants.ERROR_DIALOG_CLOSE_BTN;
import static dynamics.utils.TitleConstants.ERROR_DIALOG_OK_BTN;

public class ErrorDialog {

    @FXML
    private Label title;
    @FXML
    private TextArea message;
    @FXML
    private Button okBtn, closeBtn;

    private Stage stage;

    public void show() {
        stage.showAndWait();
    }

    @FXML
    private void cancel() {
        okBtn.getScene().getWindow().hide();
    }

    public static class ErrorDialogBuilder {
        private String title;
        private String message;

        private ErrorDialog.ActionListener okActionListener;

        private ErrorDialogBuilder() {
        }

        public ErrorDialog.ErrorDialogBuilder okActionListener(ErrorDialog.ActionListener okActionListener) {
            this.okActionListener = okActionListener;
            return this;
        }

        public ErrorDialog.ErrorDialogBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorDialog.ErrorDialogBuilder title(String title) {
            this.title = title;
            return this;
        }

        public ErrorDialog build() {

            try {
                Stage stage = new Stage();
                FXMLLoader loader = new FXMLLoader(ErrorDialog.class.getClassLoader().getResource(ERROR_DIALOG_FXML));
                Parent view = loader.load();
                Scene scene = new Scene(view);
                stage.setScene(scene);

                ErrorDialog controller = loader.getController();
                controller.stage = stage;

                controller.title.setText(this.title);
                controller.message.setText(this.message);
                controller.okBtn.setText(ERROR_DIALOG_OK_BTN);

                if (okActionListener != null) {
                    controller.okBtn.setOnAction(event -> {
                        controller.cancel();
                        okActionListener.doAction();
                    });
                } else {
                    controller.okBtn.setVisible(false);
                    controller.closeBtn.setText(ERROR_DIALOG_CLOSE_BTN);
                }

                return controller;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }

        public static ErrorDialog.ErrorDialogBuilder builder() {
            return new ErrorDialog.ErrorDialogBuilder();
        }

    }

    public interface ActionListener {
        void doAction();
    }

}
