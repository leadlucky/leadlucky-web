package com.leadlucky.api.exception

import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

import javax.servlet.http.HttpServletResponse

@RestControllerAdvice
@Slf4j
class GlobalExceptionHandlerController {

    @ExceptionHandler(CustomException.class)
    void handleCustomException(HttpServletResponse res, CustomException ex) throws IOException {
        if (ex.httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
            logException(ex)
        }

        res.sendError(ex.httpStatus.value(), ex.message)
    }

    @ExceptionHandler(AccessDeniedException.class)
    void handleAccessDeniedException(HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.UNAUTHORIZED.value(), "Access denied")
    }

    @ExceptionHandler(Exception.class)
    void handleException(HttpServletResponse res, Exception ex) throws IOException {
        logException(ex)
        res.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Something went wrong")
    }

    private static void logException(Exception ex) {
        log.error(ex.message)
        ex.printStackTrace()
    }

}
