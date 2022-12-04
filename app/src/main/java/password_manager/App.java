package password_manager;

import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    private Stage window;

    @Override
    public void start(Stage stage) {
        this.window = stage;

        this.window.setTitle("Hello, world!");
        this.window.setScene(Login.registerScene(window));
        this.window.show();
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
