package jp.co.willwave.aca.dto.api;

import lombok.Data;

import java.util.Date;

@Data
public class RouteActualDTO {
    private Long id;
    private String name;
    private String description;
    private Date actualDate;
}
