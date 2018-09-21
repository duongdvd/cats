package jp.co.willwave.aca.model;

import java.util.ArrayList;
import java.util.List;

public class Condition {
    private String name;
    private String label;
    private String operator;
    private List<Object> valueList = new ArrayList<>();

    public Condition(String name, String operator, Object value) {
        this.name = name;
        this.operator = operator;
        valueList.add(value);
    }

    public Condition(String name, String operator, List values) {
        this.name = name;
        this.operator = operator;
        valueList.addAll(values);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public List<Object> getValueList() {
        return valueList;
    }

    public void setValueList(List<Object> valueList) {
        this.valueList = valueList;
    }
}
