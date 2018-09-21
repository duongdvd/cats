package jp.co.willwave.aca.dto.api;

import lombok.Data;

import java.util.List;

@Data
public class AjaxResponseBody {
    private String msg;
    private AllRouteDTO allRouteDTO;
    private List<CarMapDTO> carMapDTOList;
    private CarDetailDTO carDetailDTO;
    private boolean success = false;

    public boolean isSuccess() {
        return success;
    }
}
