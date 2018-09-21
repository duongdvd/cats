package jp.co.willwave.aca.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = NotEmptyExValidator.class)
public @interface NotEmptyEx {
    String message() default "{NotEmptyEx}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] fields() default {};

    String[] decisions() default {};

    String[] decisionValues() default {};
}
