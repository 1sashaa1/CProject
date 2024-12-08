package main.builder;

import main.Enums.Roles;
import main.Models.Entities.Client;
import main.Models.Entities.User;

public class UserBuilder {
    private final User user;

    public UserBuilder() {
        user = new User();
    }

    public UserBuilder withLogin(String login) {
        user.setLogin(login);
        return this;
    }

    public UserBuilder withRole(Roles role) {
        user.setRole(role.name());
        return this;
    }

    public UserBuilder withPassword(String password) {
        user.setPassword(password);
        return this;
    }

    public UserBuilder withClient(Client client) {
        user.setClient(client);
        return this;
    }

    public User build() {
        return user;
    }
}
