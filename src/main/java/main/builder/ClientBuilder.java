package main.builder;

import main.Models.Entities.Client;

public class ClientBuilder {
    private final Client client;

    public ClientBuilder() {
        client = new Client();
    }

    public ClientBuilder withEmail(String email) {
        client.setEmail(email);
        return this;
    }

    public ClientBuilder withName(String name) {
        client.setName(name);
        return this;
    }

    public ClientBuilder withSurname(String surname) {
        client.setSurname(surname);
        return this;
    }

    public ClientBuilder withPatronymic(String patronymic) {
        client.setPatronymic(patronymic);
        return this;
    }

    public ClientBuilder withDOB(String dob) {
        client.setDob(dob);
        return this;
    }

    public ClientBuilder withCitizenship(String citizenship) {
        client.setCitizenship(citizenship);
        return this;
    }

    public ClientBuilder withDocumentType(String documentType) {
        client.setDocumentType(documentType);
        return this;
    }

    public ClientBuilder withIdNumber(String idNumber) {
        client.setIdNumber(idNumber);
        return this;
    }

    public ClientBuilder withDocumentNumber(String documentNumber) {
        client.setDocumentNumber(documentNumber);
        return this;
    }

    public Client build() {
        return client;
    }
}
