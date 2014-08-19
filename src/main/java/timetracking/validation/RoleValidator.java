package timetracking.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import timetracking.domain.Role;

/**
 * Created with IntelliJ IDEA.
 * User: russellb337
 * Date: 6/20/14
 * Time: 8:56 AM
 */

public class RoleValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return Role.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name", "field.required", "The name field is required");
    }
}
