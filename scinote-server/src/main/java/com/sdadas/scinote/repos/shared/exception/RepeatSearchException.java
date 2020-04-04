package com.sdadas.scinote.repos.shared.exception;

/**
 * @author Sławomir Dadas
 */
public class RepeatSearchException extends Exception {

    private final RepeatSearch context;

    public RepeatSearchException(RepeatSearch context) {
        this.context = context;
    }

    public RepeatSearch getContext() {
        return context;
    }
}
