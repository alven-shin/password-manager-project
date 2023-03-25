package password_manager;

import java.io.File;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
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
        var scene = new Scene(new VBox());
        this.showLogin(scene);
        this.window.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (this.dao != null) {
            this.dao.close();
        }
    }

    private void showLogin(Scene scene) throws SQLException {
        File database = new File("data.db");
        if (database.exists()) {
            Login.loginScene(this, window);
        } else {
            Login.registerScene(this, window);
        }
    }

    public void switchScenes(Scene scene) {
        this.window.setScene(scene);
    }

    public void setDao(DatabaseDao dao) {
        this.dao = dao;
    }

    public boolean checkDaoStatus() {
        return this.dao != null;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    public DatabaseDao getDao() {
        return this.dao;
    }
}
