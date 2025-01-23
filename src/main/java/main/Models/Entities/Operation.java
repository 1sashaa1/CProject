package main.Models.Entities;

public class Operation {
    private int idoperation;
    private String dateSend;
    private String type;
    private double sum;
    private boolean done;
    private int iddata;
    private int idclient;
    private int idcldep;

    public Operation(int idoperation) {
        this.idoperation = idoperation;
    }

    public Operation(boolean done, double sum, String dateSend, int idoperation) {
        this.done = done;
        this.sum = sum;
        this.dateSend = dateSend;
        this.idoperation = idoperation;
    }

    public int getIddata() {
        return iddata;
    }

    public void setIddata(int iddata) {
        this.iddata = iddata;
    }

    public int getIdclient() {
        return idclient;
    }

    public void setIdclient(int idclient) {
        this.idclient = idclient;
    }

    public int getIdcldep() {
        return idcldep;
    }

    public void setIdcldep(int idcldep) {
        this.idcldep = idcldep;
    }

    public int getIdoperation() {
        return idoperation;
    }

    public void setIdoperation(int idoperation) {
        this.idoperation = idoperation;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public int getIdOperation() {
        return idoperation;
    }

    public void setIdOperation(int idOperation) {
        this.idoperation = idOperation;
    }

    public String getDateSend() {
        return dateSend;
    }

    public void setDateSend(String dateSend) {
        this.dateSend = dateSend;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }
}
