package ru.ifmo.web.service.exception;

import lombok.Getter;

import javax.xml.ws.WebFault;

@WebFault(faultBean = "ru.ifmo.web.service.exception.MenagerieServiceFault")
public class MenagerieServiceException extends Exception {
    @Getter
    private final MenagerieServiceFault faultInfo;

    public MenagerieServiceException(String message, MenagerieServiceFault faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    public MenagerieServiceException(String message, Throwable cause, MenagerieServiceFault faultInfo) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }
}
