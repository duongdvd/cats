package jp.co.willwave.aca.dto.api;

import lombok.Data;

@Data
public class ConfigsMapDTO {
    private String key;
    private String value;

    public ConfigsMapDTO() {
    }

    public ConfigsMapDTO(String key, String value) {
        this.key = key;
        this.value = value;
    }
}
