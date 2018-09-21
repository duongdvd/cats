package jp.co.willwave.aca.constants;

public enum ConfigEnum {

    NOTIFICATION_TIME("notificationTime"),
    NOTIFICATION_EMAIL("notificationEmail"),
    MOBILE_ICON("mobileIcon"),
    CUSTOMER_ICON("customerIcon"),
    TIME_MESSAGE("timeMessage"),
    TRAVEL_TIME_ALERT("travelTimeAlert"),
    DISTANCE_FINISHED("distanceFinished"),
    START_END_POINT_COLOR("startEndPointColor"),
    ARRIVED_POINT("arrivedPoint"),
    NOT_ARRIVED_POINT("notArrivedPoint"),
    MAX_LENGTH_INPUT_TEXT("maxLengthInputText");

    private String value;

    ConfigEnum(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.getValue();
    }
}
