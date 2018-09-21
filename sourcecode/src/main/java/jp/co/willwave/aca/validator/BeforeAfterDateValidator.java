package jp.co.willwave.aca.validator;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BeforeAfterDateValidator extends AbstractValidator
        implements ConstraintValidator<BeforeAfterDate, Object> {
    /**
     * 検証メッセージ
     */
    private String message;

    /**
     * FROM日付項目
     */
    private String[] beforFields = {};

    /**
     * TO日付項目
     */
    private String[] afterFields = {};

    @Override
    public void initialize(BeforeAfterDate constraintAnnotation) {
        super.decisions = constraintAnnotation.decisions();
        super.decisionValues = constraintAnnotation.decisionValues();
        message = constraintAnnotation.message();
        beforFields = constraintAnnotation.beforFields();
        afterFields = constraintAnnotation.afterFields();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (super.isExcludable(value)) {
            return true;
        }

        int count = beforFields.length;
        List<String[]> errorFields = new ArrayList<>();

        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        for (int i = 0; i < count; i++) {
            String beforField = beforFields[i];
            String afterField = afterFields[i];
            Date beforFieldValue = (Date) beanWrapper.getPropertyValue(beforField);
            Date afterFieldValue = (Date) beanWrapper.getPropertyValue(afterField);
            if (beforFieldValue == null || afterFieldValue == null) {
                // チェック無し
                continue;
            } else if (beforFieldValue.getTime() < afterFieldValue.getTime()) {
                // 正常
                continue;
            } else {
                // 異常
                errorFields.add(new String[]{beforField, afterField});
            }
        }

        if (0 < errorFields.size()) {
            context.disableDefaultConstraintViolation();
            for (String[] fields : errorFields) {
                context.buildConstraintViolationWithTemplate(message).addPropertyNode(fields[1])
                        .addConstraintViolation();
            }

            return false;
        }

        return true;
    }

}
