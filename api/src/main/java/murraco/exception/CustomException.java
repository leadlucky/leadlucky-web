package murraco.exception;

import org.springframework.http.HttpStatus;

import java.util.function.Predicate;
import java.util.function.Supplier;

public class CustomException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final String message;
  private final HttpStatus httpStatus;

  public CustomException(String message, HttpStatus httpStatus) {
    this.message = message;
    this.httpStatus = httpStatus;
  }

  @Override
  public String getMessage() {
    return message;
  }

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public static Supplier<CustomException> getUserNotFoundHandler(String username){
    return () -> new CustomException(
            "Logged in user " + username + " could not be found",
            HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
