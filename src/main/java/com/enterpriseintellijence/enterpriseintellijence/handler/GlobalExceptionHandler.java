package com.enterpriseintellijence.enterpriseintellijence.handler;

import com.enterpriseintellijence.enterpriseintellijence.exception.IdMismatchException;

import com.enterpriseintellijence.enterpriseintellijence.exception.ManyRequestException;
import com.enterpriseintellijence.enterpriseintellijence.exception.ProductAlreadyLikedException;
import com.enterpriseintellijence.enterpriseintellijence.exception.TokenExpiredException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IdMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseStatusException idMismatchExceptionHandler(WebRequest req, IdMismatchException ex) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ProductAlreadyLikedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseStatusException productAlreadyLikedExceptionHandler(WebRequest req, ProductAlreadyLikedException ex) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseStatusException illegalArgumentExceptionHandler(WebRequest req, IllegalArgumentException ex) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalAccessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseStatusException illegalAccessExceptionHandler(WebRequest req, IllegalAccessException ex) {
        return new ResponseStatusException(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseStatusException entityNotFoundExceptionHandler(WebRequest req, EntityNotFoundException ex) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseStatusException methodArgumentNotValidExceptionHandler(WebRequest req, MethodArgumentNotValidException ex) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseStatusException defaultErrorHandler(WebRequest req, Exception ex) {
        log.error(ex.getMessage(), ex);
        return new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong :( \ncall Ciccio Gallo or Ernesto Rapisarda");
    }

    @ExceptionHandler(ManyRequestException.class)
    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    public String manyRequestErrorHandler(WebRequest req ,Exception ex){
        return new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, ex.getMessage()).getMessage();
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED)
    public String unauthenticatedErrorHandler(WebRequest req ,Exception ex){
        return new ResponseStatusException(HttpStatus.NETWORK_AUTHENTICATION_REQUIRED, ex.getMessage()).getMessage();
    }

}
