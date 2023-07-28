package dynamics.views.controllers;

import dynamics.Dynamics;
import dynamics.model.DynamicsException;
import dynamics.model.entities.Account;
import dynamics.model.services.LoginService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;

import static dynamics.utils.FilePathConstants.APPLICATION_ICON;
import static dynamics.utils.FilePathConstants.LOGIN_FXML;
import static dynamics.utils.TitleConstants.*;

@Controller
public class LoginController {

    private static Account loginUser;

    @Autowired
    private LoginService loginService;

    @FXML
    private Button closeBtn, loginBtn;
    @FXML
    private TextField loginId;
    @FXML
    private Label message, header, userTitle, passwordTitle;
    @FXML
    private PasswordField password;

    @FXML
    private void close() {
        loginBtn.getScene().getWindow().hide();
    }

    @FXML
    private void login() {
        try {
            loginUser = loginService.login(loginId.getText(), password.getText());
            // Запуск приложения
            MainFrameController.show();

            // Закрытие окна входа
            close();

        } catch (DynamicsException e) {
            message.setText(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            close();
        }
    }

    @FXML
    private void initialize() {
        settingTitles();
        loginId.setText(System.getProperty("user.name"));
        password.requestFocus();
        settingPasswordFieldKeyPressedEvent();
    }

    public static void loadView(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(LoginController.class.getClassLoader().getResource(LOGIN_FXML));
            loader.setControllerFactory(Dynamics.getApplicationContext()::getBean);
            Parent view = loader.load();
            Scene scene = new Scene(view);
            stage.setScene(scene);

            LoginController controller = loader.getController();
            controller.attachEvent();

            stage.setTitle(LOGIN_WINDOW_TITLE);
            stage.getIcons().add(new Image(APPLICATION_ICON));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void settingPasswordFieldKeyPressedEvent() {
        password.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                login();
            }
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                close();
            }
        });
    }

    private void attachEvent() {
        loginId.getScene().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if (closeBtn.isFocused()) {
                    close();
                }
                if (loginBtn.isFocused()) {
                    login();
                }
            }
            if (event.getCode() == KeyCode.ESCAPE) {
                close();
            }
        });
    }

    private void settingTitles() {
        header.setText(LOGIN_WINDOW_HEADER_TITLE);
        userTitle.setText(LOGIN_WINDOW_USER_TITLE);
        passwordTitle.setText(LOGIN_WINDOW_PASSWORD_TITLE);
        loginId.setPromptText(LOGIN_WINDOW_LOGIN_ID_PROMPT);
        password.setPromptText(LOGIN_WINDOW_PASSWORD_PROMPT);
        loginBtn.setText(LOGIN_WINDOW_LOGIN_BTN_TITLE);
        closeBtn.setText(LOGIN_WINDOW_CLOSE_BTN_TITLE);
    }

}
