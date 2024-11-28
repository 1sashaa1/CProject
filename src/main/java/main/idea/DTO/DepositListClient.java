package main.idea.DTO;

import java.util.Date;

public class DepositListClient {
    private String nameDeposit;
    private String type;
    private double interestRate;
    private int term;
    private boolean prolongation;
    private double firstAmount;
    private Date openingDate;

    public DepositListClient() {
    }

    public DepositListClient(Date openingDate, double firstAmount, boolean prolongation, int term, double interestRate, String type, String nameDeposit) {
        this.openingDate = openingDate;
        this.firstAmount = firstAmount;
        this.prolongation = prolongation;
        this.term = term;
        this.interestRate = interestRate;
        this.type = type;
        this.nameDeposit = nameDeposit;
    }

    public String getNameDeposit() {
        return nameDeposit;
    }

    public void setNameDeposit(String nameDeposit) {
        this.nameDeposit = nameDeposit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public boolean getProlongation() {
        return prolongation;
    }

    public void setProlongation(boolean prolongation1) {
        prolongation = prolongation1;
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
