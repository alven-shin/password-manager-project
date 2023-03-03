package password_manager;

import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Login {
    public static Scene registerScene(Window owner, Database db) {
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setPrefHeight(100);
        layout.setPadding(new Insets(40));

        Text instructions = new Text("Please enter a strong master password below to secure your data.");
        VBox.setMargin(instructions, new Insets(0, 0, 10, 0));

        PasswordField passwordTextbox = new PasswordField();
        passwordTextbox.setPromptText("Enter a master password");

        PasswordField confirmTextbox = new PasswordField();
        confirmTextbox.setPromptText("Confirm your master password");

        Button confirmButton = new Button("Set password");
        VBox.setMargin(confirmButton, new Insets(10, 0, 0, 0));
        confirmButton.setOnMouseClicked(event -> {
            // reset textboxes for the next submissions
            String pass = passwordTextbox.getText().strip();
            String confirm = confirmTextbox.getText().strip();
            passwordTextbox.clear();
            confirmTextbox.clear();

            // validate passwords
            if (pass.isEmpty()) {
                Stage s = Alert.createAlert(owner, "Password cannot be empty!");
                s.show();
            } else if (!pass.equals(confirm)) {
                Stage s = Alert.createAlert(owner, "Passwords do not match!");
                s.show();
            } else {
                try {
                    db.unlock(pass);
                    Stage s = Alert.createAlert(owner, "Your master password has been set.");
                    s.show();
                } catch (SQLException e) {
                    Stage s = Alert.createAlert(owner, e.toString());
                    s.show();
                }
            }
        });

        layout.getChildren().addAll(instructions, passwordTextbox, confirmTextbox, confirmButton);
        Scene s = new Scene(layout);
        return s;
    }

    public static Scene loginScene(Window owner, Database db) {
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setPrefHeight(100);
        layout.setPadding(new Insets(40));

        Text instructions = new Text("Please enter a your master password.");
        VBox.setMargin(instructions, new Insets(0, 0, 10, 0));

        PasswordField passwordTextbox = new PasswordField();
        passwordTextbox.setPromptText("Enter your password");

        Button confirmButton = new Button("Login");
        VBox.setMargin(confirmButton, new Insets(10, 0, 0, 0));
        confirmButton.setOnMouseClicked(event -> {
            // String pass = passwordTextbox.getText().strip();
            passwordTextbox.clear();
            // try {
            System.out.println("unlocked!");
            // } catch (SQLException e) {
            // Stage s = Alert.createAlert(owner, "Master password is incorrect!");
            // s.show();
            // }
        });

        layout.getChildren().addAll(instructions, passwordTextbox, confirmButton);
        Scene s = new Scene(layout);
        return s;
    }
}
