package jp.co.willwave.aca.validator;

import jp.co.willwave.aca.utilities.CommonUtil;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EitherRequiredValidator extends AbstractValidator implements ConstraintValidator<EitherRequired, Object> {
    /**
     * 検証メッセージ
     */
    private String message;

    /**
     * 検証対象となるフィールド
     */
    private String[] fields = {};

    /**
     * 検証対象となるフィールド名
     */
    private String[] names = {};

    @Override
    public void initialize(EitherRequired constraintAnnotation) {
        super.decisions = constraintAnnotation.decisions();
        super.decisionValues = constraintAnnotation.decisionValues();
        message = constraintAnnotation.message();
        fields = constraintAnnotation.fields();
        names = constraintAnnotation.names();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (super.isExcludable(value)) {
            return true;
        }

        boolean isValid = false;
        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        for (String field : fields) {
            Object fieldValue = beanWrapper.getPropertyValue(field);
            if (!CommonUtil.isEmpty(fieldValue)) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            message = String.format("[%s]のいずれか１つは必須です。", String.join("、", names));
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }

}
