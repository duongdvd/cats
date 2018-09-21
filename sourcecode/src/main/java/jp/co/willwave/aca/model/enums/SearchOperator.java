package jp.co.willwave.aca.model.enums;

public enum SearchOperator {
    EQUAL("=="),
    NOT_EQUAL("!="),
    GREATER(">"),
    GREATER_OR_EQUAL(">="),
    LESS("<"),
    LESS_OR_EQUAL("<="),
    LIKE("like"),
    NOT_LIKE("not like"),
    IS_NULL("is null"),
    IS_NOT_NULL("is not null"),
    IS_EMPTY("is empty"),
    IS_NOT_EMPTY("is not empty"),
    IN_VALUES("in value");

    public static final String C_IS_NULL = "is null";
    public static final String C_IS_NOT_NULL = "is not null";
    public static final String C_IS_EMPTY = "is empty";
    public static final String C_IS_NOT_EMPTY = "is not empty";
    public static final String C_EQUAL = "equal";
    public static final String C_NOT_EQUAL = "not equal";
    public static final String C_GREATER = "greater than";
    public static final String C_GREATER_OR_EQUAL = "greater than or equal";
    public static final String C_LESS = "less than";
    public static final String C_LESS_OR_EQUAL = "less than or equal";
    public static final String C_LIKE = "like";
    public static final String C_NOT_LIKE = "not like";
    public static final String C_IN_VALUES = "in value";
    public static final String C_PERCENT = "%";

    private String value;

    SearchOperator(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}