package com.intuit.developer.sampleapp.timetracking.validation;

import com.intuit.developer.sampleapp.timetracking.controllers.SyncRequest;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/20/14
 * Time: 8:56 AM
 */

public class SyncRequestValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return SyncRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "field.required", "The type field is required");
    }
}
