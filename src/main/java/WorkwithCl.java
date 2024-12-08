import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.Enums.Roles;
import main.Models.Entities.Client;
import main.Models.Entities.User;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

public class WorkwithCl {
    public Button backButton;
    public Button buttonExit;
    public Button deleteCl;
    public Button createCl;
    public Button changeCl;
    public HBox comparisonBox;
    public TableColumn IdColumn;
    public TableColumn NameColumn;
    public TableColumn SurnameColumn;
    public TableColumn PatronymicColumn;
    public TableColumn DbColumn;
    public TableColumn CitizenshipColumn;
    public TableColumn TypeDocColumn;
    public TableColumn IdNumberColumn;
    public TableColumn NumberColumn;
    public PasswordField passwordFieldPassword;
    public PasswordField passwordFieldRepeatPassword;
    public Label labelMessage;
    public TextField textFieldSurname;
    public TextField textFieldName;
    public TextField textFieldPatron;
    public DatePicker textFieldDOB;
    public TextField textFieldIDNumber;
    public TextField textFieldDocN;
    public ComboBox textFieldCiti;
    public ComboBox textFieldTypeDoc;
    public TextField textFieldLogin;
    public TextField textFieldEmail;
    public VBox createForm;
    public Button saveClient;
    public TableColumn EmailColumn;
    private ObservableList<Client> clientsdepositsList = FXCollections.observableArrayList();

    @FXML
    TableView clientTable;

    public void initialize(){
        ObservableList<String> citizenshipOptions = FXCollections.observableArrayList(
                "Беларусь",
                "Россия",
                "Польша",
                "Украина",
                "Литва",
                "Латвия"
        );

        textFieldCiti.setItems(citizenshipOptions);

        ObservableList<String> docTypeOptions = FXCollections.observableArrayList(
                "Passport",
                "ID Card",
                "Driver's License"
        );
        textFieldTypeDoc.setItems(docTypeOptions);

        clientTable.setItems(clientsdepositsList);
        IdColumn.setCellValueFactory(new PropertyValueFactory<>("clientId"));
        NameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        SurnameColumn.setCellValueFactory(new PropertyValueFactory<>("surname"));
        PatronymicColumn.setCellValueFactory(new PropertyValueFactory<>("patronymic"));
        DbColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
        CitizenshipColumn.setCellValueFactory(new PropertyValueFactory<>("citizenship"));
        TypeDocColumn.setCellValueFactory(new PropertyValueFactory<>("documentType"));
        IdNumberColumn.setCellValueFactory(new PropertyValueFactory<>("idNumber"));
        NumberColumn.setCellValueFactory(new PropertyValueFactory<>("documentNumber"));
        EmailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        try {
            loadDataFromDatabase();
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке данных из базы данных: " + e.getMessage());
        }
    }

    private void loadDataFromDatabase() throws IOException {
        Request request = new Request();
        request.setRequestType(RequestType.GETTRUECLIENTS);

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

                            Client[] clientsArray = new Gson().fromJson(jsonResponse, Client[].class);
                            Platform.runLater(() -> {
                                clientsdepositsList.clear();
                                clientsdepositsList.addAll(clientsArray);
                            });
                        } else {

                            Client singleClient = new Gson().fromJson(jsonResponse, Client.class);
                            Platform.runLater(() -> {
                                clientsdepositsList.clear();
                                clientsdepositsList.add(singleClient);
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


    public void openMainPage(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("First_page_admin.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void Exit_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    private boolean validateClientFields() {

        if (textFieldLogin.getText().isEmpty() || textFieldEmail.getText().isEmpty() ||
                passwordFieldPassword.getText().isEmpty() || passwordFieldRepeatPassword.getText().isEmpty() ||
                textFieldName.getText().isEmpty() || textFieldSurname.getText().isEmpty() ||
                textFieldDOB.getValue() == null || textFieldCiti.getValue() == null ||
                textFieldTypeDoc.getValue() == null || textFieldIDNumber.getText().isEmpty() ||
                textFieldDocN.getText().isEmpty()) {
            labelMessage.setText("Заполните все поля.");
            labelMessage.setVisible(true);
            return false;
        }

        // Проверка email
        if (!textFieldEmail.getText().matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$")) {
            labelMessage.setText("Введите корректный email.");
            labelMessage.setVisible(true);
            return false;
        }

        // Проверка логина и пароля
        String login = textFieldLogin.getText();
        String password = passwordFieldPassword.getText();
        if (login.length() < 4 || login.length() > 20 || password.length() < 4 || password.length() > 20) {
            labelMessage.setText("Логин и пароль должны быть от 4 до 20 символов.");
            labelMessage.setVisible(true);
            return false;
        }

        if (!textFieldName.getText().matches("^[A-Za-zА-Яа-яЁё]{1,24}$") ||
                !textFieldSurname.getText().matches("^[A-Za-zА-Яа-яЁё]{1,24}$") ||
                !textFieldPatron.getText().matches("^[A-Za-zА-Яа-яЁё]{1,24}$")) {
            labelMessage.setText("Имя, фамилия и отчество должны быть от 1 до 24 символов и без пробелов/цифр.");
            labelMessage.setVisible(true);
            return false;
        }

        LocalDate dob = textFieldDOB.getValue();
        LocalDate today = LocalDate.now();
        if (dob.isAfter(today.minusYears(18)) || dob.isBefore(today.minusYears(150))) {
            labelMessage.setText("Дата рождения должна быть между -150 и -18 годами от текущей даты.");
            labelMessage.setVisible(true);
            return false;
        }

        // Проверка идентификационного номера и номера документа
        if (textFieldIDNumber.getText().length() != 9 || textFieldDocN.getText().length() != 9) {
            labelMessage.setText("Идентификационный номер и номер документа должны содержать ровно 9 символов.");
            labelMessage.setVisible(true);
            return false;
        }

        // Все проверки пройдены
        return true;
    }


    // Очистка текстовых полей
    private void clearClientFields() {
        textFieldLogin.clear();
        textFieldEmail.clear();
        passwordFieldPassword.clear();
        passwordFieldRepeatPassword.clear();
        textFieldName.clear();
        textFieldSurname.clear();
        textFieldPatron.clear();
        textFieldDOB.setValue(null);
        textFieldCiti.setValue(null);
        textFieldTypeDoc.setValue(null);
        textFieldIDNumber.clear();
        textFieldDocN.clear();
    }

    // Показ алерта подтверждения действия
    private boolean showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);
        return result == ButtonType.OK;
    }

    // Метод удаления клиента с подтверждением
    public void deleteCl(ActionEvent actionEvent) {
        Client selectedClient = (Client) clientTable.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            labelMessage.setText("Выберите клиента для удаления.");
            labelMessage.setVisible(true);
            return;
        }

        if (!showConfirmationAlert("Подтверждение удаления", "Вы уверены, что хотите удалить клиента?")) {
            return;
        }

        Request request = new Request();
        request.setRequestType(RequestType.DELETECLIENT);
        request.setRequestMessage(new Gson().toJson(selectedClient));

        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            if (out != null) {
                out.println(new Gson().toJson(request));
                out.flush();
            }

            String responseMessage = ClientSocket.getInstance().getInStream().readLine();
            Response response = new Gson().fromJson(responseMessage, Response.class);

            if (response != null && response.getResponseStatus() == ResponseStatus.OK) {
                labelMessage.setText("Клиент успешно удалён.");
                labelMessage.setVisible(true);
                clientTable.getItems().remove(selectedClient);
            } else {
                labelMessage.setText("Ошибка удаления клиента.");
                labelMessage.setVisible(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            labelMessage.setText("Ошибка соединения.");
            labelMessage.setVisible(true);
        }
    }

    // Сохранение нового клиента с проверками
    public void saveClient(ActionEvent actionEvent) throws IOException {
        labelMessage.setVisible(false);

        if (!validateClientFields()) {
            return;
        }

        if (!passwordFieldPassword.getText().equals(passwordFieldRepeatPassword.getText())) {
            labelMessage.setText("Пароли не совпадают");
            labelMessage.setVisible(true);
            return;
        }

        if (!showConfirmationAlert("Подтверждение сохранения", "Вы уверены, что хотите сохранить нового клиента?")) {
            return;
        }

        User user = new User();
        user.setLogin(textFieldLogin.getText());
        user.setRole(Roles.CLIENT.name());
        user.setPassword(passwordFieldPassword.getText());

        Client client = new Client();
        client.setEmail(textFieldEmail.getText());
        client.setName(textFieldName.getText());
        client.setSurname(textFieldSurname.getText());
        client.setPatronymic(textFieldPatron.getText());
        client.setDob(textFieldDOB.getValue().toString());
        client.setCitizenship((String) textFieldCiti.getValue());
        client.setDocumentType((String) textFieldTypeDoc.getValue());
        client.setIdNumber(textFieldIDNumber.getText());
        client.setDocumentNumber(textFieldDocN.getText());

        user.setClient(client);

        Request request = new Request();
        request.setRequestMessage(new Gson().toJson(user));
        request.setRequestType(RequestType.REGISTER);

        PrintWriter out = ClientSocket.getInstance().getOut();
        if (out != null) {
            out.println(new Gson().toJson(request));
            out.flush();
        }

        String answer = ClientSocket.getInstance().getInStream().readLine();
        Response response = new Gson().fromJson(answer, Response.class);

        if (response != null && response.getResponseStatus() == ResponseStatus.OK) {
            labelMessage.setText("Клиент успешно сохранён.");
            labelMessage.setVisible(true);
            clearClientFields();
            loadDataFromDatabase();
        } else {
            labelMessage.setText("Ошибка сохранения клиента.");
            labelMessage.setVisible(true);
        }
    }

    // Изменение клиента с подтверждением
    private void saveUpdatedClient(Client client) {
        labelMessage.setVisible(false);

        if (!validateClientFields()) {
            return;
        }

        if (!showConfirmationAlert("Подтверждение изменения", "Вы уверены, что хотите сохранить изменения?")) {
            return;
        }

        client.setEmail(textFieldEmail.getText());
        client.setName(textFieldName.getText());
        client.setSurname(textFieldSurname.getText());
        client.setPatronymic(textFieldPatron.getText());
        client.setDob(textFieldDOB.getValue() != null ? textFieldDOB.getValue().toString() : null);
        client.setCitizenship((String) textFieldCiti.getValue());
        client.setDocumentType((String) textFieldTypeDoc.getValue());
        client.setIdNumber(textFieldIDNumber.getText());
        client.setDocumentNumber(textFieldDocN.getText());

        Request request = new Request();
        request.setRequestType(RequestType.UPDATECLIENT);
        request.setRequestMessage(new Gson().toJson(client));

        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            if (out != null) {
                out.println(new Gson().toJson(request));
                out.flush();
            }

            String responseMessage = ClientSocket.getInstance().getInStream().readLine();
            Response response = new Gson().fromJson(responseMessage, Response.class);

            if (response != null && response.getResponseStatus() == ResponseStatus.OK) {
                labelMessage.setText("Изменения сохранены.");
                labelMessage.setVisible(true);
                loadDataFromDatabase();
                clearClientFields();
            } else {
                labelMessage.setText("Ошибка сохранения изменений.");
                labelMessage.setVisible(true);
            }
        } catch (IOException e) {
            e.printStackTrace();
            labelMessage.setText("Ошибка соединения.");
            labelMessage.setVisible(true);
        }

        createForm.setVisible(false);
        createForm.setManaged(false);
    }


    public void changeCl(ActionEvent actionEvent) {
        Client selectedClient = (Client) clientTable.getSelectionModel().getSelectedItem();

        if (selectedClient == null) {
            labelMessage.setText("Выберите клиента для изменения.");
            labelMessage.setVisible(true);
            return;
        }

        textFieldName.setText(selectedClient.getName());
        textFieldSurname.setText(selectedClient.getSurname());
        textFieldPatron.setText(selectedClient.getPatronymic());
        textFieldDOB.setValue(LocalDate.parse(selectedClient.getDob()));
        textFieldCiti.setValue(selectedClient.getCitizenship());
        textFieldTypeDoc.setValue(selectedClient.getDocumentType());
        textFieldIDNumber.setText(selectedClient.getIdNumber());
        textFieldDocN.setText(selectedClient.getDocumentNumber());
        textFieldEmail.setText(selectedClient.getEmail());
        textFieldLogin.setText(""); // Логин не меняем для безопасности
        createForm.setVisible(true);
        createForm.setManaged(true);

        saveClient.setOnAction(event -> saveUpdatedClient(selectedClient));

    }

    @FXML
    private void showCreateForm() {
        clearClientFields();
        createForm.setVisible(true);
        createForm.setManaged(true);

    }
}
