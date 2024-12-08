package main.handlers;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import main.Models.TCP.Response;
import main.interfaces.ResponseHandler;

public class ErrorRegistrationHandler implements ResponseHandler {
    @Override
    public void handle(Response response) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка регистрации");
            alert.setHeaderText(null);
            alert.setContentText("Не удалось создать пользователя, проверьте введённые данные.");
            alert.showAndWait();
        });
    }
}
