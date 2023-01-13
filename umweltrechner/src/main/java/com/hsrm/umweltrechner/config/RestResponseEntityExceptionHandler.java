package com.hsrm.umweltrechner.config;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FAILED_DEPENDENCY;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_MODIFIED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import java.sql.SQLIntegrityConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.hsrm.umweltrechner.exceptions.NotAuthorizedException;
import com.hsrm.umweltrechner.exceptions.NotFoundException;
import com.hsrm.umweltrechner.exceptions.NotModifiedException;
import com.hsrm.umweltrechner.exceptions.interpreter.DivideByZeroException;
import com.hsrm.umweltrechner.exceptions.interpreter.DomainException;
import com.hsrm.umweltrechner.exceptions.interpreter.IllegalWriteException;
import com.hsrm.umweltrechner.exceptions.interpreter.IncorrectSyntaxException;
import com.hsrm.umweltrechner.exceptions.interpreter.OutOfRangeException;
import com.hsrm.umweltrechner.exceptions.interpreter.UnknownSymbolException;

import lombok.extern.log4j.Log4j2;

@ControllerAdvice
@Log4j2
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {IllegalStateException.class})
  protected ResponseEntity<Object> handleConflict(RuntimeException ex, WebRequest rq) {
    String response = ex.getLocalizedMessage();
    log.warn("Caught IllegalStateException", ex);
    return handleExceptionInternal(ex, response, new HttpHeaders(), CONFLICT, rq);
  }

  @ExceptionHandler(value = {IllegalArgumentException.class})
  protected ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex,
      WebRequest rq) {
    String response = ex.getLocalizedMessage();
    log.warn("Caught IllegalArgumentException", ex);
    return handleExceptionInternal(ex, response, new HttpHeaders(), BAD_REQUEST, rq);
  }

  @ExceptionHandler(value = {NotFoundException.class})
  protected ResponseEntity<Object> handleNotFound(NotFoundException ex, WebRequest rq) {
    String response = ex.getLocalizedMessage();
    log.warn("Caught NotFoundException ", ex);
    return handleExceptionInternal(ex, response, new HttpHeaders(), NOT_FOUND, rq);
  }

  @ExceptionHandler(value = {OptimisticLockingFailureException.class})
  protected ResponseEntity<Object> handleOptimisticLockingFailureException(OptimisticLockingFailureException ex, WebRequest rq) {
    String response = "DB Conflict";
    log.warn("Caught OptimisticLockingFailureException ", ex);
    return handleExceptionInternal(ex, response, new HttpHeaders(), CONFLICT, rq);
  }

  @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class})
  protected ResponseEntity<Object> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex, WebRequest rq) {
    String response = "DB Error";
    log.warn("Caught SQLIntegrityConstraintViolationException ", ex);
    return handleExceptionInternal(ex, response, new HttpHeaders(), NOT_ACCEPTABLE, rq);
  }

  @ExceptionHandler(value = {DataIntegrityViolationException.class})
  protected ResponseEntity<Object> handleDataIntegrityViolationException(SQLIntegrityConstraintViolationException ex, WebRequest rq) {
    String response = "Not deletable!!";
    log.warn("Caught DataIntegrityViolationException ", ex);
    return handleExceptionInternal(ex, response, new HttpHeaders(), FAILED_DEPENDENCY, rq);
  }

  @ExceptionHandler(value = {NotAuthorizedException.class})
  protected ResponseEntity<Object> handleNotAuthorizedException(NotAuthorizedException ex,
      WebRequest rq) {
    String response = "Not Authorized!";
    log.warn("Caught NotAuthorizedExceptionn", ex);
    return handleExceptionInternal(ex, response, new HttpHeaders(), UNAUTHORIZED, rq);
  }

  @ExceptionHandler(value = {AccessDeniedException.class})
  protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex,
      WebRequest rq) {
    String response = "Insufficient permissions!";
    log.warn("Caught " + ex.getClass().getSimpleName(), ex);
    return handleExceptionInternal(ex, response, new HttpHeaders(), FORBIDDEN, rq);
  }


  @ExceptionHandler(value = {NotModifiedException.class})
  protected ResponseEntity<Object> handleNotModifiedException(Exception ex, WebRequest rq) {
    String response = "Not modified!";
    log.warn("Caught NotModifiedException", ex);
    return handleExceptionInternal(ex, response, new HttpHeaders(), NOT_MODIFIED, rq);
  }

  @ExceptionHandler({
      DivideByZeroException.class,
      DomainException.class,
      UnknownSymbolException.class,
      IllegalWriteException.class,
      IncorrectSyntaxException.class,
      OutOfRangeException.class
  })
  protected ResponseEntity<Object> handleFormulaInterpreterException(Exception ex, WebRequest rq) {
    log.error("Caught unhandled exception", ex);
    return handleExceptionInternal(ex, ex.getMessage(), new HttpHeaders(), BAD_REQUEST, rq);
  }
  @ExceptionHandler(value = {Exception.class})
  protected ResponseEntity<Object> handleAllRemainingException(Exception ex, WebRequest rq) {
    String response = "An unexpected error occurred!";
    log.error("Caught unhandled exception", ex);
    return handleExceptionInternal(ex, response, new HttpHeaders(), INTERNAL_SERVER_ERROR, rq);
  }

}
