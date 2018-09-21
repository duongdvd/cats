package jp.co.willwave.aca.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = AssociatedRequiredValidator.class)
public @interface AssociatedRequired {
    String message() default "{AssociatedRequired}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * subFieldsの入力が１つでもあれば必須となる項目
     *
     * @return
     */
    String[] mainFields() default {};

    /**
     * mainFieldsの必須判定に影響する項目
     *
     * @return
     */
    String[] subFields() default {};

    String[] decisions() default {};

    String[] decisionValues() default {};
}
