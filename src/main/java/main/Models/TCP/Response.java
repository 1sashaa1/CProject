package main.Models.TCP;

import main.Enums.ResponseStatus;

public class Response { //???
    private ResponseStatus responseStatus;
    private String responseData;


    public Response(ResponseStatus responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Response(ResponseStatus responseStatus, String responseData) {
        this.responseStatus = responseStatus;
        this.responseData = responseData;
    }

    public ResponseStatus getResponseStatus() {
        return responseStatus;
    }

    public String getResponseData() {
        return responseData;
    }
}
