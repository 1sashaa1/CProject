package main.Models.Entities;

import java.io.Serializable;
import java.util.HashSet;

public class User implements Serializable {
    private int ID;
    private String Login;
    private String Password;
    private String Role;

    private PersonData personData;

    //private Set<UserMark> UserMarks = new HashSet<>();

    public User(){

    }
    public User(int ID, String login, String password, String role, PersonData personData) { //Set<UserMark> userMarks
        this.ID = ID;
        Login = login;
        Password = password;
        Role = role;
        this.personData = personData;
        //UserMarks = userMarks;
    }

    public int getID() {
        return ID;
    }

    public String getLogin() {
        return Login;
    }

    public String getPassword() {
        return Password;
    }

    public String getRole() {
        return Role;
    }

    public PersonData getPersonData() {
        return personData;
    }

//    public Set<UserMark> getUserMarks() {
//        return UserMarks;
//    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setLogin(String login) {
        Login = login;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public void setRole(String role) {
        Role = role;
    }

    public void setPersonData(PersonData personData) {
        this.personData = personData;
    }

//    public void setUserMarks(Set<UserMark> userMarks) {
//        UserMarks = userMarks;
//    }
}
