package password_manager;

import java.sql.SQLException;

import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import password_manager.database.DatabaseDao;

public class Home {
    private EntryPane entryPanel;
    private Sidebar sidebar;

    public Home(Window owner, DatabaseDao dao) throws SQLException {
        this.entryPanel = new EntryPane(owner, dao);
        this.sidebar = new Sidebar(this.entryPanel, dao);
    }

    public void showScene(App app) {
        var layout = new HBox();
        layout.getChildren().addAll(this.sidebar.getLayout(), this.entryPanel.getLayout());
        var scene = new Scene(layout);
        app.switchScenes(scene);
    }
}
