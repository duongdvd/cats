package jp.co.willwave.aca.dto.api;

import lombok.Data;

@Data
public class FinishRouteDetailMessageDTO {
    private Long id;
    private String message;

    public FinishRouteDetailMessageDTO() {
    }

    public FinishRouteDetailMessageDTO(Long id, String message) {
        this.id = id;
        this.message = message;
    }
}
