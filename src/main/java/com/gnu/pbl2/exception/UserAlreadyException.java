package com.gnu.pbl2.exception;

public class UserAlreadyException extends RuntimeException{

    public UserAlreadyException(String message) {
        super(message);
    }
}
