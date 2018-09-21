package jp.co.willwave.aca.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;

@Documented
@Target({ElementType.TYPE, ANNOTATION_TYPE, METHOD, FIELD, CONSTRUCTOR, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = UniqueValidator.class)
public @interface Unique {
    String message() default "{Unique}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String field() default "";
}
