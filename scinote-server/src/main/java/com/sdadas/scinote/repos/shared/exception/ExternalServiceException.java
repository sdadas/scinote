package com.sdadas.scinote.repos.shared.exception;

/**
 * @author Sławomir Dadas
 */
public class ExternalServiceException extends RuntimeException {

    private final String body;

    public ExternalServiceException(String message) {
        super(message);
        this.body = null;
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
        this.body = null;
    }

    public ExternalServiceException(String message, String body, Throwable cause) {
        super(message, cause);
        this.body = body;
    }

    public String getBody() {
        return body;
    }
}
