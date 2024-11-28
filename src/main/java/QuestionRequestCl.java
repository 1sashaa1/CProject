import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import com.google.gson.JsonParser;
import com.google.gson.Gson;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;


import java.io.IOException;
import java.util.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.Enums.RequestType;
import main.Enums.ResponseStatus;
import main.Models.Entities.Question;
import main.Models.TCP.Request;
import main.Models.TCP.Response;
import main.Utility.ClientSocket;
import main.idea.DTO.QuestionDTO;
import main.idea.DTO.Session;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;

public class QuestionRequestCl {
    @FXML
    public ListView<QuestionDTO> clientQuestionsList; // Изменено на ListView<QuestionDTO>
    @FXML
    public TextArea responseText;
    @FXML
    public Button sendResponseButton;
    @FXML
    public ListView<String> openDepositRequestsList;
    @FXML
    public Button openDepositButton;
    @FXML
    public ListView<String> closeDepositRequestsList;
    @FXML
    public Button closeDepositButton;
    @FXML
    public Button buttonExit;

    private List<Request> openDepositRequests;
    private List<Request> closeDepositRequests;

    @FXML
    public void initialize() {
        // Настройка отображения объектов в ListView
        clientQuestionsList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(QuestionDTO question, boolean empty) {
                super.updateItem(question, empty);
                if (empty || question == null) {
                    setText(null);
                } else {
                    setText(question.getText()); // Отображаем текст вопроса
                }
            }
        });
        loadQuestions(); // Загружаем вопросы
    }

    private void loadQuestions() {
        clientQuestionsList.getItems().clear();
        Request request = new Request();
        request.setRequestType(RequestType.GETALLQUESTIONS);
        request.setRequestMessage(new Gson().toJson(createClientIdJson()));

        sendRequestToServer(request, response -> {
            if (response.getResponseStatus() == ResponseStatus.OK) {
                List<QuestionDTO> questions = parseListResponse(response.getResponseUser(), QuestionDTO.class);
                clientQuestionsList.getItems().addAll(questions); // Добавляем объекты в ListView
            } else {
                showError("Ошибка загрузки вопросов", response.getResponseData());
            }
        });
    }

    public void sendResponse(ActionEvent actionEvent) {
        // Получаем выделенный вопрос из ListView
        QuestionDTO selectedQuestion = clientQuestionsList.getSelectionModel().getSelectedItem();
        if (selectedQuestion == null) {
            showAlert("Ошибка", "Выберите вопрос для ответа!");
            return;
        }

        // Проверяем, что текст ответа введён
        String responseMessage = responseText.getText().trim();
        if (responseMessage.isEmpty()) {
            showAlert("Ошибка", "Введите текст ответа!");
            return;
        }

        // Получаем ID вопроса
        int questionId = selectedQuestion.getQuestionid(); // Предполагается, что у QuestionDTO есть метод getId()
        System.out.println("ID вопроса: " + questionId);

        // Создаём объект для отправки
        JsonObject responseJson = new JsonObject();
        responseJson.addProperty("questionId", questionId);
        responseJson.addProperty("responseMessage", responseMessage);

        // Создаём объект запроса
        Request request = new Request();
        request.setRequestType(RequestType.UNSWERQUESTION);
        request.setRequestMessage(responseJson.toString());

        // Отправляем запрос на сервер
        PrintWriter out = ClientSocket.getInstance().getOut();
        if (out != null) {
            out.println(new Gson().toJson(request));
            out.flush();
        } else {
            System.err.println("Ошибка: PrintWriter равен null. Пожалуйста, убедитесь, что соединение установлено и ClientSocket правильно инициализирован.");
            return;
        }

        System.out.println("Отправляемый запрос: " + new Gson().toJson(request));
        showAlert("Успех", "Ответ успешно отправлен!");

        // Очищаем текст ответа и перезагружаем вопросы
        responseText.clear();
        loadQuestions();
    }

    public void Exit_Pressed(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }

    private JsonObject createClientIdJson() {
        JsonObject json = new JsonObject();
        json.addProperty("clientId", Session.getClientId());
        return json;
    }

    private <T> List<T> parseListResponse(String json, Class<T> clazz) {
        Gson gson = new Gson();
        JsonElement jsonElement = JsonParser.parseString(json);

        if (jsonElement.isJsonArray()) {
            // Если это массив
            Type listType = TypeToken.getParameterized(List.class, clazz).getType();
            return gson.fromJson(json, listType);
        } else if (jsonElement.isJsonObject()) {
            // Если это объект, преобразуем в список с одним элементом
            T singleObject = gson.fromJson(json, clazz);
            return Collections.singletonList(singleObject);
        } else {
            throw new JsonSyntaxException("Неподдерживаемый формат JSON: " + json);
        }
    }

    private void sendRequestToServer(Request request, ResponseHandler handler) {
        try {
            PrintWriter out = ClientSocket.getInstance().getOut();
            if (out != null) {
                out.println(new Gson().toJson(request));
                out.flush();

                String responseString = ClientSocket.getInstance().getInStream().readLine();
                Response response = new Gson().fromJson(responseString, Response.class);
                handler.handle(response);
            } else {
                showError("Ошибка соединения", "Соединение с сервером не установлено.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка отправки запроса", "Не удалось отправить запрос на сервер.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void openDepositPage(ActionEvent actionEvent) {
    }

    public void closeDepositPage(ActionEvent actionEvent) {
    }

    public void openMainPage(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) buttonExit.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("First_page_employee.fxml"));
        Scene newScene = new Scene(root);
        stage.setScene(newScene);
    }


    @FunctionalInterface
    interface ResponseHandler {
        void handle(Response response);
    }
}
