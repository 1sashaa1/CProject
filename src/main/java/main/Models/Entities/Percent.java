package main.Models.Entities;

public class Percent {
    private int idpercent;
    private int period;
    private double sum;
    private String date;

    public int getIdpercent() {
        return idpercent;
    }

    public void setIdpercent(int idercent) {
        this.idpercent = idercent;
    }

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
