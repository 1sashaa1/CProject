import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.Models.Entities.Client;
import main.Models.Entities.Deposit;
import main.Models.Entities.User;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;
import main.idea.DTO.DepositListClient;
import main.idea.DTO.RequestClientsDepositsDTO;
import main.idea.DTO.Session;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

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
    private ObservableList<DepositListClient> depositsList = FXCollections.observableArrayList();


    public void initialize() {
        depositTable.setItems(depositsList);
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("nameDeposit"));
        dateOpenColumn.setCellValueFactory(new PropertyValueFactory<>("openingDate"));
        CountColumn.setCellValueFactory(new PropertyValueFactory<>("firstAmount"));
        TypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        RateColumn.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        TermColumn.setCellValueFactory(new PropertyValueFactory<>("term"));
        ProlongColumn.setCellValueFactory(new PropertyValueFactory<>("prolongation"));


        try {
            loadDataFromDatabase();
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке данных из базы данных: " + e.getMessage());
        }
    }

    private void loadDataFromDatabase() throws IOException {
        Request request = new Request();
        request.setRequestType(RequestType.GETMYDEPOSITS);
        Client currentUser = ClientSocket.getInstance().getClient();

        int clientId = Session.getClientId();
        System.out.println(clientId);
        JsonObject requestMessage = new JsonObject();
        requestMessage.addProperty("clientId", clientId);

        request.setRequestMessage(new Gson().toJson(requestMessage));

        PrintWriter out2 = ClientSocket.getInstance().getOut();
        if (out2 != null) {
            out2.println(new Gson().toJson(request));
            out2.flush();
        } else {
            System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
        }
        System.out.println("Отправляемый запрос: " + new Gson().toJson(request));
        ClientSocket.getInstance().getOut().flush();

        new Thread(() -> {
            try {
                String answer2 = ClientSocket.getInstance().getInStream().readLine();
                if (answer2 != null) {
                    Response response2 = new Gson().fromJson(answer2, Response.class);
                    System.out.println(response2.getResponseUser());

                    if (response2.getResponseStatus() == ResponseStatus.OK) {
                        String jsonResponse = response2.getResponseUser();

                        DepositListClient[] clientDepositsArray = new Gson().fromJson(jsonResponse, DepositListClient[].class);
                        System.out.println("Parsed Deposits: " + Arrays.toString(clientDepositsArray));

                        Platform.runLater(() -> {
                            depositsList.clear();
                            depositsList.addAll(clientDepositsArray);
                        });
                    }
                } else {
                    System.out.println("Ошибка: Не удалось загрузить данные из базы данных");
                }
            } catch (IOException e) {
                System.err.println("Ошибка при получении данных: " + e.getMessage());
            }
        }).start();

    }

    public void closeD(ActionEvent actionEvent) {
        Deposit selectedDeposit = (Deposit) depositTable.getSelectionModel().getSelectedItem();
        if (selectedDeposit != null) {
            try {
                int clientId = Session.getClientId();
                Request request = new Request();
                request.setRequestType(RequestType.CLOSEDEPOSIT);
                JsonObject requestMessage = new JsonObject();
                requestMessage.addProperty("clientId", clientId);
                requestMessage.addProperty("depositId", selectedDeposit.getId());

                request.setRequestMessage(new Gson().toJson(requestMessage));

                System.out.println("Запрос на закрытие депозита " + request.getRequestMessage());
                PrintWriter out = ClientSocket.getInstance().getOut();
                if (out != null) {
                    out.println(new Gson().toJson(request));
                    out.flush();
                } else {
                    System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
                }

                System.out.println("Отправлен запрос на закрытие депозита: " + selectedDeposit.getId());

                new Thread(() -> {
                    try {
                        String answer = ClientSocket.getInstance().getInStream().readLine();
                        if (answer != null) {
                            Response response = new Gson().fromJson(answer, Response.class);
                            if (response.getResponseStatus() == ResponseStatus.OK) {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Закрытие депозита");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Депозит успешно отправлен на рассмотрение!");
                                    alert.showAndWait();
                                });
                            } else {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка закрытия депозита");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Не удалось закрыть депозит.");
                                    alert.showAndWait();
                                });
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Ошибка при получении ответа: " + e.getMessage());
                    }
                }).start();

            } catch (Exception e) {
                System.err.println("Ошибка при попытке закрыть депозит: " + e.getMessage());
            }
        } else {
            // Если депозит не выбран, показать предупреждение
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Выбор депозита");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, выберите депозит для закрытия.");
            alert.showAndWait();
        }
    }

    public void prolongD(ActionEvent actionEvent) {

    }

    public void openD(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
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

    public void openMainPage(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("First_page_client.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}
