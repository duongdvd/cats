package jp.co.willwave.aca.validator;

import jp.co.willwave.aca.utilities.CommonUtil;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class AssociatedRequiredValidator extends AbstractValidator
        implements ConstraintValidator<AssociatedRequired, Object> {
    /**
     * 検証メッセージ
     */
    private String message;

    /**
     * subFieldsの入力が１つでもあれば必須となる項目
     */
    private String[] mainFields = {};

    /**
     * mainFieldsの必須判定に影響する項目
     */
    private String[] subFields = {};

    @Override
    public void initialize(AssociatedRequired constraintAnnotation) {
        super.decisions = constraintAnnotation.decisions();
        super.decisionValues = constraintAnnotation.decisionValues();
        message = constraintAnnotation.message();
        mainFields = constraintAnnotation.mainFields();
        subFields = constraintAnnotation.subFields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (super.isExcludable(value)) {
            return true;
        }

        boolean isRequired = false;
        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        for (String field : subFields) {
            Object fieldValue = beanWrapper.getPropertyValue(field);
            if (!CommonUtil.isEmpty(fieldValue)) {
                isRequired = true;
                break;
            }
        }

        if (isRequired) {
            List<String> errorFields = new ArrayList<String>();
            for (String field : mainFields) {
                Object fieldValue = beanWrapper.getPropertyValue(field);
                if (CommonUtil.isEmpty(fieldValue)) {
                    errorFields.add(field);
                }
            }

            if (0 < errorFields.size()) {
                context.disableDefaultConstraintViolation();
                for (String field : errorFields) {
                    context.buildConstraintViolationWithTemplate(message).addPropertyNode(field)
                            .addConstraintViolation();
                }

                return false;
            }
        }

        return true;
    }

}
