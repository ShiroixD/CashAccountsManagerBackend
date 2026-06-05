package org.dev.cash_accounts_manager_backend.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.io.IOException;
import io.jsonwebtoken.security.SignatureException;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException.BadRequest;

/**
 * Global exception handler catching exceptions occurred during request processing
 *
 * @author Fabian Frontczak
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     *  Method catching exceptions occurred during request processing logic, data access, security checks
     *  @param exception caught exception instance
     *  @return {@link org.springframework.http.ProblemDetail}
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception exception) {
        ProblemDetail errorDetail = null;

        exception.printStackTrace();

        if (exception instanceof BadRequest) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), exception.getMessage());
            errorDetail.setProperty("description", "Bad request");

            return errorDetail;
        }

        if (exception instanceof BadCredentialsException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(402), exception.getMessage());
            errorDetail.setProperty("description", "The username or password is incorrect");

            return errorDetail;
        }

        if (exception instanceof AccountStatusException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The account is locked");

            return errorDetail;
        }

        if (exception instanceof AuthenticationException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
            errorDetail.setProperty("description", "Authentication error");

            return errorDetail;
        }

        if (exception instanceof AccessDeniedException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "You are not authorized to access this resource");

            return errorDetail;
        }

        if (exception instanceof SignatureException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The JWT signature is invalid");

            return errorDetail;
        }

        if (exception instanceof ExpiredJwtException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
            errorDetail.setProperty("description", "The JWT token has expired");

            return errorDetail;
        }

        if (exception instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            List<String> errors = methodArgumentNotValidException.getBindingResult()
                    .getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
            ;
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(406), "Request fields validations error");
            errorDetail.setProperty("description", errors);

            return errorDetail;
        }

        if (exception instanceof HttpMessageNotReadableException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(512), exception.getMessage());
            errorDetail.setProperty("description", "Invalid http message: not readable");

            return errorDetail;
        }

        if (exception instanceof UserRoleNotExist) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(513), exception.getMessage());
            errorDetail.setProperty("description", "Role not found");

            return errorDetail;
        }

        if (exception instanceof UsernameNotFoundException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(514), exception.getMessage());
            errorDetail.setProperty("description", "Account with given name not found");

            return errorDetail;
        }

        if (exception instanceof UserNotFound) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(531), exception.getMessage());
            errorDetail.setProperty("description", "Account with given name not found");

            return errorDetail;
        }

        if (exception instanceof UserAlreadyExistAuthenticationException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(515), exception.getMessage());
            errorDetail.setProperty("description", "User with given name exists");

            return errorDetail;
        }

        if (exception instanceof SQLException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(516), exception.getMessage());
            errorDetail.setProperty("description", "SQL error");

            return errorDetail;
        }

        if (exception instanceof IOException) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(517), exception.getMessage());
            errorDetail.setProperty("description", "IO error");

            return errorDetail;
        }

        if (errorDetail == null) {
            errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
            errorDetail.setProperty("description", "Unknown internal server error.");
        }

        return errorDetail;
    }
}