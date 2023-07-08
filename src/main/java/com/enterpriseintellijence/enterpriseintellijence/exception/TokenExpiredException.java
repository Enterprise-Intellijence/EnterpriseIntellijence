package com.enterpriseintellijence.enterpriseintellijence.exception;

import lombok.experimental.StandardException;

@StandardException
public class TokenExpiredException extends RuntimeException{
    public TokenExpiredException() {
        super("Token expired");
    }
}
