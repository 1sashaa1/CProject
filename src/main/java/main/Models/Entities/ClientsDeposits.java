package main.Models.Entities;

import java.util.Date;

public class ClientsDeposits {
    private int idDeposit;
    private boolean isOpen;
    private double firstAmount;
    private Date openingDate;

    public ClientsDeposits(int idDeposit, double firstAmount, Date openingDate, boolean isOpen) {
        this.idDeposit = idDeposit;
        this.isOpen = isOpen;
        this.firstAmount = firstAmount;
        this.openingDate = openingDate;
    }

    public int getIdDeposit() {
        return idDeposit;
    }

    public void setIdDeposit(int idDeposit) {
        this.idDeposit = idDeposit;
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
}
