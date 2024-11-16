import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.Models.Entities.Deposit;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;
import main.idea.DTO.Session;

import java.io.IOException;
import java.io.PrintWriter;

public class ListDeposits {
    @FXML
    public TableColumn idColumn;
    @FXML
    public TableColumn NameColumn;
    @FXML
    public TableColumn TypeColumn;
    @FXML
    public TableColumn RateColumn;
    @FXML
    public TableColumn MinCountColumn;
    @FXML
    public TableColumn AddColumn;
    @FXML
    public TableColumn TransactionColumn;
    @FXML
    public TableColumn ProlongationColumn;
    @FXML
    public TableColumn TermColumn;
    @FXML
    public TableView depositTable;
    @FXML
    public Button openDeposit;
    @FXML
    public Button compareDeposits;
    @FXML
    public Button buttonExit;
    @FXML
    public HBox comparisonBox;

    public FlowPane comparisonPane;

    private final ObservableList<Deposit> depositsList = FXCollections.observableArrayList();
    private final ObservableList<Deposit> selectedDeposits = FXCollections.observableArrayList();


    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        TypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        RateColumn.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        MinCountColumn.setCellValueFactory(new PropertyValueFactory<>("minAmount"));
        TermColumn.setCellValueFactory(new PropertyValueFactory<>("term"));
        AddColumn.setCellValueFactory(new PropertyValueFactory<>("replenishment"));
        TransactionColumn.setCellValueFactory(new PropertyValueFactory<>("transactions"));
        ProlongationColumn.setCellValueFactory(new PropertyValueFactory<>("prolongation"));

        depositTable.setItems(depositsList);
        try {
            loadDataFromDatabase();
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке данных из базы данных: " + e.getMessage());
        }

        depositTable.setRowFactory(tv -> {
            TableRow<Deposit> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty()) {
                    Deposit clickedDeposit = row.getItem();

                    // Добавляем или удаляем выбранные строки вручную
                    if (selectedDeposits.contains(clickedDeposit)) {
                        selectedDeposits.remove(clickedDeposit);
                        row.getStyleClass().remove("selected-row");
                    } else if (selectedDeposits.size() < 3) {  // Ограничиваем выбор до 3 строк
                        selectedDeposits.add(clickedDeposit);
                        row.getStyleClass().add("selected-row");
                    } else {
                        System.out.println("Можно выбрать только 3 строки.");
                    }
                }
            });
            return row;
        });
    }

    private void loadDataFromDatabase() throws IOException {
        Request request = new Request();
        request.setRequestType(RequestType.GETDEPOSITS);

        PrintWriter out = ClientSocket.getInstance().getOut();
        if (out != null) {
            out.println(new Gson().toJson(request));
            out.flush();
        } else {
            System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
        }
        System.out.println("Отправляемый запрос: " + new Gson().toJson(request));
        ClientSocket.getInstance().getOut().flush();

        new Thread(() -> {
            try {
                String answer = ClientSocket.getInstance().getInStream().readLine();
                if (answer != null) {
                    Response response = new Gson().fromJson(answer, Response.class);
                    System.out.println(response.getResponseUser());

                    if (response.getResponseStatus() == ResponseStatus.OK) {
                        String jsonResponse = response.getResponseUser();
                        if (jsonResponse.startsWith("[")) {

                            Deposit[] depositsArray = new Gson().fromJson(jsonResponse, Deposit[].class);
                            Platform.runLater(() -> {
                                depositsList.clear();
                                depositsList.addAll(depositsArray);
                            });
                        } else {

                            Deposit singleDeposit = new Gson().fromJson(jsonResponse, Deposit.class);
                            Platform.runLater(() -> {
                                depositsList.clear();
                                depositsList.add(singleDeposit);
                            });
                        }
                    } else {
                        System.out.println("Ошибка: Не удалось загрузить данные из базы данных");
                    }
                }
            } catch (IOException e) {
                System.err.println("Ошибка при получении данных: " + e.getMessage());
            }
        }).start();

    }

    public void Exit_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void openD(ActionEvent actionEvent) {
        Deposit selectedDeposit = (Deposit) depositTable.getSelectionModel().getSelectedItem();
        if (selectedDeposit != null) {
            try {
                int clientId = Session.getClientId();
                Request request = new Request();
                request.setRequestType(RequestType.OPENDEPOSIT);
                JsonObject requestMessage = new JsonObject();
                requestMessage.addProperty("clientId", clientId);
                requestMessage.addProperty("depositId", selectedDeposit.getId());

                request.setRequestMessage(new Gson().toJson(requestMessage));

                System.out.println("Запрос на открытие депозита " + request.getRequestMessage());
                PrintWriter out = ClientSocket.getInstance().getOut();
                if (out != null) {
                    out.println(new Gson().toJson(request));
                    out.flush();
                } else {
                    System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
                }

                System.out.println("Отправлен запрос на открытие депозита: " + selectedDeposit.getId());

                new Thread(() -> {
                    try {
                        String answer = ClientSocket.getInstance().getInStream().readLine();
                        if (answer != null) {
                            Response response = new Gson().fromJson(answer, Response.class);
                            if (response.getResponseStatus() == ResponseStatus.OK) {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.setTitle("Открытие депозита");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Депозит успешно отправлен на рассмотрение!");
                                    alert.showAndWait();
                                });
                            } else {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка открытия депозита");
                                    alert.setHeaderText(null);
                                    alert.setContentText("Не удалось открыть депозит.");
                                    alert.showAndWait();
                                });
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Ошибка при получении ответа: " + e.getMessage());
                    }
                }).start();

            } catch (Exception e) {
                System.err.println("Ошибка при попытке открыть депозит: " + e.getMessage());
            }
        } else {
            // Если депозит не выбран, показать предупреждение
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Выбор депозита");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, выберите депозит для открытия.");
            alert.showAndWait();
        }
    }

    public void compareD(ActionEvent actionEvent) {
        if (selectedDeposits.size() < 2 || selectedDeposits.size() > 3) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Сравнение депозитов");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, выберите 2 или 3 депозита для сравнения.");
            alert.showAndWait();
            return;
        }

        comparisonBox.getChildren().clear();

        for (Deposit deposit : selectedDeposits) {
            VBox depositBox = new VBox();
            depositBox.setSpacing(10);
            depositBox.setPadding(new Insets(10));
            depositBox.setStyle("-fx-border-color: #3c9d7a; -fx-background-color: #f0f0f0; -fx-border-radius: 5; -fx-background-radius: 5;");

            Label nameLabel = new Label("Название: " + deposit.getName());
            Label typeLabel = new Label("Тип: " + deposit.getType());
            Label rateLabel = new Label("Процентная ставка: " + deposit.getInterestRate());
            Label minCountLabel = new Label("Мин. сумма: " + deposit.getMinAmount());
            Label termLabel = new Label("Срок: " + deposit.getTerm());

            depositBox.getChildren().addAll(nameLabel, typeLabel, rateLabel, minCountLabel, termLabel);

            comparisonBox.getChildren().add(depositBox);
        }
    }
    public void openMainPage(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("First_page_client.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }
}