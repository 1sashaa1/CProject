package main.Models.Entities;

import java.io.Serializable;

public class User implements Serializable {
    private int id;
    private String login;
    private String password;
    private String role;

    private Client client;

    public User(){

    }
    public User(int ID, String login, String password, String role, Client client) {
        this.id = ID;
        this.login = login;
        this.password = password;
        this.role = role;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public Client getClient() {
        return client;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setClient(Client client) {
        this.client = client;
    }

}
