package app.helpers.validations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@ControllerAdvice
public class ValidationErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ValidationErrors> handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<ValidationError> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            var err = new ValidationError();
            err.setCode(error.getCode());
            err.setArguments(Arrays.stream(Objects.requireNonNull(error.getArguments())).map(Object::toString).toList());
            errors.add(err);
        });
        var result = new ValidationErrors();
        result.setErrors(errors);
        return ResponseEntity.badRequest().body(result);
    }
}
