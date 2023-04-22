package com.enterpriseintellijence.enterpriseintellijence.exception;

import lombok.experimental.StandardException;

@StandardException
public class IdMismatchException extends RuntimeException {
    public IdMismatchException() {
        super("Id mismatch");
    }
}
