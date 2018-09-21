package jp.co.willwave.aca.dto.api.map;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "time",
        "linkOffset",
        "linkLength"
})
public class DetailedTime {

    @JsonProperty("time")
    private Double time;
    @JsonProperty("linkOffset")
    private Long linkOffset;
    @JsonProperty("linkLength")
    private Long linkLength;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("time")
    public Double getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(Double time) {
        this.time = time;
    }

    @JsonProperty("linkOffset")
    public Long getLinkOffset() {
        return linkOffset;
    }

    @JsonProperty("linkOffset")
    public void setLinkOffset(Long linkOffset) {
        this.linkOffset = linkOffset;
    }

    @JsonProperty("linkLength")
    public Long getLinkLength() {
        return linkLength;
    }

    @JsonProperty("linkLength")
    public void setLinkLength(Long linkLength) {
        this.linkLength = linkLength;
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
