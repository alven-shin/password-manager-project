package password_manager;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class HideableTextField {
    private StackPane layout;
    private TextField visibleField;
    private PasswordField hiddenField;

    public HideableTextField() {
        this.visibleField = new TextField();
        this.visibleField.setEditable(false);

        this.hiddenField = new PasswordField();
        this.visibleField.textProperty().bindBidirectional(this.hiddenField.textProperty());

        this.layout = new StackPane(hiddenField, visibleField);
        hiddenField.toFront();

        this.layout.setOnMouseEntered(event -> {
            this.visibleField.toFront();
        });
        this.layout.setOnMouseExited(event -> {
            this.hiddenField.toFront();
        });
    }

    public StackPane getLayout() {
        return this.layout;
    }

    public void setEditable(boolean val) {
        this.visibleField.setEditable(val);
        this.hiddenField.setEditable(val);
    }

    public void clear() {
        this.visibleField.clear();
        this.hiddenField.clear();
    }

    public void setText(String s) {
        this.visibleField.setText(s);
        this.hiddenField.setText(s);
    }

    public String getText() {
        return this.hiddenField.getText();
    }
}
