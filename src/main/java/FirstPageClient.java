import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class FirstPageClient {
    public Button notifButton;
    public Button buttonExit;
    public Button confirmButton;

    public void openList(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) notifButton.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("CList.fxml")));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void openQuestions(ActionEvent actionEvent) {
    }

    public void openExtract(ActionEvent actionEvent) {
    }

    public void openNotifications(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) notifButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("CNotific.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void openBank(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) notifButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("ListDeposits.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void Exit_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}
