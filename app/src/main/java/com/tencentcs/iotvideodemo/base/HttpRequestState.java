package com.tencentcs.iotvideodemo.base;

import com.google.gson.JsonObject;

public class HttpRequestState {
    public enum Status {
        START, SUCCESS, ERROR
    }

    private Status status;

    private JsonObject jsonObject;

    private Throwable e;

    public Status getStatus() {
        return status;
    }

    public String getStatusTip() {
        if (status == Status.START) {
            return "start http request";
        } else if (status == Status.SUCCESS) {
            return jsonObject.toString();
        } else if (status == Status.ERROR) {
            return e.getMessage();
        }

        return "";
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Throwable getE() {
        return e;
    }

    public void setE(Throwable e) {
        this.e = e;
    }
}
