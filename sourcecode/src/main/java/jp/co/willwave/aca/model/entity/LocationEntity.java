package jp.co.willwave.aca.model.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * locationテーブルエンティティ
 *
 * @author WillWave Co., Ltd.
 */
@Data
public class LocationEntity {
    private Long seq;
    private Integer carNo;
    private Timestamp locationDt;
    private String locationLat;
    private String locationLng;
}
