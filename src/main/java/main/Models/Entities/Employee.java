package main.Models.Entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Date;
import java.util.Set;

public class Employee implements Serializable {
    @SerializedName("id")
    private int IdE;

    private String seat;

    private Client client;

    public Employee() {
    }

    public Employee(int idE, String seat, Client client) {
        IdE = idE;
        this.seat = seat;
        Client Client = client;
    }

    public int getIdE() {
        return IdE;
    }

    public void setIdE(int idE) {
        IdE = idE;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}