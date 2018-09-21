package jp.co.willwave.aca.dto.api;

import lombok.Data;

import java.util.List;

@Data
public class RouteDetailMessageDTO {
    private Long id;
    private List<MessageDTO> messages;
}
