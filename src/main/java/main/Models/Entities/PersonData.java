package main.Models.Entities;

import java.util.HashSet;
import java.util.Set;

public class PersonData {
    private String Email;

    private Set<User> Users = new HashSet<>();

    public PersonData(){

    }

    public PersonData(String email, Set<User> users) {
        Email = email;
        Users = users;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public Set<User> getUsers() {
        return Users;
    }

    public void setUsers(Set<User> users) {
        Users = users;
    }
}
