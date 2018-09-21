package jp.co.willwave.aca.dto.api.map;

import com.fasterxml.jackson.annotation.*;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "height",
        "width",
        "weight"
})
public class Regulation {

    @JsonProperty("height")
    private Object height;
    @JsonProperty("width")
    private Object width;
    @JsonProperty("weight")
    private Object weight;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("height")
    public Object getHeight() {
        return height;
    }

    @JsonProperty("height")
    public void setHeight(Object height) {
        this.height = height;
    }

    @JsonProperty("width")
    public Object getWidth() {
        return width;
    }

    @JsonProperty("width")
    public void setWidth(Object width) {
        this.width = width;
    }

    @JsonProperty("weight")
    public Object getWeight() {
        return weight;
    }

    @JsonProperty("weight")
    public void setWeight(Object weight) {
        this.weight = weight;
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
