package jp.co.willwave.aca.dto.api.map;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "toll",
        "distance",
        "time",
        "link",
        "detailedTime"
})
public class Route {

    @JsonProperty("toll")
    private Long toll;
    @JsonProperty("distance")
    private Long distance;
    @JsonProperty("time")
    private Long time;
    @JsonProperty("link")
    private List<Link> link = null;
    @JsonProperty("detailedTime")
    private List<DetailedTime> detailedTime = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("toll")
    public Long getToll() {
        return toll;
    }

    @JsonProperty("toll")
    public void setToll(Long toll) {
        this.toll = toll;
    }

    @JsonProperty("distance")
    public Long getDistance() {
        return distance;
    }

    @JsonProperty("distance")
    public void setDistance(Long distance) {
        this.distance = distance;
    }

    @JsonProperty("time")
    public Long getTime() {
        return time;
    }

    @JsonProperty("time")
    public void setTime(Long time) {
        this.time = time;
    }

    @JsonProperty("link")
    public List<Link> getLink() {
        return link;
    }

    @JsonProperty("link")
    public void setLink(List<Link> link) {
        this.link = link;
    }

    @JsonProperty("detailedTime")
    public List<DetailedTime> getDetailedTime() {
        return detailedTime;
    }

    @JsonProperty("detailedTime")
    public void setDetailedTime(List<DetailedTime> detailedTime) {
        this.detailedTime = detailedTime;
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
