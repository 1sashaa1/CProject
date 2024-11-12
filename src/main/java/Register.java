import com.google.gson.Gson;
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

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;

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

        // Проверка на пустые поля
        if (textFieldLogin.getText().isEmpty() || textFieldEmail.getText().isEmpty() ||
                passwordFieldPassword.getText().isEmpty() || passwordFieldRepeatPassword.getText().isEmpty()) {
            labelMessage.setText("Не все поля заполнены");
            labelMessage.setVisible(true);
            return;
        }

        String password = passwordFieldPassword.getText();
        String repeatPassword = passwordFieldRepeatPassword.getText();

        if (!password.equals(repeatPassword)) {
            labelMessage.setText("Пароли не совпадают");
            labelMessage.setVisible(true);
            return;
        }

        // Создание пользователя
        User user = new User();
        user.setLogin(textFieldLogin.getText());
        Roles userRole = Roles.CLIENT;
        user.setRole(userRole.name());
        user.setPassword(password);

        // Создание клиента
        Client client = new Client();
        client.setEmail(textFieldEmail.getText());
        client.setName(textFieldName.getText());
        client.setSurname(textFieldSurname.getText());
        client.setPatronymic(textFieldPatron.getText());

        // Преобразование даты
        LocalDate dob = textFieldDOB.getValue();
        if (dob != null) {
            client.setDob(dob.toString());  // Преобразуем дату в строку
        }

        client.setCitizenship(textFieldCiti.getValue());
        client.setDocumentType(textFieldTypeDoc.getValue());
        client.setIdNumber(textFieldIDNumber.getText());
        client.setDocumentNumber(textFieldDocN.getText());

        user.setClient(client);

        // Создание и отправка запроса
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

        // Получение ответа от сервера
        String answer = ClientSocket.getInstance().getInStream().readLine();
        System.out.println(answer);
        Response response = new Gson().fromJson(answer, Response.class);
        System.out.println(response);

        if (response != null) {
        if (response.getResponseStatus() == ResponseStatus.OK) {
            labelMessage.setVisible(false);
            ClientSocket.getInstance().setUser(new Gson().fromJson(response.getResponseUser(), User.class));ClientSocket.getInstance().setUser(new Gson().fromJson(response.getResponseUser(), User.class));


            //            // Получение пользователя из ClientSocket
//            User currentUser = ClientSocket.getInstance().getUser();
//
//// Проверка, был ли установлен пользователь
//            if (currentUser != null) {
//                System.out.println("Установленный пользователь: " + currentUser.toString());
//            } else {
//                System.out.println("Пользователь не был установлен в ClientSocket.");
//            }


            // Переход на другой экран (укажите корректный путь)
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
}

