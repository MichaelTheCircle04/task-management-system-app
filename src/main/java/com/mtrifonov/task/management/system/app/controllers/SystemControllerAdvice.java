package com.mtrifonov.task.management.system.app.controllers;

import java.util.NoSuchElementException;

import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class SystemControllerAdvice {
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) {
		return ResponseEntity.status(403).body(e.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class) 
	public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
		return ResponseEntity.badRequest().body("Incorrect value: " + e.getValue() + " cause exception: " + e.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException .class) 
	public ResponseEntity<String> handleMethodArgumentNotValidException (MethodArgumentNotValidException  e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
		return ResponseEntity.badRequest().body("Error deserializing request body: " + e.getMessage());
	}
	
	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<String> handleHandlerMethodValidationException(HandlerMethodValidationException e) {
		return ResponseEntity.badRequest().body("Invalid argument cause exception: " + e.getMessage());
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
	
	@ExceptionHandler(IndexOutOfBoundsException.class)
	public ResponseEntity<String> handleIndexOutOfBoundsException(IndexOutOfBoundsException e) {
		return ResponseEntity.badRequest().body("Invalid page number: " + e.getMessage());
	}
	
	@ExceptionHandler(PropertyReferenceException.class)
	public ResponseEntity<String> handlePropertyReferenceException(PropertyReferenceException e) {
		return ResponseEntity.badRequest().body("Invalid sort field: " + e.getMessage());
	}
}
