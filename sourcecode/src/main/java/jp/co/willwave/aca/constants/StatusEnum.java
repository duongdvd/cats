package jp.co.willwave.aca.constants;

public enum StatusEnum {
    ACTIVE(1), INACTIVE(0);

    private final Integer value;

    StatusEnum(final Integer newValue) {
        value = newValue;
    }

    public Integer getValue() {
        return value;
    }

    public static StatusEnum oppositeStatus(Integer value) {
        if (StatusEnum.ACTIVE.getValue().equals(value)) {
            return StatusEnum.INACTIVE;
        } else {
            return StatusEnum.ACTIVE;
        }
    }

    public static boolean contains(final int test) {

        for (final StatusEnum value : StatusEnum.values()) {
            if (value.getValue() == test) {
                return true;
            }
        }
        return false;

    }
}
