package jp.co.willwave.aca.model.enums;

public enum CustomerType {
    CUSTOMER(1), GARAGE(2);

    private Integer type;

    CustomerType(Integer type) {
        this.type = type;
    }

    public static CustomerType getCustomerType(Integer type) {
        return (type == null || CUSTOMER.getType().equals(type))? CUSTOMER : GARAGE;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
