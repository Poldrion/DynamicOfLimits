package dynamics;

import dynamics.model.BaseRepositoryImpl;
import dynamics.views.controllers.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl.class)
public class Dynamics extends Application {

    private static ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        applicationContext = SpringApplication.run(Dynamics.class);
    }

    @Override
    public void stop() {
        applicationContext.close();
    }

    @Override
    public void start(Stage stage) {
        LoginController.loadView(stage);
    }

    public static void main(String[] args) {
        launch();
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }
}