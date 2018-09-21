package jp.co.willwave.aca.dto.api;

import jp.co.willwave.aca.model.enums.LogsType;
import lombok.Data;

import java.util.Date;

@Data
public class TimeLineDTO {
    private String title;
    private String content;
    private Date orderTime;
    private LogsType type;
    private Long idObject;
    private Date timeIn;
    private Date timeOut;
    private String iconPath;

}
