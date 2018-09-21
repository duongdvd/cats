package jp.co.willwave.aca.model.enums;

public enum DeleteFlg {
    UN_DELETE(0), DELETE(1);
    private Integer flag;

    DeleteFlg(Integer flag) {
        this.flag = flag;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }
}
