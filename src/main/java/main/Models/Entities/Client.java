package main.Models.Entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Date;
import java.util.Set;

public class Client implements Serializable {
    @SerializedName("id")
    private int clientId;

    private String email;

    private String name;

    private String surname;

    private String patronymic;

    private String dob;

    private String citizenship;

    private String documentType;

    private String idNumber;

    private String documentNumber;
    private Set<User> Users = new HashSet<>();

    public Client() {
    }

    public Client(int clientId, String name, String surname, String patronymic, Date dob, String citizenship, String documentType, String idNumber, String documentNumber, Set<User> users) {
        this.clientId = clientId;
        this.name = name;
        this.surname = surname;
        this.patronymic = patronymic;
        this.dob = String.valueOf(dob);
        this.citizenship = citizenship;
        this.documentType = documentType;
        this.idNumber = idNumber;
        this.documentNumber = documentNumber;
        Users = users;
    }

    public int getClientId() {
        return clientId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCitizenship() {
        return citizenship;
    }

    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }


    public Set<User> getUsers() {
        return Users;
    }

    public void setUsers(Set<User> users) {
        Users = users;
    }

    @Override
    public String toString() {
        return "Клиент" +
                "id=" + clientId +
                ", name='" + name + '\'';
    }
}