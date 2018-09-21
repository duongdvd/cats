package jp.co.willwave.aca.validator;

import jp.co.willwave.aca.utilities.CommonUtil;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;

public class NotEmptyExValidator extends AbstractValidator implements ConstraintValidator<NotEmptyEx, Object> {
    /**
     * 検証メッセージ
     */
    private String message;

    /**
     * 検証対象となるフィールド名
     */
    private String[] fields = {};

    @Override
    public void initialize(NotEmptyEx constraintAnnotation) {
        super.decisions = constraintAnnotation.decisions();
        super.decisionValues = constraintAnnotation.decisionValues();
        message = constraintAnnotation.message();
        fields = constraintAnnotation.fields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (super.isExcludable(value)) {
            return true;
        }

        List<String> errorFields = new ArrayList<String>();
        BeanWrapper beanWrapper = new BeanWrapperImpl(value);

        for (String field : fields) {
            Object fieldValue = beanWrapper.getPropertyValue(field);
            if (CommonUtil.isEmpty(fieldValue)) {
                errorFields.add(field);
            }
        }

        if (0 < errorFields.size()) {
            context.disableDefaultConstraintViolation();
            for (String field : errorFields) {
                context.buildConstraintViolationWithTemplate(message).addPropertyNode(field).addConstraintViolation();
            }

            return false;
        }

        return true;
    }

}
