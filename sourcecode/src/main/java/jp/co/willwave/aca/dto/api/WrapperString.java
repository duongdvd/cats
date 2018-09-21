package jp.co.willwave.aca.dto.api;

import lombok.Data;

@Data
public class WrapperString {
    private String value;

    public WrapperString() {
    }

    public WrapperString(String value) {
        this.value = value;
    }
}
