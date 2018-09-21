package jp.co.willwave.aca.model.enums;

public enum Type {
    LONG(0), INTEGER(1), STRING(2), DATE(3), BOOLEAN(4);

    private Integer type;

    Type(Integer type) {
        this.type = type;
    }

    public boolean isLong() {
        return this.type.equals(LONG.type);
    }

    public boolean isInteger() {
        return this.type.equals(INTEGER.type);
    }

    public boolean isString() {
        return this.type.equals(STRING.type);
    }

    public boolean isDate() {
        return this.type.equals(DATE.type);
    }

    public boolean isBoolean() {
        return this.type.equals(BOOLEAN.type);
    }

    @Override
    public String toString() {
        return super.toString().toUpperCase();
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
