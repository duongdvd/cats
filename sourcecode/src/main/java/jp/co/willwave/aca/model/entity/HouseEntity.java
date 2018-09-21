package jp.co.willwave.aca.model.entity;

import lombok.Data;

import java.sql.Timestamp;

/**
 * houseテーブルエンティティ
 *
 * @author WillWave Co., Ltd.
 */
@Data
public class HouseEntity {
    private Integer houseNo;
    private String name;
    private String address;
    private String lat;
    private String lng;
    private Boolean finishFlg;
    private Timestamp createDt;
    private Timestamp updateDt;
}
