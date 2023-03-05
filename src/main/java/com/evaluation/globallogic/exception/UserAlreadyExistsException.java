package com.evaluation.globallogic.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        super("user " + email + " already exists.");
    }
}
