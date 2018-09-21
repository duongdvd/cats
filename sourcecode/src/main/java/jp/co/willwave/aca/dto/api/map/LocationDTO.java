package jp.co.willwave.aca.dto.api.map;

import lombok.Data;

import java.util.Objects;

@Data
public class LocationDTO {
    private String latitude;
    private String longtitude;

    public LocationDTO() {
    }

    public LocationDTO(String latitude, String longtitude) {
        this.latitude = latitude;
        this.longtitude = longtitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LocationDTO that = (LocationDTO) o;
        return Objects.equals(latitude, that.latitude)
                && Objects.equals(longtitude, that.longtitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longtitude);
    }
}