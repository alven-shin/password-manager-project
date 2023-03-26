package password_manager;

import java.sql.SQLException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import password_manager.database.DatabaseDao;
import password_manager.database.tables.Account;

public class Sidebar {
    private ObservableList<Account> accounts;
    private VBox layout;
    private EntryPane entryPanel;
    private ListView<Account> list;

    public Sidebar(EntryPane entryPanel, DatabaseDao dao) throws SQLException {
        this.accounts = FXCollections.observableArrayList();
        this.entryPanel = entryPanel;

        var task = new Thread(() -> {
            try {
                this.accounts.addAll(dao.getAccounts());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        task.start();

        var createButton = new Button("+");
        createButton.setMaxWidth(Double.MAX_VALUE);
        createButton.tooltipProperty().setValue(new Tooltip("Add an account"));
        createButton.setOnMouseClicked(event -> {
            this.entryPanel.addEntry(this.accounts, this);
            layout.disableProperty().setValue(true);
        });

        this.layout = new VBox();
        this.list = new ListView<>(this.accounts);
        layout.getChildren().addAll(createButton, this.list);

        this.list.getSelectionModel().selectedItemProperty().addListener(event -> {
            var acc = this.list.getSelectionModel().getSelectedItem();
            if (acc != null) {
                this.entryPanel.showAccount(acc, this);
            }
        });
    }

    public VBox getLayout() {
        return this.layout;
    }

    public ListView<Account> getListView() {
        return this.list;
    }

    public void clearSelection() {
        this.list.getSelectionModel().clearSelection();
    }

    public void deleteSelectedAccount(DatabaseDao dao) {
        var idx = this.list.getSelectionModel().getSelectedIndex();
        var acc = this.accounts.remove(idx);
        this.list.getSelectionModel().clearSelection();

        var task = new Thread(() -> {
            try {
                dao.deleteAccount(acc);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        task.start();
    }
}
