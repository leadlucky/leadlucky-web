package com.leadlucky.api.exception

import org.springframework.http.HttpStatus

import java.util.function.Predicate
import java.util.function.Supplier

class CustomException extends RuntimeException {

  private static final long serialVersionUID = 1L

  String message
  HttpStatus httpStatus

  @Override
  String getMessage() {
    return message
  }

  HttpStatus getHttpStatus() {
    return httpStatus
  }

  static Supplier<CustomException> getUserNotFoundHandler(String username){
    return {
      new CustomException(
              message: "Logged in user " + username + " could not be found",
              httpStatus: HttpStatus.INTERNAL_SERVER_ERROR)
    }
  }

}
