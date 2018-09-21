package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.config.QuickBlox;
import jp.co.willwave.aca.constants.CarStatus;
import jp.co.willwave.aca.constants.RunningStatus;
import jp.co.willwave.aca.dto.api.AjaxResponseBody;
import jp.co.willwave.aca.dto.api.AllRouteDTO;
import jp.co.willwave.aca.dto.api.CarDetailDTO;
import jp.co.willwave.aca.dto.api.CarMapDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.service.CarManageService;
import jp.co.willwave.aca.utilities.SessionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CarManagementController extends AbstractController {

	private final CarManageService carManageService;

	private final QuickBlox quickBlox;

	@Autowired
	public CarManagementController(CarManageService carManageService, QuickBlox quickBlox) {
		this.carManageService = carManageService;
		this.quickBlox = quickBlox;
	}

	@GetMapping("/ws/getCarList")
	public ResponseEntity<?> getCarList(@RequestParam(name = "divisionId", required = false) Long divisionId) throws CommonException {
		AjaxResponseBody response = new AjaxResponseBody();

		UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
		Integer roleId = userInfo.getRoleId();

		Map<List<DivisionsEntity>, List<CarMapDTO>> carMap = getCarListByDivisionAndUserRole(
							divisionId, userInfo.getId(), roleId, CarStatus.ONLINE, RunningStatus.RUNNING);
		Map.Entry<List<DivisionsEntity>, List<CarMapDTO>> carMapEntry = carMap.entrySet().iterator().next();
		List<CarMapDTO> carMapDTOList = carMapEntry.getValue();
		response.setCarMapDTOList(carMapDTOList);
		response.setSuccess(!CollectionUtils.isEmpty(carMapDTOList));
		if (!response.isSuccess()) {
			response.setMsg(messageSource.get(Constant.ErrorCode.DEVICES_NOT_FOUND).getContent());
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping("/ws/getCarDetail")
	public ResponseEntity<?> getCarDetail(@RequestParam(name = "deviceId", required = false) Long deviceId) throws
		CommonException {
		AjaxResponseBody response = new AjaxResponseBody();

		if (deviceId != null) {
			CarDetailDTO carInfo = carManageService.getCarDetailInfo(deviceId);
			if (carInfo != null) {
				response.setCarDetailDTO(carInfo);
				response.setSuccess(true);
			}
		}

		if (!response.isSuccess()) {
			response.setMsg(messageSource.get(Constant.ErrorCode.DEVICES_NOT_FOUND).getContent());
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping("/ws/getRouteDetail")
	public ResponseEntity<?> getDetailRoute(@RequestParam(name = "deviceId", required = false) Long deviceId) {
		AjaxResponseBody response = new AjaxResponseBody();

		if (deviceId != null) {
			AllRouteDTO allRouteDTO = carManageService.getAllRouteDetailByDevice(deviceId, RunningStatus.RUNNING);
			if (allRouteDTO.getRouteActualDetail() != null) {
				response.setAllRouteDTO(allRouteDTO);
				response.setSuccess(true);
			}
		}

		if (!response.isSuccess()) {
			response.setMsg(messageSource.get(Constant.ErrorCode.ROUTE_NOT_FOUND).getContent());
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping(value = "/carStatusMapView")
	public String viewCarMap(Model model) throws CommonException {
		UserInfo userInfo = (UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO);
		Integer roleId = userInfo.getRoleId();

		Map<List<DivisionsEntity>, List<CarMapDTO>> carMap = getCarListByDivisionAndUserRole(
			              userInfo.getDivisionsId(), userInfo.getId(), roleId, CarStatus.ONLINE, RunningStatus.RUNNING);

		Map.Entry<List<DivisionsEntity>, List<CarMapDTO>> carMapEntry = carMap.size() > 0 ? carMap.entrySet().iterator().next() : null;
		List<DivisionsEntity> divisionsEntities = carMapEntry != null ? carMapEntry.getKey() : new ArrayList<>();
        List<CarMapDTO> lstCarMapDTO = carMapEntry != null ? carMapEntry.getValue() : new ArrayList<>();

		model.addAttribute("divisions", divisionsEntities);
		model.addAttribute("userLoginDevisionId", userInfo.getDivisionsId());
		model.addAttribute("carList", lstCarMapDTO);
		model.addAttribute("quickBloxConfig", quickBlox);

		return "car/carStatus";
	}

	/*
	 * Get Car List from division Id.
	 * In case user login role is operator => only load car list managed by that user.
	 * Else load all car list of this division and its children divisions.
	 */
	private Map<List<DivisionsEntity>, List<CarMapDTO>> getCarListByDivisionAndUserRole(
		Long divisionId, Long userId, Integer roleId, CarStatus carStatus, RunningStatus status) throws CommonException {
		Map<List<DivisionsEntity>, List<CarMapDTO>> carMap = new HashMap<>();
		if (UserRole.OPERATOR.getRole().equals(roleId)) {
			carMap = carManageService.getCarListManagedByOperator(divisionId, userId, carStatus, status);
		} else {
			carMap = carManageService.getCarListOfViewer(divisionId, carStatus, status);
		}

		return carMap;
	}
}
