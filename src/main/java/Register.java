import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.Enums.Roles;
import main.Models.Entities.Client;
import main.Models.Entities.User;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;
import main.builder.ClientBuilder;
import main.builder.UserBuilder;
import main.handlers.DefaultResponseHandler;
import main.handlers.ErrorRegistrationHandler;
import main.handlers.SuccessfulRegistrationHandler;
import main.interfaces.ResponseHandler;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Map;

//Паттерн строитель и стратегия
public class Register {
    @FXML
    private TextField textFieldLogin;
    @FXML
    private TextField textFieldEmail;
    @FXML
    private Button buttonCreate;
    @FXML
    private Label labelMessage;
    @FXML
    private PasswordField passwordFieldPassword;
    @FXML
    private PasswordField passwordFieldRepeatPassword;
    @FXML
    private Button buttonLogin;
    @FXML
    private TextField textFieldSurname;
    @FXML
    private TextField textFieldName;
    @FXML
    private TextField textFieldPatron;
    @FXML
    private TextField textFieldIDNumber;
    @FXML
    private TextField textFieldDocN;
    @FXML
    private DatePicker textFieldDOB;
    @FXML
    private ComboBox<String> textFieldCiti;
    @FXML
    private ComboBox<String> textFieldTypeDoc;

    public TextField getTextFieldLogin() {
        return textFieldLogin;
    }

    public void setTextFieldLogin(TextField textFieldLogin) {
        this.textFieldLogin = textFieldLogin;
    }

    public TextField getTextFieldEmail() {
        return textFieldEmail;
    }

    public void setTextFieldEmail(TextField textFieldEmail) {
        this.textFieldEmail = textFieldEmail;
    }

    public Button getButtonCreate() {
        return buttonCreate;
    }

    public void setButtonCreate(Button buttonCreate) {
        this.buttonCreate = buttonCreate;
    }

    public Label getLabelMessage() {
        return labelMessage;
    }

    public void setLabelMessage(Label labelMessage) {
        this.labelMessage = labelMessage;
    }

    public PasswordField getPasswordFieldPassword() {
        return passwordFieldPassword;
    }

    public void setPasswordFieldPassword(PasswordField passwordFieldPassword) {
        this.passwordFieldPassword = passwordFieldPassword;
    }

    public PasswordField getPasswordFieldRepeatPassword() {
        return passwordFieldRepeatPassword;
    }

    public void setPasswordFieldRepeatPassword(PasswordField passwordFieldRepeatPassword) {
        this.passwordFieldRepeatPassword = passwordFieldRepeatPassword;
    }

    public Button getButtonLogin() {
        return buttonLogin;
    }

    public void setButtonLogin(Button buttonLogin) {
        this.buttonLogin = buttonLogin;
    }

    public TextField getTextFieldSurname() {
        return textFieldSurname;
    }

    public void setTextFieldSurname(TextField textFieldSurname) {
        this.textFieldSurname = textFieldSurname;
    }

    public TextField getTextFieldName() {
        return textFieldName;
    }

    public void setTextFieldName(TextField textFieldName) {
        this.textFieldName = textFieldName;
    }

    public TextField getTextFieldPatron() {
        return textFieldPatron;
    }

    public void setTextFieldPatron(TextField textFieldPatron) {
        this.textFieldPatron = textFieldPatron;
    }

    public TextField getTextFieldIDNumber() {
        return textFieldIDNumber;
    }

    public void setTextFieldIDNumber(TextField textFieldIDNumber) {
        this.textFieldIDNumber = textFieldIDNumber;
    }

    public TextField getTextFieldDocN() {
        return textFieldDocN;
    }

    public void setTextFieldDocN(TextField textFieldDocN) {
        this.textFieldDocN = textFieldDocN;
    }

    public DatePicker getTextFieldDOB() {
        return textFieldDOB;
    }

    public void setTextFieldDOB(DatePicker textFieldDOB) {
        this.textFieldDOB = textFieldDOB;
    }

    public ComboBox<String> getTextFieldCiti() {
        return textFieldCiti;
    }

    public void setTextFieldCiti(ComboBox<String> textFieldCiti) {
        this.textFieldCiti = textFieldCiti;
    }

    public ComboBox<String> getTextFieldTypeDoc() {
        return textFieldTypeDoc;
    }

    public void setTextFieldTypeDoc(ComboBox<String> textFieldTypeDoc) {
        this.textFieldTypeDoc = textFieldTypeDoc;
    }

    Map<ResponseStatus, ResponseHandler> handlers = Map.of(
            ResponseStatus.OK, new SuccessfulRegistrationHandler(),
            ResponseStatus.ERROR, new ErrorRegistrationHandler()
    );

    public void initialize() {

        // Заполнение ComboBox для гражданства

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
    }

    public void Register_Pressed(ActionEvent actionEvent) throws IOException {

        labelMessage.setVisible(false);

        if (!validateClientFields()) {
            return;
        }

        String password = passwordFieldPassword.getText();
        String repeatPassword = passwordFieldRepeatPassword.getText();

        if (!password.equals(repeatPassword)) {
            labelMessage.setText("Пароли не совпадают");
            labelMessage.setVisible(true);
            return;
        }

        //Использование паттерна строитель
        Client client = new ClientBuilder()
                .withEmail(textFieldEmail.getText())
                .withName(textFieldName.getText())
                .withSurname(textFieldSurname.getText())
                .withPatronymic(textFieldPatron.getText())
                .withDOB(textFieldDOB.getValue() != null ? textFieldDOB.getValue().toString() : null)
                .withCitizenship(textFieldCiti.getValue())
                .withDocumentType(textFieldTypeDoc.getValue())
                .withIdNumber(textFieldIDNumber.getText())
                .withDocumentNumber(textFieldDocN.getText())
                .build();

        User user = new UserBuilder()
                .withLogin(textFieldLogin.getText())
                .withRole(Roles.CLIENT)
                .withPassword(passwordFieldPassword.getText())
                .withClient(client)
                .build();

        Request request = new Request();
        request.setRequestMessage(new Gson().toJson(user));
        request.setRequestType(RequestType.REGISTER);

        PrintWriter out = ClientSocket.getInstance().getOut();
        if (out != null) {
            out.println(new Gson().toJson(request));
            out.flush();
        } else {
            System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
        }
        ClientSocket.getInstance().getOut().flush();

        String answer = ClientSocket.getInstance().getInStream().readLine();
        System.out.println(answer);
        Response response = new Gson().fromJson(answer, Response.class);
        System.out.println(response);

        if (response != null) {
        if (response.getResponseStatus() == ResponseStatus.OK) {
            labelMessage.setVisible(false);
            ClientSocket.getInstance().setUser(new Gson().fromJson(response.getResponseUser(), User.class));ClientSocket.getInstance().setUser(new Gson().fromJson(response.getResponseUser(), User.class));

            ResponseHandler handler = handlers.getOrDefault(response.getResponseStatus(), new SuccessfulRegistrationHandler());
            handler.handle(response);

            Stage stage = (Stage) buttonCreate.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("First_page_client.fxml"));
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.show();
        }
        else if (response.getResponseStatus() == null) {
            labelMessage.setText("Ошибка внесения в БД.");
        }
        else  {
            labelMessage.setText("Пользователь с таким логином уже существует.");
            labelMessage.setVisible(true);
            ResponseHandler handler = handlers.getOrDefault(response.getResponseStatus(), new DefaultResponseHandler());
            handler.handle(response);

        }
        } else {

            System.err.println("Response is null. Registration failed.");
        }
    }

    public void Login_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonLogin.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public boolean validateClientFields() {

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

        if (textFieldIDNumber.getText().length() != 9 || textFieldDocN.getText().length() != 9) {
            labelMessage.setText("Идентификационный номер и номер документа должны содержать ровно 9 символов.");
            labelMessage.setVisible(true);
            return false;
        }

        return true;
    }
}

