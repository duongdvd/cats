package jp.co.willwave.aca.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(
        validatedBy = BeforeAfterDateValidator.class)
public @interface BeforeAfterDate {
    String message() default "{BeforeAfterDate}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * FROM日付項目
     *
     * @return
     */
    String[] beforFields() default {};

    /**
     * TO日付項目
     *
     * @return
     */
    String[] afterFields() default {};

    /**
     * 表示名称
     *
     * @return
     */
    String[] displayNames() default {};

    String[] decisions() default {};

    String[] decisionValues() default {};
}
