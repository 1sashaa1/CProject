package main.handlers;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import main.Models.TCP.Response;
import main.interfaces.ResponseHandler;

public class SuccessfulRegistrationHandler implements ResponseHandler {
    @Override
    public void handle(Response response) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Регистрация");
            alert.setHeaderText(null);
            alert.setContentText("Регистрация прошла успешно!");
            alert.showAndWait();
        });
    }
}
