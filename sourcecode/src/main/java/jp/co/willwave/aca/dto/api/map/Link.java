package jp.co.willwave.aca.dto.api.map;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "roadType",
        "tollFlag",
        "toll",
        "line",
        "distance",
        "guidance",
        "linkID",
        "facilityName",
        "facilityInfo",
        "regulation"
})
public class Link {

    @JsonProperty("roadType")
    private String roadType;
    @JsonProperty("tollFlag")
    private Boolean tollFlag;
    @JsonProperty("toll")
    private Long toll;
    @JsonProperty("line")
    private Line line;
    @JsonProperty("distance")
    private Long distance;
    @JsonProperty("guidance")
    private Object guidance;
    @JsonProperty("linkID")
    private String linkID;
    @JsonProperty("facilityName")
    private List<Object> facilityName = null;
    @JsonProperty("facilityInfo")
    private List<Object> facilityInfo = null;
    @JsonProperty("regulation")
    private Regulation regulation;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("roadType")
    public String getRoadType() {
        return roadType;
    }

    @JsonProperty("roadType")
    public void setRoadType(String roadType) {
        this.roadType = roadType;
    }

    @JsonProperty("tollFlag")
    public Boolean getTollFlag() {
        return tollFlag;
    }

    @JsonProperty("tollFlag")
    public void setTollFlag(Boolean tollFlag) {
        this.tollFlag = tollFlag;
    }

    @JsonProperty("toll")
    public Long getToll() {
        return toll;
    }

    @JsonProperty("toll")
    public void setToll(Long toll) {
        this.toll = toll;
    }

    @JsonProperty("line")
    public Line getLine() {
        return line;
    }

    @JsonProperty("line")
    public void setLine(Line line) {
        this.line = line;
    }

    @JsonProperty("distance")
    public Long getDistance() {
        return distance;
    }

    @JsonProperty("distance")
    public void setDistance(Long distance) {
        this.distance = distance;
    }

    @JsonProperty("guidance")
    public Object getGuidance() {
        return guidance;
    }

    @JsonProperty("guidance")
    public void setGuidance(Object guidance) {
        this.guidance = guidance;
    }

    @JsonProperty("linkID")
    public String getLinkID() {
        return linkID;
    }

    @JsonProperty("linkID")
    public void setLinkID(String linkID) {
        this.linkID = linkID;
    }

    @JsonProperty("facilityName")
    public List<Object> getFacilityName() {
        return facilityName;
    }

    @JsonProperty("facilityName")
    public void setFacilityName(List<Object> facilityName) {
        this.facilityName = facilityName;
    }

    @JsonProperty("facilityInfo")
    public List<Object> getFacilityInfo() {
        return facilityInfo;
    }

    @JsonProperty("facilityInfo")
    public void setFacilityInfo(List<Object> facilityInfo) {
        this.facilityInfo = facilityInfo;
    }

    @JsonProperty("regulation")
    public Regulation getRegulation() {
        return regulation;
    }

    @JsonProperty("regulation")
    public void setRegulation(Regulation regulation) {
        this.regulation = regulation;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
