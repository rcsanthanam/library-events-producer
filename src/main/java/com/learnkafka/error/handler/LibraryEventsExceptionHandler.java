package com.learnkafka.error.handler;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class LibraryEventsExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> requestBodyHandler(MethodArgumentNotValidException ex) {
	List<FieldError> errorList = ex.getBindingResult().getFieldErrors();
	String error = errorList.stream()
		                 .map(fieldError -> fieldError.getField() + " - " + fieldError.getDefaultMessage())
		                 .sorted()
		                 .collect(Collectors.joining(", "));
	log.info(" Error : {}", error);
	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
