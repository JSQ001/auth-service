

package com.hand.hcf.app.core.exception.core;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends RuntimeException {

    private List<ValidationError> validationErrors = new ArrayList<ValidationError>();

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(List<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public ValidationException(ValidationError validationError) {
        validationErrors.add(validationError);
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    @Override
    public String toString() {
        return "ValidationException{" +
                "validationErrors=" + validationErrors +
                '}';
    }
}
