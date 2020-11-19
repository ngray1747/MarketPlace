package com.modul.marketplace.restful;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ipos.fabimanager.model.employee.DmEmployee;

public class ApiError extends Exception {
    private String codeContent = "";
    private String messageBody = "";
    private int httpCode = 200;

    public String getMessageBody() {
        return messageBody;
    }

    public ApiError(String message, int code, String messageBody) {
        super(message);
        this.httpCode = code;
        this.messageBody = messageBody;
    }

    public ApiError(String exceptionMessage, Throwable reason) {
        super(exceptionMessage, reason);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public ApiError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ApiError(Throwable cause) {
        super(cause);
        messageBody = "Error API: " + cause.getMessage();
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public void check401(Context context) {
        if (httpCode == 401) {
            DmEmployee.logout(context);
        }
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public void setCodeContent(String codeContent) {
        this.codeContent = codeContent;
    }
}
