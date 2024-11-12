import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class FirstPageEmp {
    @FXML
    public Button newsletterButton;
    @FXML
    private Button buttonExit;

    Image image1 = new Image(getClass().getResourceAsStream("edit.png"));
    Image image2 = new Image(getClass().getResourceAsStream("message.png"));
    Image image3 = new Image(getClass().getResourceAsStream("mail.png"));
    Image image4 = new Image(getClass().getResourceAsStream("check.png"));

    public void Exit_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void openQuestions(ActionEvent actionEvent) {
    }

    public void openDeposits(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("EDeposits.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void openNewsletter(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) newsletterButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Notification.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void openConfirmations(ActionEvent actionEvent) {
    }
}
