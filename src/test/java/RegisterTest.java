import javafx.application.Platform;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class RegisterTest {

    private Register register;

    @BeforeAll
    static void setUpJavaFX() {
        if (!Platform.isFxApplicationThread()) {
            CompletableFuture.runAsync(() -> Platform.startup(() -> {}));
        }
    }

    @BeforeEach
    void setUp() {
        register = new Register();

        register.setTextFieldLogin(new TextField());
        register.setTextFieldEmail(new TextField());
        register.setPasswordFieldPassword(new PasswordField());
        register.setPasswordFieldRepeatPassword(new PasswordField());
        register.setTextFieldName(new TextField());
        register.setTextFieldSurname(new TextField());
        register.setTextFieldPatron(new TextField());
        register.setTextFieldIDNumber(new TextField());
        register.setTextFieldDocN(new TextField());
        register.setTextFieldDOB(new DatePicker());
        register.setTextFieldCiti(new ComboBox<>());
        register.setTextFieldTypeDoc(new ComboBox<>());
        register.setLabelMessage(new Label());
    }

    @AfterAll
    static void tearDownJavaFX() {
        Platform.exit();
    }
    @Test
    void testValidateClientFields_allFieldsValid() {
        // Устанавливаем корректные данные
        register.getTextFieldLogin().setText("validLogin");
        register.getTextFieldEmail().setText("email@example.com");
        register.getPasswordFieldPassword().setText("validPassword");
        register.getPasswordFieldRepeatPassword().setText("validPassword");
        register.getTextFieldName().setText("Имя");
        register.getTextFieldSurname().setText("Фамилия");
        register.getTextFieldPatron().setText("Отчество");
        register.getTextFieldIDNumber().setText("123456789");
        register.getTextFieldDocN().setText("123456789");
        register.getTextFieldDOB().setValue(java.time.LocalDate.now().minusYears(25));
        register.getTextFieldCiti().setValue("Беларусь");
        register.getTextFieldTypeDoc().setValue("Passport");

        // Проверяем, что метод возвращает true
        assertTrue(register.validateClientFields());
    }

    @Test
    void testValidateClientFields_missingFields() {
        // Пропускаем заполнение всех полей
        register.getTextFieldLogin().setText("");
        register.getTextFieldEmail().setText("");
        register.getPasswordFieldPassword().setText("");
        register.getPasswordFieldRepeatPassword().setText("");

        assertFalse(register.validateClientFields());

        assertEquals("Заполните все поля.", register.getLabelMessage().getText());
    }

    @Test
    void testValidateClientFields_invalidEmail() {
        // Устанавливаем некорректный email
        register.getTextFieldEmail().setText("invalid-email");
        register.getTextFieldLogin().setText("validLogin");
        register.getPasswordFieldPassword().setText("validPassword");
        register.getPasswordFieldRepeatPassword().setText("validPassword");
        register.getTextFieldName().setText("Имя");
        register.getTextFieldSurname().setText("Фамилия");
        register.getTextFieldPatron().setText("Отчество");
        register.getTextFieldIDNumber().setText("123456789");
        register.getTextFieldDocN().setText("123456789");
        register.getTextFieldDOB().setValue(java.time.LocalDate.now().minusYears(25));
        register.getTextFieldCiti().setValue("Беларусь");
        register.getTextFieldTypeDoc().setValue("Passport");

        // Проверяем, что метод возвращает false
        assertFalse(register.validateClientFields());

        assertEquals("Введите корректный email.", register.getLabelMessage().getText());
    }

    @Test
    void testValidateClientFields_invalidPasswordLength() {
        // Устанавливаем короткий пароль
        register.getPasswordFieldPassword().setText("123");
        register.getPasswordFieldRepeatPassword().setText("123");
        register.getTextFieldLogin().setText("validLogin");
        register.getTextFieldEmail().setText("email@example.com");
        register.getTextFieldName().setText("Имя");
        register.getTextFieldSurname().setText("Фамилия");
        register.getTextFieldPatron().setText("Отчество");
        register.getTextFieldIDNumber().setText("123456789");
        register.getTextFieldDocN().setText("123456789");
        register.getTextFieldDOB().setValue(java.time.LocalDate.now().minusYears(25));
        register.getTextFieldCiti().setValue("Беларусь");
        register.getTextFieldTypeDoc().setValue("Passport");

        // Проверяем, что метод возвращает false
        assertFalse(register.validateClientFields());

        // Проверяем сообщение ошибки
        assertEquals("Логин и пароль должны быть от 4 до 20 символов.", register.getLabelMessage().getText());
    }

    @Test
    void testInitialize_comboboxValues() {
        // Вызываем метод initialize
        register.initialize();

        // Проверяем, что ComboBox заполнены корректно
        assertEquals(6, register.getTextFieldCiti().getItems().size());
        assertTrue(register.getTextFieldCiti().getItems().contains("Беларусь"));

        assertEquals(3, register.getTextFieldTypeDoc().getItems().size());
        assertTrue(register.getTextFieldTypeDoc().getItems().contains("Passport"));
    }
}
