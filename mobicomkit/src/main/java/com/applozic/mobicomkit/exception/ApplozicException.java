package com.applozic.mobicomkit.exception;

/**
 * Default base Applozic exception.
 */
//Cleanup: can be improved or removed
public class ApplozicException extends Exception {
    private String message;

    public ApplozicException(String message) {
        super(message);
        this.message = message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
