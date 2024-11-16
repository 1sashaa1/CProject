package main.Models.Entities;

import java.util.Date;

public class ClientsDeposits {
    private int idDeposit;
    private boolean isOpen;
    private double firstAmount;
    private Date openingDate;
    private int possibility;

    public int getIdDeposit() {
        return idDeposit;
    }

    public void setIdDeposit(int idDeposit) {
        this.idDeposit = idDeposit;
    }

    public boolean isOpen() {
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

    public int getPossibility() {
        return possibility;
    }

    public void setPossibility(int possibility) {
        this.possibility = possibility;
    }
}
