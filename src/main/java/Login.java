import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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

public class Login {
    @FXML
    private TextField textFieldLogin;
    @FXML
    private Label labelMessage;
    @FXML
    private PasswordField passwordFieldPassword;
    @FXML
    private Button buttonRegistration;
    @FXML
    private Button buttonLogin;

    public void Register_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonRegistration.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Register.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    public void Login_Pressed(ActionEvent actionEvent) throws IOException
    {
        labelMessage.setVisible(false);

    // Проверка на пустые поля
        if (textFieldLogin.getText().isEmpty() || passwordFieldPassword.getText().isEmpty()) {
        labelMessage.setText("Не все поля заполнены");
        labelMessage.setVisible(true);
        return;
    }

    String password = passwordFieldPassword.getText();

    // Создание пользователя
    User user = new User();
    user.setLogin(textFieldLogin.getText());
    Roles userRole = Roles.CLIENT;
    user.setRole(userRole.name());
    user.setPassword(password);

    Request request = new Request();
        request.setRequestMessage(new Gson().toJson(user));
        request.setRequestType(RequestType.LOGIN);

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
            ClientSocket.getInstance().setUser(new Gson().fromJson(response.getResponseUser(), User.class));

            // Переход на другой экран (укажите корректный путь)
            Stage stage = (Stage) buttonLogin.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("First_page.fxml"));
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.show();
        }
        else if (response.getResponseStatus() == null) {
            labelMessage.setText("Ошибка при входе.");
        }
        else  {
            labelMessage.setText("Пользователь с таким логином не существует.");
            labelMessage.setVisible(true);
        }
    } else {

        System.err.println("Response is null. Login failed.");
    }
}
}
