package main.Models.TCP;

import com.google.gson.Gson;
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

    public String getResponseUser() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getResponseData() {
        return responseData;
    }
}
