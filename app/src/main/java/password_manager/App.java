package password_manager;

import java.io.File;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.stage.Stage;
import password_manager.database.DatabaseDao;

public class App extends Application {
    private Stage window;
    private DatabaseDao dao;

    @Override
    public void start(Stage stage) throws SQLException {
        this.window = stage;
        this.dao = null;

        this.window.setTitle("Password Manager");
        this.showLogin();
        this.window.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (this.dao != null) {
            this.dao.close();
        }
    }

    private void showLogin() throws SQLException {
        File database = new File("data.db");
        if (database.exists()) {
            this.window.setScene(Login.loginScene(this, window));
        } else {
            this.window.setScene(Login.registerScene(this, window));
        }
    }

    public void setDao(DatabaseDao dao) {
        this.dao = dao;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
