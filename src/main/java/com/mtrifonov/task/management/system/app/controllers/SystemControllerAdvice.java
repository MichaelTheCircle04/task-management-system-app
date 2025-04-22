package com.mtrifonov.task.management.system.app.controllers;

import java.util.NoSuchElementException;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
*
* @Mikhail Trifonov
*/
@ControllerAdvice
public class SystemControllerAdvice {
	
	@ExceptionHandler(MissingServletRequestParameterException.class)
	public ResponseEntity<String> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) { //Если отсутствует обязательный параметр
		return ResponseEntity.badRequest().body("Error due to missing required parameter: " + e.getMessage());
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<String> handleAccessDeniedException(AccessDeniedException e) { //Если недостаточо прав доступа
		return ResponseEntity.status(403).body(e.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class) 
	public ResponseEntity<String> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) { //Если аргумент не подходящего типа
		return ResponseEntity.badRequest().body("Incorrect value: " + e.getValue() + " cause exception: " + e.getMessage());
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class) 
	public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) { //Если не валидные параметры/тело отмеченные @Valid
		return ResponseEntity.badRequest().body(e.getMessage());
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<String> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) { //Если не передано тело запроса/не соответсвует ожидаемому
		return ResponseEntity.badRequest().body("Error deserializing request body: " + e.getMessage());
	}
	
	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<String> handleHandlerMethodValidationException(HandlerMethodValidationException e) { //Если не валидные параметры/тело 
		return ResponseEntity.badRequest().body("Invalid argument cause exception: " + e.getMessage());		   //отмеченные напрямую constraint-аннотациями
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) { //Если не удалось найти по ID
		return ResponseEntity.badRequest().body(e.getMessage());
	}
	
	@ExceptionHandler(PropertyReferenceException.class)
	public ResponseEntity<String> handlePropertyReferenceException(PropertyReferenceException e) { //Если указано неправильное поле для сортировки
		return ResponseEntity.badRequest().body("Invalid sort field: " + e.getMessage());
	}
}
