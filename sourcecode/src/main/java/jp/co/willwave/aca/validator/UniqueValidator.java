package jp.co.willwave.aca.validator;

import jp.co.willwave.aca.utilities.CommonUtil;
import lombok.Data;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UniqueValidator implements ConstraintValidator<Unique, Object> {
    private static Logger logger = Logger.getLogger(UniqueValidator.class);

    /**
     * 検証メッセージ
     */
    private String message;

    /**
     * 検証対象となるフィールド
     */
    private String field = "";

    @Override
    public void initialize(Unique constraintAnnotation) {
        logger.debug("initialize");
        message = constraintAnnotation.message();
        field = constraintAnnotation.field();
    }

    @Override
    public boolean isValid(Object values, ConstraintValidatorContext context) {
        List<Element> elements = new ArrayList<Element>();

        if (CommonUtil.isEmpty(field)) {
            return true;
        }

        if (values instanceof List<?>) {
            List<?> list = (List<?>) values;
            for (Object obj : list) {
                Object objValue = new BeanWrapperImpl(obj).getPropertyValue(field);
                if (!CommonUtil.isEmpty(objValue)) {
                    elements.add(new Element(elements.size(), String.valueOf(objValue)));
                }
            }

            Map<Object, List<Element>> map = elements.stream().collect(Collectors.groupingBy(e -> e.getValue()));
            Map<String, List<Element>> duplications = new HashMap<>();
            for (Map.Entry<Object, List<Element>> entry : map.entrySet()) {
                // 空ではなく、重複がある
                if (!CommonUtil.isEmpty(entry.getValue().get(0).getValue()) && 1 < entry.getValue().size()) {
                    duplications.put(String.valueOf(entry.getKey()), entry.getValue());
                }
            }

            if (!duplications.isEmpty()) {
                context.disableDefaultConstraintViolation();

                String text = "";
                for (Map.Entry<String, List<Element>> entry : duplications.entrySet()) {
                    entry.getKey();
                    String lines = String.join(", ", entry.getValue().stream()
                            .map(e -> String.valueOf(e.getIndex() + 1)).collect(Collectors.toList()));
                    text = String.format("%sが[%s]行目で重複しています。", message, lines);
                    context.buildConstraintViolationWithTemplate(text).addConstraintViolation();
                }

                return false;
            }
        }

        return true;
    }

    @Data
    public class Element {
        private int index;
        private String value;

        public Element(int index, String value) {
            this.index = index;
            this.value = value;
        }
    }

}
