import com.google.gson.Gson;
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
import main.idea.DTO.Session;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Objects;

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

    public void Login_Pressed(ActionEvent actionEvent) throws IOException {
        labelMessage.setVisible(false);

        if (textFieldLogin.getText().isEmpty() || passwordFieldPassword.getText().isEmpty()) {
            showAlert("Ошибка входа","Не все поля заполнены");
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
        System.out.println("Отправляемый запрос: " + new Gson().toJson(request));
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
                // Получение пользователя из ClientSocket
                User currentUser = ClientSocket.getInstance().getUser();

                if (currentUser != null) {
                    System.out.println("Установленный пользователь: " + currentUser.toString());
                    Session.setClientId(currentUser.getId());
                } else {
                    System.out.println("Пользователь не был установлен в ClientSocket.");
                }

                // Получение роли пользователя
                assert currentUser != null;
                String LoginUserRole = currentUser.getRole();
                System.out.println("User Role: " + LoginUserRole);

                if (LoginUserRole != null) {
                    // Проверка и переход на соответствующий экран в зависимости от роли пользователя
                    switch (LoginUserRole) {
                        case "CLIENT":
                            loadPage("First_page_client.fxml");
                            break;
                        case "ADMIN":
                            loadPage("First_page_admin.fxml");
                            break;
                        case "EMPLOYEE":
                            loadPage("First_page_employee.fxml");
                            break;
                        default:
                            break;
                    }
                } else {
                    showAlert("Ошибка входа","Пользователь не определён.");
                    labelMessage.setText("Пользователь не определён.");
                    labelMessage.setVisible(true);
                }
            } else {
                showAlert("Ошибка входа","Пользователя с такими данными не существует.");
                labelMessage.setText("Ошибка при входе.");
                labelMessage.setVisible(true);
            }
        } else {
            showAlert("Ошибка входа","Пользователя с такими данными не существует.");
            labelMessage.setText("Пользователь с таким логином не существует.");
            labelMessage.setVisible(true);
        }
    }

    private void loadPage(String pagePath) throws IOException {
        Stage stage = (Stage) buttonLogin.getScene().getWindow();
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(pagePath)));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
        stage.show();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
