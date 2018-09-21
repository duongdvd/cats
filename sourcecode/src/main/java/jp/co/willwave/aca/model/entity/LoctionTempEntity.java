package jp.co.willwave.aca.model.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class LoctionTempEntity {
    private Long carId;
    private Timestamp locationTime;
    private String lat;
    private String lng;
    private Long seq;
}
