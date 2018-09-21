package jp.co.willwave.aca.web.restController;


import jp.co.willwave.aca.constants.RunningStatus;
import jp.co.willwave.aca.dto.api.quickblox.QuickBloxUserResponse;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.entity.CallLogsEntity;
import jp.co.willwave.aca.service.CallLogsService;
import jp.co.willwave.aca.service.QuickBloxService;
import jp.co.willwave.aca.service.RoutesService;
import jp.co.willwave.aca.web.form.CallLogForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;

@Controller
@RequestMapping("/quickblox")
public class QuickBloxController {

    private final QuickBloxService quickBloxService;
    private final CallLogsService callLogsService;
    private final RoutesService routesService;

    @Autowired
    public QuickBloxController(QuickBloxService quickBloxService, CallLogsService callLogsService, RoutesService routesService) {
        this.quickBloxService = quickBloxService;
        this.callLogsService = callLogsService;
        this.routesService = routesService;
    }

    @GetMapping("/token")
    public String getToken() throws IOException {

        return quickBloxService.getTokenQuickBlox();

    }

    @GetMapping("/user/create")
    public QuickBloxUserResponse createUser() {
        String companyCode = quickBloxService.generateQuickBloxTagByCompanyId(3L);

        return quickBloxService.createQuickBloxUser("company" + 3L, "D_" + 3L);

    }

    @PostMapping("/call/end")
    public ResponseEntity<?> endCall(@RequestBody CallLogForm callLogForm) throws CommonException {
        CallLogsEntity callLogsEntity = new CallLogsEntity(callLogForm);

        // Get latest route detail id of running route by deviceId.
        Long lastRouteId = routesService.getLastRouteDetailIdByDevice(callLogForm.getDeviceId(), RunningStatus.RUNNING);

        callLogsEntity.setRouteDetailId(lastRouteId);
        callLogsService.writeCallLog(callLogsEntity);

        return ResponseEntity.ok(new ArrayList<Message>());
    }
}
