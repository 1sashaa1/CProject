import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.Models.Entities.Client;
import main.Models.Entities.Notifications;
import main.Models.Entities.Question;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;
import main.idea.DTO.Session;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MyQuestion {
    public TextArea questionText;
    public Button sendQuestionButton;
    public ListView questionsAndAnswersList;
    public Button buttonExit;

    @FXML
    public void initialize() throws IOException {
        loadQuestions();
    }

    public void loadQuestions() throws IOException {
        questionsAndAnswersList.getItems().clear(); // Очищаем список перед загрузкой

        Request request = new Request();
        request.setRequestType(RequestType.GETQUESTIONS);

        int clientId = Session.getClientId();
        System.out.println(clientId);
        JsonObject requestMessage = new JsonObject();
        requestMessage.addProperty("clientId", clientId);

        request.setRequestMessage(new Gson().toJson(requestMessage));

        PrintWriter out = ClientSocket.getInstance().getOut();
        if (out != null) {
            out.println(new Gson().toJson(request));
            out.flush();
        } else {
            System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
            return;
        }

        // Получаем ответ от сервера
        String answer = ClientSocket.getInstance().getInStream().readLine();
        System.out.println(answer);
        Response response = new Gson().fromJson(answer, Response.class);
        if (response != null && response.getResponseStatus() == ResponseStatus.OK) {
            String json = response.getResponseUser();
            if (json.startsWith("[") && json.endsWith("]")) {
                // Ответ представляет собой список объектов JSON
                Type listType = new TypeToken<List<Question>>() {}.getType();
                List<Question> questions = new Gson().fromJson(json, listType);

                // Отображаем вопросы в ListView
                for (Question question : questions) {
                    String item = "Вопрос: " + question.getText() +
                            (question.getAnswer() != null ? "\nОтвет: " + question.getAnswer() : "\nОтвет: (нет ответа)");
                    questionsAndAnswersList.getItems().add(item);
                }
            } else if (json.startsWith("{") && json.endsWith("}")) {
                // Ответ представляет собой один объект JSON
                Question question = new Gson().fromJson(json, Question.class);
                String item = "Вопрос: " + question.getText() +
                        (question.getAnswer() != null ? "\nОтвет: " + question.getAnswer() : "\nОтвет: (нет ответа)");
                questionsAndAnswersList.getItems().add(item);}
        } else {
            System.err.println("Ошибка загрузки вопросов: " + (response != null ? response.getResponseData() : "Нет ответа от сервера"));
        }
    }

    public void sendQuestion(ActionEvent actionEvent) throws IOException {
        String message = questionText.getText().trim();
        if (message.isEmpty()) {
            System.out.println("Сообщение не может быть пустым");
            return;
        }
        sendNotificationToEmployee(message);
    }

    public void sendNotificationToEmployee(String message) throws IOException {
        Question question = new Question();
        question.setText(message);

        Client client = new Client();
        client.setClientId(Session.getClientId());
        question.setClient(client);

        Request request = new Request();
        request.setRequestType(RequestType.ASKQUESTION);
        request.setRequestMessage(new Gson().toJson(question));

        PrintWriter out = ClientSocket.getInstance().getOut();
        if (out != null) {
            out.println(new Gson().toJson(request));
            out.flush();
        } else {
            System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
        }
        loadQuestions();
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

