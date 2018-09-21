package jp.co.willwave.aca.dto.api;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ConfigsDTO {
    private Map<String, Object> appConfig = new HashMap<>();
}
