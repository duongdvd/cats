package jp.co.willwave.aca.web.form.message;

import jp.co.willwave.aca.model.enums.LogsType;
import lombok.Data;

import java.util.Date;

@Data
public class LogsDTO {
    private Long id;
    private LogsType type;
    private Date time;

    public LogsDTO(Long id, LogsType type) {
        this.id = id;
        this.type = type;
    }

    public LogsDTO(Long id, LogsType type, Date time) {
        this.id = id;
        this.type = type;
        this.time = time;
    }

    public LogsDTO() {
    }
}
