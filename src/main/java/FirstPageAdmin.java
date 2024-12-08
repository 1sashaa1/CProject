import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class FirstPageAdmin {
    public Button buttonExit;
    public Button clButton;
    public Button empButton;

    public void Exit_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void openClients(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) clButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("WorkwithCl.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void openEmployees(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) empButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("WorkwithEmp.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}
