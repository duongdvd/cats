package jp.co.willwave.aca.dto.api;

import lombok.Data;

import java.util.Date;


@Data
public class ConditionSearchMessage {
    private Long devicesId;
    private Date date;
    private String content;
}
