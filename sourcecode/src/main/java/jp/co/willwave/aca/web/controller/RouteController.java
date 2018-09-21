package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.constants.AddEditModeEnum;
import jp.co.willwave.aca.dto.api.CarDetailDTO;
import jp.co.willwave.aca.dto.api.RouteDTO;
import jp.co.willwave.aca.dto.api.RouteDetailDTO;
import jp.co.willwave.aca.dto.api.RoutesDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.exception.LogicWebException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.CustomersEntity;
import jp.co.willwave.aca.model.entity.RoutesEntity;
import jp.co.willwave.aca.model.enums.CustomerType;
import jp.co.willwave.aca.service.*;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.DateUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.AddEditRouteForm;
import jp.co.willwave.aca.web.form.RouteCustomerDetailForm;
import jp.co.willwave.aca.web.form.SearchRouteForm;
import jp.co.willwave.aca.web.form.route.DeviceCarDTO;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static jp.co.willwave.aca.constants.DateConstant.DATE_FORMAT;
import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

@Controller
public class RouteController extends AbstractController {

    private final RoutesService routesService;

    private final RouteDetailService routeDetailService;

    private final CustomersService customerService;

    private final DevicesService devicesService;

    private final CarManageService carManageService;

    private long deviceId;


    @Autowired
    public RouteController(RoutesService routesService, RouteDetailService routeDetailService, CustomersService customerService, DevicesService devicesService, CarManageService carManageService) {
        this.routesService = routesService;
        this.routeDetailService = routeDetailService;
        this.customerService = customerService;
        this.devicesService = devicesService;
        this.carManageService = carManageService;
    }

    @RequestMapping(value = {"/routeList"}, method = RequestMethod.GET)
    public String routeView(ModelMap model, Integer offset, Integer maxResults) throws ParseException {
        SearchRouteForm searchRouteForm = (SearchRouteForm) SessionUtil.getAttribute("SearchForm");
        if (searchRouteForm == null) {
            searchRouteForm = new SearchRouteForm();
            searchRouteForm.setSpecificDate(DateUtil.getCurrentDate(DATE_FORMAT));
        }

        Long userLoginId = getLoginUserId();
        List<RouteDTO> routeDTOList = routesService.searchPlanRouteList(userLoginId, searchRouteForm, offset, maxResults, true);
        Long totalCount = routesService.countPlanRoutes(userLoginId, searchRouteForm, offset, maxResults, false);

        //set delete and change status permission, used to disable the buttons on the client
        routesService.setDeleteAndChangeStatusPermission(routeDTOList);

        //list route id (filter form)
        List<Long> routeIdList = routesService.getRoutePlanIdByCreator(userLoginId);
        List<Message> messageList = ConversionUtil.castList(
                                                SessionUtil.getAttribute(Constant.Session.MESSAGES), Message.class);


        setAddEditResultIntoModelMap(model);

        Date currentDate = new Date();
        model.addAttribute("SearchRouteForm", searchRouteForm);
        model.addAttribute("routeDTOList", routeDTOList);
        model.addAttribute("count", totalCount);
        model.addAttribute("offset", offset);
        model.addAttribute("routeIdList", routeIdList);
        model.addAttribute(Constant.Session.MESSAGES, messageList);
        model.addAttribute("currentDate", currentDate);
        SessionUtil.setAttribute(Constant.Session.MESSAGES, null);
        SessionUtil.setAttribute("deleteResult", null);
        SessionUtil.setAttribute("addEditResult", null);
        return "route/routeList";
    }

    @RequestMapping(value = "/searchRoute", method = RequestMethod.GET)
    public String search(@Valid @ModelAttribute("SearchRouteForm") SearchRouteForm searchRouteForm, BindingResult bindingResult, Integer offset, Integer maxResults) {
        logger.info("search route list");
        List<Message> messages = validatorUtil.validate(bindingResult);
        List<Message> validateForm = routesService.validateDateTimeForm(searchRouteForm);
        if (!CollectionUtils.isEmpty(validateForm)) {
            messages.addAll(validateForm);
        }
        if (!CollectionUtils.isEmpty(messages)) {
            SessionUtil.setAttribute("SearchForm", null);
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
        } else {
            SessionUtil.setAttribute("SearchForm", searchRouteForm);
        }

        return redirect("routeList");
    }

    @RequestMapping(value = "/searchRoutePaging", method = RequestMethod.GET)
    public String searchPaging(Integer offset, Integer maxResults) {
        logger.info("search route list paging");
        return pagingRedirectToList("routeList", offset, maxResults);
    }

    @RequestMapping(value = "/deleteRoute", method = RequestMethod.GET)
    public String deleteRoute(@RequestParam("id") Long id) throws CommonException {
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        List<Message> messageList = routesService.deleteRoute(id, userLoginId);
        if (!CollectionUtils.isEmpty(messageList)) {
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messageList);
            SessionUtil.setAttribute("deleteResult", false);
        } else {
            SessionUtil.setAttribute("deleteResult", true);
        }

        return redirect("routeList");
    }

    @RequestMapping(value = "/addRouteView", method = RequestMethod.GET)
    public String addRouteView(ModelMap model) throws CommonException {
        AddEditRouteForm addEditRouteForm = new AddEditRouteForm();

        return addOrEditRouterView(model, addEditRouteForm, AddEditModeEnum.ADD.getMode());
    }


    @RequestMapping(value = "/editRouteView", method = RequestMethod.GET)
    public String editRouteView(@RequestParam(name = "routeId", required = false) Long routeId,
                                @RequestParam(name = "fromCarStatus", required = false) Boolean fromCarStatus,
                                @RequestParam(name = "updateCarStatusRoute", required = false) Boolean updateCarStatusRoute,
                                ModelMap model) throws Exception {
        RoutesEntity route = routesService.findById(routeId);
deviceId = route.getDevicesId();
        // In case not from car status, if login user does not manage the device which used in this route => show error message.
        if ((fromCarStatus == null || !fromCarStatus) && !devicesService.checkManageDevice(SessionUtil.getLoginUser().getId(), route.getDevicesId())) {
            model.addAttribute(Constant.Session.MESSAGES, Arrays.asList(messageSource.get(Constant.ErrorCode.DEVICE_HAS_BEEN_UNASSIGNED)));
            route.setDevicesId(null);
        }

        RoutesDTO routesDTO = routesService.findRouteDetailByRouteId(routeId);

        AddEditRouteForm addEditRouteForm = ConversionUtil.mapper(route, AddEditRouteForm.class);
        //from car status
        if (fromCarStatus != null && fromCarStatus) {
            initFormFormCarStatus(model, addEditRouteForm);
        }

        List<RouteDetailDTO> routeDetails =  routesDTO.getRouteDetails();
        Triple<RouteDetailDTO, List<RouteDetailDTO>, RouteDetailDTO> routesSplitted
                = routeDetailService.splitRoutesGarageAndCustomer(routeDetails);

        addEditRouteForm.setStartGarageId(routesSplitted.getLeft().getCustomersId());
        addEditRouteForm.setEndGarageId(routesSplitted.getRight().getCustomersId());
        addEditRouteForm.setEditPermission(routesService.validateEditPermission(route) == null);
        model.addAttribute("routeDetails", routesSplitted.getMiddle());
        return addOrEditRouterView(model, addEditRouteForm, AddEditModeEnum.EDIT.getMode());
    }

    @RequestMapping(value = "/cloneRouteView", method = RequestMethod.GET)
    public String cloneRouteView(@RequestParam(name = "routeId") Long routeId, ModelMap model) throws Exception {
        RoutesEntity route = routesService.findById(routeId);
        RoutesDTO routesDTO = routesService.findRouteDetailByRouteId(routeId);
        List<RouteDetailDTO> routeDetails = routesDTO.getRouteDetails();
        if(!CollectionUtils.isEmpty(routeDetails)) {
            for (RouteDetailDTO r:routeDetails) {
                r.setId(null);
            }
        }

        Triple<RouteDetailDTO, List<RouteDetailDTO>, RouteDetailDTO> routesSplitted
                = routeDetailService.splitRoutesGarageAndCustomer(routeDetails);
        AddEditRouteForm addEditRouteForm = ConversionUtil.mapper(route, AddEditRouteForm.class);
        addEditRouteForm.setId(null);
        addEditRouteForm.setStartGarageId(routesSplitted.getLeft().getCustomersId());
        addEditRouteForm.setEndGarageId(routesSplitted.getRight().getCustomersId());
        addEditRouteForm.setName(addEditRouteForm.getName() + " - Copy");
        model.addAttribute("routeDetails", routesSplitted.getMiddle());
        return addOrEditRouterView(model, addEditRouteForm, AddEditModeEnum.CLONE.getMode());
    }

    @PostMapping(value = "/addRoute")
    public String addRoute(ModelMap model, @RequestParam(name = "mode") Integer mode,
                           @Valid @ModelAttribute("addEditRouteForm") AddEditRouteForm form,
                           BindingResult result) throws CommonException, ParseException {
        List<Message> messages = validatorUtil.validate(result);

        if (CollectionUtils.isEmpty(messages)) {
            //if login user does not manage the device which used in this route
            if (!form.getFromCarStatus()
                    && !devicesService.checkManageDevice(SessionUtil.getLoginUser().getId(), form.getDevicesId())) {
                messages.add(messageSource.get(Constant.ErrorCode.DEVICE_HAS_BEEN_UNASSIGNED));
            }

            // Validate start date time and end date time.
            Date startDate = form.getStartDate();
            Date endDate = form.getEndDate();
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.YYYY_MM_DD);
            Date currentDate = sdf.parse(sdf.format(new Date()));

            // Validate Customer
            Boolean checkCustomer = true;
            String customerHasDelete = null;
            List<Long> listCustomerToCheck = new ArrayList<>();
            Long maxVisitedOrder = routeDetailService.getVisitedOrderByRouteId(form.getId());
            listCustomerToCheck.add(form.getEndGarageId());
            if (!(maxVisitedOrder != null && maxVisitedOrder >= 0)){
                listCustomerToCheck.add(form.getStartGarageId());
            }

            List<RouteDetailDTO> listRouteDetails = RouteDetailDTO.fromEntity(routeDetailService.createCustomerListFromRouteDetailStr(form.getRouteDetails()));
            for (int i = 0; i < listRouteDetails.size(); i++){
                if (!(maxVisitedOrder != null && listRouteDetails.get(i).getVisitOrder() <= maxVisitedOrder)){
                    listCustomerToCheck.add(listRouteDetails.get(i).getCustomersId());
                }
            }

            for (int i = 0; i < listCustomerToCheck.size(); i++){
                CustomersEntity customersEntity = customerService.getCustomer(listCustomerToCheck.get(i));
                if (customersEntity != null && customersEntity.getDeleteFlg())
                {
                    checkCustomer = false;
                    customerHasDelete = customersEntity.getName();
                    break;
                }
            }

            if (startDate.before(currentDate) && !form.getFromCarStatus()) {
                messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.MUST_GREATER_THAN, new String[]{"route.startDate", "current.date"}));
            } else if (startDate.after(endDate)) {
                messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.MUST_GREATER_THAN, new String[]{"route.endDate", "route.startDate"}));
            } else if (!checkCustomer) {
                messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.FAIL_CHECK_CUSTOMER, new String[]{customerHasDelete}));
            } else {
                RoutesEntity route = ConversionUtil.mapper(form, RoutesEntity.class);
                String routeDetails = form.getRouteDetails();
                if (AddEditModeEnum.isAddMode(mode) || AddEditModeEnum.isCloneMode(mode)) {
                    routesService.createRoute(route, routeDetails, form.getMapDetails());
                } else if (form.getFromCarStatus()) {
                    messages.addAll(routesService.updateRunningRoute(route, routeDetails, form.getMapDetails()));
                } else {
                    messages.addAll(routesService.updateRoute(route, routeDetails, form.getMapDetails()));
                }

                if (CollectionUtils.isEmpty(messages) && form.getFromCarStatus()) {
                    form.setUpdateCarStatusRoute(true);
                    return addOrEditRouterView(model, form, mode);
                }

                SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
                return redirect("routeList");
            }
        }

        model.addAttribute(Constant.Session.MESSAGES, messages);
        //from car status
        if (form.getFromCarStatus()) {
            initFormFormCarStatus(model, form);
        }
        model.addAttribute("routeDetails", RouteDetailDTO.fromEntity(routeDetailService.createCustomerListFromRouteDetailStr(form.getRouteDetails())));
        return addOrEditRouterView(model, form, mode);
    }

    @RequestMapping(value = "/searchRouteDetail", method = RequestMethod.GET)
    public String searchAssignNewDevice(@ModelAttribute("SearchRouteDetailForm")
                                                RouteCustomerDetailForm routeCustomerDetailForm) {
        logger.info("search route detail list");

        SessionUtil.setAttribute("SearchRouteDetailForm", routeCustomerDetailForm);
        Long id = (Long) SessionUtil.getAttribute("routeId");
        return redirect("routeDetail?id=" + id);
    }

    @PostMapping(value = "/route/change/status/{id}")
    public ResponseEntity<List<Message>> changeStatus(@PathVariable("id") Long id) throws CommonException {
        List<Message> messages = new ArrayList<>();
        try {
            routesService.changeStatusRoute(id);
        } catch (LogicWebException e) {
            messages.add(e.getErrorMessage());
        } catch (Exception e) {
            throw new CommonException(e);
        }
        return ResponseEntity.ok(messages);
    }

    private String addOrEditRouterView(ModelMap model, AddEditRouteForm form, Integer mode) throws CommonException {
        List<DeviceCarDTO> managedDevices = devicesService.getAllActiveAssignDeviceList(getLoginUserId());

        if(form.getFromCarStatus()==true && managedDevices.size()==0){
    CarDetailDTO cart= carManageService.getCarDetailInfo(deviceId);
    DeviceCarDTO deviceCarDTO = new DeviceCarDTO();
    deviceCarDTO.setDeviceId(deviceId);
    deviceCarDTO.setLoginId(cart.getLoginId());
    deviceCarDTO.setPlateNumber((cart.getPlateNumber()));
    managedDevices.add(deviceCarDTO);
}
        List<CustomersEntity> allCustomers = customerService.getActiveCustomersByUserId(getLoginUserId());

        List<CustomersEntity> garages = new ArrayList<>();
        List<CustomersEntity> customers = new ArrayList<>();
        for (CustomersEntity customer : allCustomers) {
            if (CustomerType.GARAGE.equals(customer.getCustomerType())) {
                garages.add(customer);
            } else {
                customers.add(customer);
            }
        }

        if (form.getStartGarageId() != null)
        {
            CustomersEntity garageStart = customerService.getCustomer(form.getStartGarageId());
            if (!garages.contains(garageStart)){
                garages.add(garageStart);
            }
        }

        if (form.getEndGarageId() != null)
        {
            CustomersEntity garageEnd = customerService.getCustomer(form.getEndGarageId());
            if (!garages.contains(garageEnd)){
                garages.add(garageEnd);
            }
        }

        // Set default value of start and end garage id.
        if (!CollectionUtils.isEmpty(garages)) {
            if (form.getStartGarageId() == null) {
                form.setStartGarageId(garages.get(0).getId());
            }

            if (form.getEndGarageId() == null) {
                form.setEndGarageId(garages.get(0).getId());
            }
        }

        model.addAttribute("addEditRouteForm", form);
        model.addAttribute("managedDevices", managedDevices);
        model.addAttribute("customers", customers);
        model.addAttribute("garages", garages);
        model.addAttribute("mode", mode);

        return "route/addRoute";
    }

    /**
     * init form in case update route from car status management screen.
     * @param model
     * @param form AddEditRouteForm
     */
    private void initFormFormCarStatus(ModelMap model, AddEditRouteForm form) {
        form.setFromCarStatus(true);
        //new route plan only valid for the day of editing
        form.setStartDate(new Date());
        form.setEndDate(new Date());
        // Get max visited order of route id with current date
        Long maxVisitedOrder = routeDetailService.getVisitedOrderByRouteId(form.getId());
        model.addAttribute("maxVisitedOrder", maxVisitedOrder);
    }

    @GetMapping("/getRouteInfo/{id}")
    public ResponseEntity getRouteInfo(@PathVariable("id") Long id) {
        RoutesEntity routesEntity = routesService.findById(id);
        routesEntity = routesEntity == null ? new RoutesEntity() : routesEntity;
        return ResponseEntity.ok(routesEntity);
    }
}
