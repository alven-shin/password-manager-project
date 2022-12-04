package password_manager;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Alert {
    public static Stage createAlert(Window owner, String alertText) {
        VBox vbox = new VBox(10);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPrefSize(300, 100);

        Text text = new Text(alertText);
        Button button = new Button("Ok");
        vbox.getChildren().addAll(text, button);
        Scene scene = new Scene(vbox);

        Stage stage = new Stage();
        stage.setScene(scene);

        // focus on the popup
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        stage.setResizable(false);

        button.setOnMouseClicked(event -> {
            stage.close();
        });
        return stage;
    }
}
