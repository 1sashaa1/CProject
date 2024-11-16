import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.Models.Entities.Notifications;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import com.google.gson.reflect.TypeToken;
import main.idea.DTO.Session;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.scene.control.cell.PropertyValueFactory;


public class CNotification {
    @FXML
    public Button buttonExit;
    @FXML
    public Button buttonSearch;
    @FXML
    public ListView<String> notificationList;
    @FXML
    public TextField searchField;
    @FXML
    public Label titleLabel;
    @FXML
    public TableView<Notifications> notificationTable;
    @FXML
    public DatePicker dateFilter;
    @FXML
    public TextField subjectFilter;
    @FXML
    public TableColumn<Notifications, Integer> idColumn;
    @FXML
    public TableColumn<Notifications, String> messageColumn;
    @FXML
    public TableColumn<Notifications, String> readStatusColumn;
    @FXML
    public ComboBox statusFilter;
    public Button backButton;

    private int clientId = Session.getClientId();
    private List<Notifications> allNotifications = new ArrayList<>();

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public void Exit_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void searchNotifications(ActionEvent actionEvent) {
        String searchText = searchField.getText();
        if (searchText != null && !searchText.isEmpty()) {
            List<String> foundNotifications = findNotifications(searchText);
            notificationList.getItems().setAll(foundNotifications);
        } else {
            List<String> messages = allNotifications.stream()
                    .map(Notifications::getMessage)
                    .collect(Collectors.toList());
            notificationList.getItems().setAll(messages);
        }
    }

    private void getAllNotifications(Consumer<List<Notifications>> callback) {
        int clientId = Session.getClientId();
        Request request = new Request();
        request.setRequestType(RequestType.GETNOTIFICATIONS);
        request.setRequestMessage(new Gson().toJson(clientId));

        PrintWriter out = ClientSocket.getInstance().getOut();
        if (out != null) {
            out.println(new Gson().toJson(request));
            out.flush();
        } else {
            System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
            return;
        }
        System.out.println("Отправляемый запрос: " + new Gson().toJson(request));

        new Thread(() -> {
            try {
                String answer = ClientSocket.getInstance().getInStream().readLine();
                if (answer != null) {
                    Response response = new Gson().fromJson(answer, Response.class);
                    System.out.println(response.getResponseUser());

                    if (response.getResponseStatus() == ResponseStatus.OK) {
                        String jsonResponse = response.getResponseUser();

                        Type listType = new TypeToken<List<Notifications>>() {}.getType();
                        List<Notifications> notifications = new Gson().fromJson(jsonResponse, listType);

                        Platform.runLater(() -> {
                            if (notificationTable != null) {
                                // Устанавливаем источник данных для каждого столбца
                                idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
                                messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));

                                // Для столбца статуса добавляем проверку, чтобы отобразить "Прочитано" или "Не прочитано"
                                readStatusColumn.setCellValueFactory(cellData ->
                                        new SimpleStringProperty(cellData.getValue().isRead() ? "Прочитано" : "Не прочитано")
                                );

                                // Устанавливаем данные в таблицу
                                notificationTable.getItems().setAll(notifications);
                            } else {
                                System.err.println("Ошибка: notificationTable не инициализирован.");
                            }
                        });


                        // Передаем полученные данные в callback
                        callback.accept(notifications);
                    } else {
                        System.out.println("Ошибка: Не удалось загрузить данные из базы данных");
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при получении данных: " + e.getMessage());
            }
        }).start();
    }

    @FXML
    public void initialize() {
        ObservableList<String> citizenshipOptions = FXCollections.observableArrayList(
                "Не просмотрено",
                "Просмотрено"
        );

        statusFilter.setItems(citizenshipOptions);

        // Загружаем все уведомления и устанавливаем их в таблицу и список
        getAllNotifications(notifications -> {
            if (notifications != null) {
                allNotifications = notifications; // Сохраняем все уведомления
                notificationTable.getItems().setAll(allNotifications); // Заполняем таблицу уведомлениями
            } else {
                System.err.println("Ошибка: Уведомления не были загружены.");
            }
        });
    }

    private List<String> findNotifications(String searchText) {
        return allNotifications.stream()
                .filter(notification -> notification.getMessage() != null &&
                        notification.getMessage().toLowerCase().contains(searchText.toLowerCase()))
                .map(Notifications::getMessage)
                .collect(Collectors.toList());
    }

    public void openMainPage(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("First_page_client.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}
