import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;

public class CList {
    public TableColumn dateOpenColumn;
    public TableColumn NameColumn;
    public TableColumn TypeColumn;
    public TableColumn RateColumn;
    public TableColumn CountColumn;
    public TableColumn TermColumn;
    public TableColumn StatusColumn;
    public TableColumn ProlongColumn;
    public TableView depositTable;
    public Button closeDeposit;
    public Button prolongDeposit;
    public Button openDeposit;
    public Button buttonExit;
    public Button backButton;

    public void closeD(ActionEvent actionEvent) {
    }

    public void prolongD(ActionEvent actionEvent) {
    }

    public void openD(ActionEvent actionEvent) {
    }

    public void Exit_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void openMainPage(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("First_page_client.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}
