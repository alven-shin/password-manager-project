package password_manager;

import java.sql.SQLException;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import password_manager.database.DatabaseDao;
import password_manager.database.tables.Account;

public class EntryPane {
    private Window window;
    private VBox layout;
    private HBox buttonBar;
    private Button leftButton;
    private Button rightButton;
    private TextField websiteName;
    private TextField url;
    private TextField username;
    private HideableTextField password;
    private TextArea notes;
    private DatabaseDao dao;

    public EntryPane(Window owner, DatabaseDao dao) {
        this.dao = dao;
        this.window = owner;
        this.layout = new VBox();

        this.buttonBar = new HBox();
        this.buttonBar.alignmentProperty().setValue(Pos.CENTER_RIGHT);
        this.leftButton = new Button("Cancel");
        this.rightButton = new Button("Save");
        this.buttonBar.getChildren().addAll(leftButton, rightButton);

        var nameLabel = new Label("Website name:");
        this.websiteName = new TextField();

        var urlLabel = new Label("URL:");
        this.url = new TextField();

        var usernameLabel = new Label("Username:");
        this.username = new TextField();

        var passwordLabel = new Label("Password:");
        this.password = new HideableTextField();

        var notesLabel = new Label("Notes:");
        this.notes = new TextArea();

        this.layout.getChildren().addAll(buttonBar, nameLabel, this.websiteName, urlLabel, this.url, usernameLabel,
                this.username,
                passwordLabel, this.password.getLayout(), notesLabel, this.notes);
        this.layout.disableProperty().setValue(true);
    }

    public VBox getLayout() {
        return this.layout;
    }

    private void reset() {
        this.buttonBar.disableProperty().setValue(false);

        this.websiteName.clear();
        this.websiteName.setEditable(true);

        this.url.clear();
        this.url.setEditable(true);

        this.username.clear();
        this.username.setEditable(true);

        this.password.clear();
        this.password.setEditable(true);

        this.notes.clear();
        this.notes.setEditable(true);

        this.layout.disableProperty().setValue(true);
    }

    public void addEntry(ObservableList<Account> accounts, Sidebar sideBar) {
        this.reset();
        this.layout.disableProperty().setValue(false);

        this.rightButton.setText("Save");
        this.rightButton.setOnMouseClicked(event -> {
            if (this.websiteName.getText().isBlank() || this.username.getText().isBlank()) {
                Alert.createAlert(this.window, "Website name and Username fields are required!").show();
                return;
            }

            var websiteName = this.websiteName.getText();
            var url = this.url.getText();
            var username = this.username.getText();
            var password = this.password.getText();
            var notes = this.notes.getText();

            var task = new Thread(() -> {
                Account acc;
                try {
                    acc = new Account(websiteName, url, username,
                            password, notes, this.dao);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                Platform.runLater(() -> {
                    accounts.add(acc);
                });
            });
            task.start();

            this.reset();
            sideBar.getLayout().disableProperty().setValue(false);
        });

        this.leftButton.setText("Cancel");
        this.leftButton.setOnMouseClicked(event -> {
            this.reset();
            sideBar.getLayout().disableProperty().setValue(false);
        });
    }

    public void showAccount(Account acc, Sidebar sidebar) {
        this.websiteName.setText(acc.getWebsiteName());
        this.websiteName.setEditable(false);

        this.url.setText(acc.getUrl());
        this.url.setEditable(false);

        this.username.setText(acc.getUsername());
        this.username.setEditable(false);

        this.password.setText(acc.getPassword());
        this.password.setEditable(false);

        this.notes.setText(acc.getNote());
        this.notes.setEditable(false);

        this.leftButton.setText("Delete");
        this.leftButton.setOnMouseClicked(event -> {
            sidebar.deleteSelectedAccount(this.dao);
            this.reset();
        });

        this.rightButton.setText("Edit");
        this.rightButton.setOnMouseClicked(event -> {
            this.editAccount(acc, sidebar);
        });

        this.layout.disableProperty().setValue(false);
    }

    public void editAccount(Account acc, Sidebar sidebar) {
        this.websiteName.setEditable(true);
        this.url.setEditable(true);
        this.username.setEditable(true);
        this.password.setEditable(true);
        this.notes.setEditable(true);

        this.rightButton.setText("Save");
        this.rightButton.setOnMouseClicked(event -> {
            if (this.websiteName.getText().isBlank() || this.username.getText().isBlank()) {
                Alert.createAlert(this.window, "Website name and Username fields are required!").show();
                return;
            }

            var websiteName = this.websiteName.getText();
            var url = this.url.getText();
            var username = this.username.getText();
            var password = this.password.getText();
            var notes = this.notes.getText();

            acc.setWebsiteName(websiteName);
            acc.setUrl(url);
            acc.setUsername(username);
            acc.setPassword(password);
            acc.setNote(notes);
            var task = new Thread(() -> {
                try {
                    this.dao.updateAccount(acc);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            task.start();

            this.reset();
            sidebar.getLayout().disableProperty().setValue(false);
            sidebar.clearSelection();
        });

        this.leftButton.setText("Cancel");
        this.leftButton.setOnMouseClicked(event -> {
            this.reset();
            sidebar.getLayout().disableProperty().setValue(false);
            sidebar.clearSelection();
        });
    }
}
