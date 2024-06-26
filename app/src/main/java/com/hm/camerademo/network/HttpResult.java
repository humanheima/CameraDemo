package com.hm.camerademo.network;

import com.google.gson.annotations.SerializedName;

public class HttpResult<T> {

    private int resultCode;

    @SerializedName("resultMsg")
    private String resultMessage;

    private T body;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public boolean isSuccess() {
        return resultCode == 1;
    }
}
