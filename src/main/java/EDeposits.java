import com.google.gson.Gson;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.Models.Entities.Deposit;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.application.Platform;


public class EDeposits {
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
    public ComboBox depositTypeComboBox;
    @FXML
    public TextField nameField;
    @FXML
    public TextField interestRateField;
    @FXML
    public TextField minAmountField;
    @FXML
    public TextField termField;
    @FXML
    public CheckBox isReplenishment;
    @FXML
    public CheckBox isTransactions;
    @FXML
    public CheckBox isProlongation;
    @FXML
    public Button buttonAdd;
    @FXML
    public Button buttonCancel;
    @FXML
    public Button deleteDeposit;
    @FXML
    public Button changeDeposit;

    @FXML
    private Button buttonExit;

    private ObservableList<Deposit> depositsList = FXCollections.observableArrayList();

    public void initialize() {
        depositTable.setItems(depositsList);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("nameDeposit"));
        TypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        RateColumn.setCellValueFactory(new PropertyValueFactory<>("interestRate"));
        MinCountColumn.setCellValueFactory(new PropertyValueFactory<>("minAmount"));
        TermColumn.setCellValueFactory(new PropertyValueFactory<>("term"));
        AddColumn.setCellValueFactory(new PropertyValueFactory<>("replenishment"));
        TransactionColumn.setCellValueFactory(new PropertyValueFactory<>("transactions"));
        ProlongationColumn.setCellValueFactory(new PropertyValueFactory<>("prolongation"));

        try {
            loadDataFromDatabase();
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке данных из базы данных: " + e.getMessage());
        }
    }

    public void Exit_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void addDeposit(ActionEvent actionEvent) throws IOException {
        if (validateFields()) {
            Deposit deposit = new Deposit();
            deposit.setNameDeposit(nameField.getText());
            deposit.setType((String) depositTypeComboBox.getValue());
            deposit.setInterestRate(Double.parseDouble(interestRateField.getText()));
            deposit.setMinAmount(Double.parseDouble(minAmountField.getText()));
            deposit.setTerm(Integer.parseInt(termField.getText()));
            deposit.setReplenishment(isReplenishment.isSelected());
            deposit.setTransactions(isTransactions.isSelected());
            deposit.setProlongation(isProlongation.isSelected());

            saveToDatabase(deposit);
            depositTable.getItems().add(deposit);
            clearFields();
            initialize();

        } else {
            System.out.println("Ошибка: Не все поля заполнены корректно");
        }
    }

    private void saveToDatabase(Deposit deposit) throws IOException {
        Request request = new Request();
        request.setRequestMessage(new Gson().toJson(deposit));
        request.setRequestType(RequestType.ADDDEPOSIT);

        PrintWriter out = ClientSocket.getInstance().getOut();
        if (out != null) {
            out.println(new Gson().toJson(request));
            out.flush();
        } else {
            System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
        }
        ClientSocket.getInstance().getOut().flush();
        loadDataFromDatabase();
    }


    private boolean validateFields() {
        // Проверка на заполненность полей
        if (nameField.getText().isEmpty() || depositTypeComboBox.getValue() == null ||
                interestRateField.getText().isEmpty() || minAmountField.getText().isEmpty() ||
                termField.getText().isEmpty()) {
            showAlert("Ошибка", "Все поля должны быть заполнены.");
            return false;
        }

        // Проверка длины названия вклада
        String depositName = nameField.getText();
        if (depositName.length() < 2 || depositName.length() > 30) {
            showAlert("Ошибка", "Название вклада должно быть не менее 2 и не более 30 символов.");
            return false;
        }

        try {
            // Проверка процентной ставки
            double interestRate = Double.parseDouble(interestRateField.getText());
            if (interestRate < 2 || interestRate > 20) {
                showAlert("Ошибка", "Процентная ставка должна быть от 2% до 20%.");
                return false;
            }

            // Проверка минимального взноса
            double minAmount = Double.parseDouble(minAmountField.getText());
            if (minAmount < 100 || minAmount > 30000) {
                showAlert("Ошибка", "Границы первоначального взноса от 100 до 30,000.");
                return false;
            }

            // Проверка срока хранения
            int term = Integer.parseInt(termField.getText());
            if (term < 1 || term > 36) {
                showAlert("Ошибка", "Срок хранения должен быть от 1 до 36 месяцев.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Ошибка", "Введите корректные числовые значения для ставок, минимальной суммы и срока.");
            return false;
        }

        return true;
    }

    // Вспомогательный метод для отображения сообщений об ошибке
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void cancel(ActionEvent actionEvent) {
        clearFields();
    }

    private void clearFields() {
        nameField.clear();
        interestRateField.clear();
        minAmountField.clear();
        termField.clear();
        depositTypeComboBox.getSelectionModel().clearSelection();
        isReplenishment.setSelected(false);
        isTransactions.setSelected(false);
        isProlongation.setSelected(false);
    }

    public void deleteD(ActionEvent actionEvent) throws IOException {
        Deposit selectedDeposit = (Deposit) depositTable.getSelectionModel().getSelectedItem();

        if (selectedDeposit != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Подтверждение");
            confirmationAlert.setHeaderText("Подтвердите действие удаления депозита");
            confirmationAlert.setContentText("Вы уверены, что хотите удалить выбранный депозит?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Request request = new Request();
                request.setRequestType(RequestType.DELETEDEPOSIT);
                request.setRequestMessage(new Gson().toJson(selectedDeposit.getId()));

                PrintWriter out = ClientSocket.getInstance().getOut();
                if (out != null) {
                    out.println(new Gson().toJson(request));
                    out.flush();
                } else {
                    System.err.println("Ошибка: соединение не установлено.");
                }
                loadDataFromDatabase();
                depositsList.remove(selectedDeposit);
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, выберите депозит для удаления.");
            alert.showAndWait();
        }
    }

    public void changeD(ActionEvent actionEvent) throws IOException {
        Deposit selectedDeposit = (Deposit) depositTable.getSelectionModel().getSelectedItem();

        if (selectedDeposit != null) {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Подтверждение");
            confirmationAlert.setHeaderText("Подтвердите действие изменения депозита");
            confirmationAlert.setContentText("Вы уверены, что хотите изменить выбранный депозит?");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {

                selectedDeposit.setNameDeposit(nameField.getText());
                selectedDeposit.setType((String) depositTypeComboBox.getValue());
                selectedDeposit.setInterestRate(Double.parseDouble(interestRateField.getText()));
                selectedDeposit.setMinAmount(Double.parseDouble(minAmountField.getText()));
                selectedDeposit.setTerm(Integer.parseInt(termField.getText()));
                selectedDeposit.setReplenishment(isReplenishment.isSelected());
                selectedDeposit.setTransactions(isTransactions.isSelected());
                selectedDeposit.setProlongation(isProlongation.isSelected());

                Request request = new Request();
                request.setRequestType(RequestType.UPDATEDEPOSIT);
                request.setRequestMessage(new Gson().toJson(selectedDeposit));

                PrintWriter out = ClientSocket.getInstance().getOut();
                if (out != null) {
                    out.println(new Gson().toJson(request));
                    out.flush();
                } else {
                    System.err.println("Ошибка: соединение не установлено.");
                }
                loadDataFromDatabase();
                depositTable.refresh();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Пожалуйста, выберите депозит для изменения.");

            alert.showAndWait();
        }
    }
    public void openMainPage(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("First_page_employee.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void openDeposit(ActionEvent actionEvent) {
    }

    public void closeDeposit(ActionEvent actionEvent) {
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
}
