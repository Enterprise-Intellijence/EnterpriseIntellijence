package com.enterpriseintellijence.enterpriseintellijence.exception;

import lombok.experimental.StandardException;

@StandardException
public class UnauthenticatedException extends RuntimeException{
    public UnauthenticatedException() {
        super("Unauthenticated");
    }
}
