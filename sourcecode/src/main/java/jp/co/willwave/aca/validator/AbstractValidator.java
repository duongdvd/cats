package jp.co.willwave.aca.validator;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.util.Arrays;
import java.util.List;

public abstract class AbstractValidator {
    /**
     * 検証対象外とするかどうかを判断するフィールド名
     */
    protected String[] decisions = {};

    /**
     * decisionsの値がいずれかの値と等しい場合、検証対象外
     */
    protected String[] decisionValues = {};

    /**
     * 検証対象外とするかどうかを判断
     *
     * @param value
     * @return
     */
    protected boolean isExcludable(Object value) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(value);
        List<String> decisionValueList = Arrays.asList(decisionValues);
        for (String decision : decisions) {
            Object fieldValue = beanWrapper.getPropertyValue(decision);
            if (decisionValueList.contains(fieldValue)) {
                // 検証対象外
                return true;
            }
        }

        return false;
    }
}
