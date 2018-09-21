package jp.co.willwave.aca.model.enums;

public enum LogsType {
    ROUTE_DETAIL(1), MESSAGE(2), SAFETY_CONFIRM(3), EMERGENCY(4), MESSAGE_ADMIN(5), MESSAGE_DEVICE(6),
    CALL_ADMIN_SUCCESS(7), CALL_DEVICE_SUCCESS(8), MISS_CALL_ADMIN(9), MISS_CALL_DEVICE(10);;

    private int type;

    LogsType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
