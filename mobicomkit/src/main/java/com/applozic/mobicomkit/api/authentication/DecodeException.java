package com.applozic.mobicomkit.api.authentication;

import com.applozic.mobicomkit.annotations.ApplozicInternal;

@SuppressWarnings("WeakerAccess")
@ApplozicInternal
public class DecodeException extends RuntimeException {

    DecodeException(String message) {
        super(message);
    }

    DecodeException(String message, Throwable cause) {
        super(message, cause);
    }
}
