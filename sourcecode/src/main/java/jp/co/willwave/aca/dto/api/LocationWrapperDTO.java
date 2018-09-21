package jp.co.willwave.aca.dto.api;

import lombok.Data;

import java.util.List;

@Data
public class LocationWrapperDTO {
    private List<LocationTempDTO> locationTempDTOs;
}
