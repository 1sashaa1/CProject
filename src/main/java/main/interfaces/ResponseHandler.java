package main.interfaces;

import main.Models.TCP.Response;

public interface ResponseHandler {
    void handle(Response response);
}
