import com.google.gson.Gson;
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
import main.Models.Entities.ClientsDeposits;
import main.Models.Entities.Deposit;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;
import main.idea.DTO.ClientsDepositsDTO;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.time.LocalDate;

public class Confirmations {
    public Button deleteDeposit;
    public Button changeDeposit;
    public Button closeDepositButton;
    public Button openDepositButton;
    public Button buttonExit;
    public TextField depositAmountField;
    public DatePicker depositDatePicker;
    public TableView depositTable;
    public TableColumn NameColumn;
    public TableColumn TypeColumn;
    public TableColumn RateColumn;
    public TableColumn MinCountColumn;
    public TableColumn TermColumn;
    public TableColumn AddColumn;
    public TableColumn TransactionColumn;
    public TableColumn ProlongationColumn;
    public TableColumn StatusColumn;
    public TableColumn DateColumn;
    public TableColumn SumColumn;
    public Button backButton;
    public TableView confTable;
    public TableColumn ClientNameColumn;
    public TableColumn ClientPColumn;
    public TableColumn ClientSecondNameColumn;

    public TableColumn idColumn;
    public TableColumn NameDepositColumn;

    private ObservableList<Deposit> depositsList = FXCollections.observableArrayList();
    private ObservableList<ClientsDepositsDTO> clientsdepositsList = FXCollections.observableArrayList();

    public void initialize() {

        depositTable.setItems(depositsList);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        NameDepositColumn.setCellValueFactory(new PropertyValueFactory<>("nameDeposit"));
        TypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        RateColumn.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        MinCountColumn.setCellValueFactory(new PropertyValueFactory<>("minAmount"));
        TermColumn.setCellValueFactory(new PropertyValueFactory<>("term"));
        AddColumn.setCellValueFactory(new PropertyValueFactory<>("replenishment"));
        TransactionColumn.setCellValueFactory(new PropertyValueFactory<>("transactions"));
        ProlongationColumn.setCellValueFactory(new PropertyValueFactory<>("prolongation"));


        confTable.setItems(clientsdepositsList);
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("nameDeposit"));
        StatusColumn.setCellValueFactory(new PropertyValueFactory<>("isOpen"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<>("openingDate"));
        SumColumn.setCellValueFactory(new PropertyValueFactory<>("firstAmount"));
        ClientNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ClientPColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        ClientSecondNameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));

        try {
            loadDataFromDatabase();
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке данных из базы данных: " + e.getMessage());
        }
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

        Request request2 = new Request();
        request2.setRequestType(RequestType.GETCLIENTSDEPOSITS);

        PrintWriter out2 = ClientSocket.getInstance().getOut();
        if (out2 != null) {
            out2.println(new Gson().toJson(request2));
            out2.flush();
        } else {
            System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
        }
        System.out.println("Отправляемый запрос: " + new Gson().toJson(request2));
        ClientSocket.getInstance().getOut().flush();

        new Thread(() -> {
            try {
                String answer2 = ClientSocket.getInstance().getInStream().readLine();
                if (answer2 != null) {
                    Response response2 = new Gson().fromJson(answer2, Response.class);
                    System.out.println(response2.getResponseUser());

                    if (response2.getResponseStatus() == ResponseStatus.OK) {
                        String jsonResponse = response2.getResponseUser();

                        ClientsDepositsDTO[] clientDepositsArray = new Gson().fromJson(jsonResponse, ClientsDepositsDTO[].class);
                        Platform.runLater(() -> {
                            clientsdepositsList.clear();
                            clientsdepositsList.addAll(clientDepositsArray);
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

    public void openMainPage(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("First_page_employee.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void deleteD(ActionEvent actionEvent) {
    }

    public void changeD(ActionEvent actionEvent) {
    }

    public void closeDeposit(ActionEvent actionEvent) {
        ClientsDepositsDTO selectedDeposit = (ClientsDepositsDTO) confTable.getSelectionModel().getSelectedItem();
        if (selectedDeposit == null) {
            showAlert("Ошибка", "Выберите вклад для закрытия!");
            return;
        }

        selectedDeposit.setOpen(false);
        confTable.refresh();

        Request request = new Request();
        request.setRequestType(RequestType.ECLOSEDEPOSIT);
        request.setRequestMessage(new Gson().toJson(new ClientsDepositsDTO(
                selectedDeposit.getIdDeposit(),
                selectedDeposit.getNameDeposit(),
                0,
                null,
                false
        )));

        PrintWriter out = ClientSocket.getInstance().getOut();
        if (out != null) {
            out.println(new Gson().toJson(request));
            out.flush();
        } else {
            System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
        }
        System.out.println("Отправляемый запрос: " + new Gson().toJson(request));
        ClientSocket.getInstance().getOut().flush();

        showAlert("Успех", "Вклад успешно закрыт!");
    }

    public void openDeposit(ActionEvent actionEvent) {
        ClientsDepositsDTO selectedDeposit = (ClientsDepositsDTO) confTable.getSelectionModel().getSelectedItem();
        if (selectedDeposit == null) {
            showAlert("Ошибка", "Выберите вклад для открытия!");
            return;
        }

        try {
            double amount = Double.parseDouble(depositAmountField.getText());
            Date openDate = Date.valueOf(depositDatePicker.getValue());

            if (openDate == null) {
                showAlert("Ошибка", "Введите дату открытия!");
                return;
            }

            selectedDeposit.setOpen(true);
            selectedDeposit.setFirstAmount(amount);
            selectedDeposit.setOpeningDate(openDate);
            confTable.refresh();

            Request request = new Request();
            request.setRequestType(RequestType.EOPENDEPOSIT);
            request.setRequestMessage(new Gson().toJson(new ClientsDepositsDTO(
                    selectedDeposit.getIdDeposit(),
                    selectedDeposit.getNameDeposit(),
                    amount,
                    openDate,
                    true
            )));

            PrintWriter out = ClientSocket.getInstance().getOut();
            if (out != null) {
                out.println(new Gson().toJson(request));
                out.flush();
            } else {
                System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
            }
            System.out.println("Отправляемый запрос: " + new Gson().toJson(request));
            ClientSocket.getInstance().getOut().flush();

            showAlert("Успех", "Вклад успешно открыт!");
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректную сумму!");
        }
    }

    public void Exit_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
