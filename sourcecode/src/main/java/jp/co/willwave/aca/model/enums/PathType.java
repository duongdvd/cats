package jp.co.willwave.aca.model.enums;

public enum PathType {
    MATCH(0), REGEX(1);

    private Integer type;

    PathType(Integer type) {
        this.type = type;
    }

    public Integer getPathType() { return this.type; }

    public void setPathType(Integer type) {
        this.type = type;
    }

}
