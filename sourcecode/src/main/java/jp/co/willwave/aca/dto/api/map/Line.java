package jp.co.willwave.aca.dto.api.map;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "latlon"
})
public class Line {

    @JsonProperty("latlon")
    private List<Double> latlon = null;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("latlon")
    public List<Double> getLatlon() {
        return latlon;
    }

    @JsonProperty("latlon")
    public void setLatlon(List<Double> latlon) {
        this.latlon = latlon;
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
