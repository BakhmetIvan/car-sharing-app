package mate.capsharingapp.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Method;

public class FieldMatchValidator implements ConstraintValidator<FieldMatch, Object> {
    public static final String GET_METHOD_PREFIX = "get";
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            final Method getterFirst = value.getClass()
                    .getMethod(GET_METHOD_PREFIX + capitalize(firstFieldName));
            final Object firstObj = getterFirst.invoke(value);

            final Method getterSecond = value.getClass()
                    .getMethod(GET_METHOD_PREFIX + capitalize(secondFieldName));
            final Object secondObj = getterSecond.invoke(value);

            return firstObj == null && secondObj == null
                    || firstObj != null && firstObj.equals(secondObj);
        } catch (Exception e) {
            throw new RuntimeException("Помилка валідації пароля", e);
        }
    }

    private String capitalize(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }
}
