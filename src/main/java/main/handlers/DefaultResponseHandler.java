package main.handlers;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import main.Models.TCP.Response;
import main.interfaces.ResponseHandler;

public class DefaultResponseHandler implements ResponseHandler {
    @Override
    public void handle(Response response) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Неизвестный ответ");
            alert.setHeaderText(null);
            alert.setContentText("Получен неизвестный статус ответа. Проверьте соединение с сервером.");
            alert.showAndWait();
        });
    }
}
