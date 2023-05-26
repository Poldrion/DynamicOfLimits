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

@Controller
public class LoginController {

    private static Account loginUser;

    @Autowired
    private LoginService loginService;

    @FXML
    private Button closeBtn;
    @FXML
    private Button loginBtn;
    @FXML
    private TextField loginId;
    @FXML
    private Label message;
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
    void initialize() {
        loginId.setText(System.getProperty("user.name"));
        password.requestFocus();
        password.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                login();
            }
            if (keyEvent.getCode() == KeyCode.ESCAPE) {
                close();
            }
        });
    }

    public static void loadView(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(LoginController.class.getClassLoader().getResource("views/Login.fxml"));
            loader.setControllerFactory(Dynamics.getApplicationContext()::getBean);
            Parent view = loader.load();
            Scene scene = new Scene(view);
            stage.setScene(scene);

            LoginController controller = loader.getController();
            controller.attachEvent();

            stage.setTitle("Вход в приложение");
            stage.getIcons().add(new Image("images/appIcon.png"));
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

}
