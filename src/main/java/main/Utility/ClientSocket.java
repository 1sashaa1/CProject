package main.Utility;

import main.Models.Entities.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocket {
    private static final ClientSocket SINGLE_INSTANCE = new ClientSocket();
    private User user;
    private static Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private ClientSocket(){
        try{
            socket = new Socket("localhost", 5555);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));//запись в контейнер
            out = new PrintWriter(socket.getOutputStream());
        } catch(Exception e){
        }
    }

    private int FlightId = -1;

    public static ClientSocket getInstance(){return SINGLE_INSTANCE;}


    public int getFlightId() {
        return FlightId;
    }

    public User getUser() {
        return user;
    }

    public static Socket getSocket() {
        return socket;
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setFlightId(int flightId) {
        FlightId = flightId;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static void setSocket(Socket socket) {
        ClientSocket.socket = socket;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }

}
