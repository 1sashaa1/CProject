import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.Models.Entities.Client;
import main.Models.Entities.Deposit;
import main.Models.Entities.Notifications;
import main.Models.Entities.User;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class Notification {
    @FXML
    private ComboBox<Client> clientList;
    @FXML
    private Button sendMessageButton;
    @FXML
    private RadioButton radioAllClients;
    @FXML
    private RadioButton radioSingleClient;
    @FXML
    private TextArea messageText;
    @FXML
    private Button buttonExit;

    private List<Client> clients = new ArrayList<>();

    @FXML
    public void initialize() {
        ToggleGroup clientGroup = new ToggleGroup();
        radioSingleClient.setToggleGroup(clientGroup);
        radioAllClients.setToggleGroup(clientGroup);
        loadClients();
    }

    private void loadClients() {
        Request request = new Request();
        request.setRequestType(RequestType.GETCLIENTS);

        PrintWriter out = ClientSocket.getInstance().getOut();
        if (out != null) {
            out.println(new Gson().toJson(request));
            out.flush();
        } else {
            System.err.println("Ошибка: PrintWriter равен null. Убедитесь, что соединение установлено.");
            return;
        }

        new Thread(() -> {
            try {
                String answer = ClientSocket.getInstance().getInStream().readLine();
                if (answer != null) {
                    Response response = new Gson().fromJson(answer, Response.class);
                    if (response.getResponseStatus() == ResponseStatus.OK) {
                        String jsonResponse = response.getResponseUser();
                        clients = new ArrayList<>(Arrays.asList(new Gson().fromJson(jsonResponse, Client[].class)));
                        Platform.runLater(() -> {
                            if (clientList != null) {
                                clientList.getItems().clear();
                                clientList.getItems().addAll(clients);
                            } else {
                                System.err.println("Ошибка: clientList не инициализирован.");
                            }
                        });
                    } else {
                        System.out.println("Ошибка: Не удалось загрузить данные из базы.");
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при получении данных: " + e.getMessage());
            }
        }).start();
    }

    public void sendNotificationToClient(int clientId, String message) {
        Notifications notification = new Notifications();
        notification.setClientId(clientId);
        notification.setMessage(message);
        notification.setRead(false);

        Request request = new Request();
        request.setRequestType(RequestType.SENDNOTIFICATION);
        request.setRequestMessage(new Gson().toJson(notification));

        PrintWriter out = ClientSocket.getInstance().getOut();
        if (out != null) {
            out.println(new Gson().toJson(request));
            out.flush();
        } else {
            System.err.println("Ошибка: PrintWriter равен null.");
        }
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Успешно!");
        alert.setHeaderText(null);
        alert.setContentText("Сообщение отправлено клиенту");
        alert.showAndWait();
    }

    public void broadcastNotification(String message) {
        for (Client client : clients) {
            sendNotificationToClient(client.getClientId(), message);
        }
    }

    public void sendMessage(ActionEvent actionEvent) {
        String message = messageText.getText().trim();
        if (message.isEmpty() || message.length() < 10) {
            showAlert("Ошибка", "Сообщение должно содержать не менее 10 символов.");
            return;
        }

        if (radioAllClients.isSelected()) {
            broadcastNotification(message);
        } else if (radioSingleClient.isSelected()) {
            if (clientList.getValue() == null) {
                showAlert("Ошибка", "Пожалуйста, выберите клиента из списка.");
                return;
            }
            Client selectedClient = clientList.getValue();
            sendNotificationToClient(selectedClient.getClientId(), message);
        } else {
            showAlert("Ошибка", "Пожалуйста, выберите получателя: одному клиенту или всем.");
        }
    }

    public void Exit_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void openMainPage(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("First_page_employee.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}