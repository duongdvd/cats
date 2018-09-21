package jp.co.willwave.aca.model.enums;

public enum Channel {
    SYSTEM_ADMIN(0), NORMAL_USER(1);
    private Integer value;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    Channel(Integer value) {
        this.value = value;
    }

    public boolean isSystemAdmin() {
        return Channel.SYSTEM_ADMIN.value.equals(this.value);
    }

    public boolean isNormalUser() {
        return Channel.NORMAL_USER.value.equals(this.value);
    }
}
