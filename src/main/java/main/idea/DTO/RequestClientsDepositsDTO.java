package main.idea.DTO;

import java.util.Date;

public class RequestClientsDepositsDTO {
    private int idDeposit;

    private String nameDeposit;
    private boolean isOpen;
    private double firstAmount;
    private Date openingDate;

    private int idClient;
    private String name;
    private String surname;
    private String patronymic;

    public RequestClientsDepositsDTO() {
    }

    public RequestClientsDepositsDTO(int idDeposit, String nameDeposit, double firstAmount, Date openingDate, boolean isOpen) {
        this.idDeposit = idDeposit;
        this.nameDeposit = nameDeposit;
        this.isOpen = isOpen;
        this.firstAmount = firstAmount;
        this.openingDate = openingDate;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public int getIdDeposit() {
        return idDeposit;
    }

    public void setIdDeposit(int idDeposit) {
        this.idDeposit = idDeposit;
    }

    public String getNameDeposit() {
        return nameDeposit;
    }

    public void setNameDeposit(String nameDeposit) {
        this.nameDeposit = nameDeposit;
    }

    public boolean getIsOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public double getFirstAmount() {
        return firstAmount;
    }

    public void setFirstAmount(double firstAmount) {
        this.firstAmount = firstAmount;
    }

    public Date getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Date openingDate) {
        this.openingDate = openingDate;
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
}
