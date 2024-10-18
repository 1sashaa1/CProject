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
import main.Models.Entities.PersonData;
import main.Models.Entities.User;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;

import java.io.IOException;

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

    public void Register_Pressed(ActionEvent actionEvent) throws IOException {
        labelMessage.setVisible(false);
        String password = passwordFieldPassword.getText();
        String repeatPassword = passwordFieldRepeatPassword.getText();


        User user = new User();
        user.setLogin(textFieldLogin.getText());
        //user.setRole(Roles.User);
        user.setPassword(password);

        PersonData personData = new PersonData();
        personData.setEmail(textFieldEmail.getText());

        user.setPersonData(personData);
        if (!password.equals(repeatPassword)) {
            labelMessage.setText("Пароли не совпадают");
            labelMessage.setVisible(true);
            return;
        }
        if (textFieldLogin.equals("") || textFieldEmail.equals("") || passwordFieldPassword.equals("") || passwordFieldRepeatPassword.equals("")){
            labelMessage.setText("Не все поля заполнены");
            labelMessage.setVisible(true);
            return;
        }

        Request request = new Request();

        request.setRequestMessage(new Gson().toJson(user));
        request.setRequestType(RequestType.REGISTER);

        ClientSocket.getInstance().getOut().println(new Gson().toJson(request));
        ClientSocket.getInstance().getOut().flush();

        String answer = ClientSocket.getInstance().getIn().readLine();//////////////
        Response response = new Gson().fromJson(answer, Response.class);
        if (response.getResponseStatus() == ResponseStatus.OK){
            labelMessage.setVisible(false);
            ClientSocket.getInstance().setUser(new Gson().fromJson(response.getResponseData(), User.class));
            //Stage stage = (Stage) buttonBack.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource(""));
            Scene newScene = new Scene(root);
            //stage.setScene(newScene);
        }
        else{
            labelMessage.setText("Пользователь с таким логином уже существует.");
            labelMessage.setVisible(true);
        }

    }

    public void Login_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonLogin.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);

    }
}
