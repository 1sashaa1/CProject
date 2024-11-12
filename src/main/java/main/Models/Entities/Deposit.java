package main.Models.Entities;

public class Deposit {
    private int id;
    private String name;
    private String type;
    private double interestRate;
    private double minAmount;
    private int term;
    private boolean isReplenishment;
    private boolean isTransactions;
    private boolean isProlongation;

    public Deposit() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public boolean isReplenishment() {
        return isReplenishment;
    }

    public void setReplenishment(boolean isReplenishment) {
        this.isReplenishment = isReplenishment;
    }

    public boolean isTransactions() {
        return isTransactions;
    }

    public void setTransactions(boolean isTransactions) {
        this.isTransactions = isTransactions;
    }

    public boolean isProlongation() {
        return isProlongation;
    }

    public void setProlongation(boolean isProlongation) {
        this.isProlongation = isProlongation;
    }
}