package main.idea.DTO;

public class Session {
    private static int clientId;

    public static int getClientId() {
        return clientId;
    }

    public static void setClientId(int id) {
        clientId = id;
    }
}
