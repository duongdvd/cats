package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.constants.StatusEnum;
import jp.co.willwave.aca.constants.UploadConstant;
import jp.co.willwave.aca.dto.api.AssignDeviceDTO;
import jp.co.willwave.aca.dto.api.DeviceDTO;
import jp.co.willwave.aca.dto.api.DivisionDTO;
import jp.co.willwave.aca.dto.api.EditDeviceDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.exception.LogicWebException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.service.CompanyUsageService;
import jp.co.willwave.aca.service.DevicesService;
import jp.co.willwave.aca.service.DivisionService;
import jp.co.willwave.aca.service.UserService;
import jp.co.willwave.aca.utilities.CommonUtil;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.FileUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.*;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

@Controller
public class DeviceController extends AbstractController {

    private static final String SEARCH_DEVICE_FORM = "searchDeviceForm";
    public static final String SEARCH_ASSIGN_DEVICE_FORM = "searchAssignDeviceForm";
    private final DevicesService devicesService;
    private final DivisionService divisionService;
    private final FileUtil<DeviceExportForm> deviceExportFormFileUtil;
    private final CompanyUsageService companyUsageService;
    private final UserService userService;

    @Autowired
    public DeviceController(DevicesService devicesService, DivisionService divisionService, FileUtil<DeviceExportForm> deviceExportFormFileUtil, CompanyUsageService companyUsageService, UserService userService) {
        this.devicesService = devicesService;
        this.divisionService = divisionService;
        this.deviceExportFormFileUtil = deviceExportFormFileUtil;
        this.companyUsageService = companyUsageService;
        this.userService = userService;
    }

    @RequestMapping(value = {"/deviceList"}, method = RequestMethod.GET)
    public String deviceView(ModelMap model, Integer offset, Integer maxResults) throws CommonException {

        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        Long divisionIdUser = divisionService.findOnlyIdByUserId(userLoginId);

        List<DeviceDTO> deviceDTOList = devicesService.getDeviceList(divisionIdUser, offset, maxResults, true, false);
        List<Long> deviceIdList = new ArrayList<>();
        HashSet<DivisionDTO> divisionIdList = new HashSet<>();
        if (!CollectionUtils.isEmpty(deviceDTOList)) {
            for (DeviceDTO deviceDTO : deviceDTOList
                    ) {
                deviceIdList.add(deviceDTO.getDeviceId());
                DivisionDTO divitionDTO = new DivisionDTO();
                divitionDTO.setDivisionId(deviceDTO.getDivisionId());
                if (StringUtils.isNotBlank(deviceDTO.getDivisionName())) {
                    divitionDTO.setDivisionName(deviceDTO.getDivisionName());
                }
                divisionIdList.add(divitionDTO);
            }
        }

        Integer count = 0;
        SearchDeviceForm searchDeviceForm = (SearchDeviceForm) SessionUtil.getAttribute(SEARCH_DEVICE_FORM);
        if (searchDeviceForm == null) {
            searchDeviceForm = new SearchDeviceForm();
            deviceDTOList = devicesService.getDeviceList(divisionIdUser, offset, maxResults, false, false);
            count = devicesService.getDeviceList(divisionIdUser, offset, maxResults, true, false).size();
        } else {
            deviceDTOList = devicesService.searchDeviceList(searchDeviceForm, offset, maxResults, false, false);
            count = devicesService.searchDeviceList(searchDeviceForm, offset, maxResults, true, false).size();
        }

        List<Message> messageList = ConversionUtil.castList(SessionUtil.getAttribute(Constant.Session.MESSAGES), Message.class);
        setAddEditResultIntoModelMap(model);

        model.addAttribute("deviceList", deviceDTOList);
        model.addAttribute(SEARCH_DEVICE_FORM, searchDeviceForm);
        model.addAttribute("deviceIdList", deviceIdList);
        model.addAttribute("divisionIdList", divisionIdList);
        model.addAttribute("count", count);
        model.addAttribute("offset", offset);
        model.addAttribute(Constant.Session.MESSAGES, messageList);

        SessionUtil.setAttribute(Constant.Session.MESSAGES, null);
        SessionUtil.setAttribute("deleteResult", null);
        SessionUtil.setAttribute("addEditResult", null);
        return "device/devices";
    }

    @RequestMapping(value = "/searchDevices", method = RequestMethod.GET)
    public String search(@ModelAttribute("searchDeviceForm") SearchDeviceForm searchDeviceForm, Integer offset, Integer maxResults) {
        logger.info("search device list");

        SessionUtil.setAttribute(SEARCH_DEVICE_FORM, searchDeviceForm);
        return redirect("deviceList");
    }

    @PostMapping(value = "/device/change/status/{id}")
    public ResponseEntity<List<Message>> changeStatus(@PathVariable("id") Long id) throws CommonException {
        List<Message> messages = new ArrayList<>();
        try {
            devicesService.changeStatusDevice(id);
        } catch (LogicWebException e) {
            messages.add(e.getErrorMessage());
        } catch (Exception e) {
            throw new CommonException(e);
        }
        return ResponseEntity.ok(messages);
    }

    @RequestMapping(value = {"/exportDeviceList"}, method = RequestMethod.GET)
    public void exportDeviceList(HttpServletResponse response) throws IOException {

        SearchDeviceForm searchDeviceForm = null;
        try {
            searchDeviceForm = (SearchDeviceForm) SessionUtil.getAttribute(SEARCH_DEVICE_FORM);
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

        List<DeviceExportForm> deviceExportFormList;
        if (searchDeviceForm == null) {
            Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
            Long divivionIdUser = divisionService.findOnlyIdByUserId(userLoginId);
            deviceExportFormList = devicesService.exportDeviceList(divivionIdUser);

        } else {
            deviceExportFormList = devicesService.exportDeviceListByCondition(searchDeviceForm);
        }

        deviceExportFormFileUtil.downloadCsvFile(response, deviceExportFormList, UploadConstant.devicesForm, "deviceList.csv");

    }


    //TODO
    @RequestMapping(value = {"/importDeviceList"}, method = RequestMethod.POST)
    public String importDevice(@RequestParam("fileInput") MultipartFile file,
                               @RequestParam(value = "type", required = false) Integer type, Model model)
            throws CommonException {

        List<Message> messages = new ArrayList<>();
        if (!file.isEmpty()) {
           messages = devicesService.checkImportFile(file);

            if (CollectionUtils.isEmpty(messages)) {

                // Read imported file.
                List<DeviceExportForm> deviceExportFormList = deviceExportFormFileUtil.readCsv(file, DeviceExportForm.class);

                if (!CollectionUtils.isEmpty(deviceExportFormList)) {
                    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
                    Validator validator = factory.getValidator();

                    Map<Integer, Set<ConstraintViolation<DeviceExportForm>>> errorMap = new HashMap<>();
                    Set<ConstraintViolation<DeviceExportForm>> violations;
                    int i = 2;
                    for (DeviceExportForm deviceExportForm : deviceExportFormList) {
                        violations = validator.validate(deviceExportForm);
                        if (!CollectionUtils.isEmpty(violations)) {
                            errorMap.put(i, violations);
                        }
                        i++;
                    }
                    if (!CollectionUtils.isEmpty(errorMap.keySet())) {
                        for (Integer lineCsv : errorMap.keySet()
                                ) {
                            messages.add(messageSource.getMessage(Constant.ErrorCode.CSV_ERROR, new String[]{String.valueOf(lineCsv)}));
                        }
                        SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
                    } else {
                        messages = devicesService.importDevice(deviceExportFormList);
                        if(!CollectionUtils.isEmpty(messages)) {
                            SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
                        } else {
                            Long divisionId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getDivisionsId();

                            DivisionsEntity divisionsEntity = divisionService.findById(divisionId);
                            Long companyId = Long.valueOf(CommonUtil.getCompanyID(divisionsEntity));

                            companyUsageService.updateCompanyUsage(companyId);
                            SessionUtil.setAttribute("addEditResult", true);
                        }
                    }
                }

            } else {
                SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
            }
        } else {
            messages.add(messageSource.getMessage(Constant.ErrorCode.NOT_EMPTY, new String[]{"file.upload"}));
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
        }
        return redirect("deviceList");
    }


    @RequestMapping(value = "/searchPaging", method = RequestMethod.GET)
    public String searchPaging(Integer offset, Integer maxResults) {
        logger.info("search device list paging");
        return pagingRedirectToList("deviceList", offset, maxResults);
    }

    @RequestMapping(value = "/deleteDevice", method = RequestMethod.GET)
    public String deleteDevice(@RequestParam("id") Long id) throws CommonException {
        List<Message> messageList = devicesService.deleteDevice(id);
        if (!CollectionUtils.isEmpty(messageList)) {
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messageList);
            SessionUtil.setAttribute("deleteResult", false);
        } else {
            SessionUtil.setAttribute("deleteResult", true);
        }
        return redirect("deviceList");
    }

    @RequestMapping(value = "/addDevice", method = RequestMethod.GET)
    public String addView(ModelMap model) throws CommonException {
        DeviceForm deviceForm = new DeviceForm();
        model.addAttribute("deviceForm", deviceForm);
        return "device/addDevice";
    }

    @RequestMapping(value = "/addOrEditDevice", method = RequestMethod.POST)
    public String addOrEditDevice(@Valid @ModelAttribute("deviceForm") DeviceForm deviceForm,
                                  BindingResult bindingResult, ModelMap model) throws CommonException {
        // Validation Data Input.
        List<Message> messages = validatorUtil.validate(bindingResult);

        if (CollectionUtils.isEmpty(messages)) {

            messages = devicesService.addNewDevice(deviceForm, false);
            if (CollectionUtils.isEmpty(messages)) {
                if(deviceForm.getStatus() == StatusEnum.ACTIVE.getValue()) {
                    Long divisionId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getDivisionsId();

                    DivisionsEntity divisionsEntity = divisionService.findById(divisionId);
                    Long companyId = Long.valueOf(CommonUtil.getCompanyID(divisionsEntity));

                    companyUsageService.updateCompanyUsage(companyId);
                }


                return redirect("deviceList");
            }
            model.addAttribute(Constant.Session.MESSAGES, messages);
            model.addAttribute("deviceForm", deviceForm);
            return "device/addDevice";

        }
        model.addAttribute(Constant.Session.MESSAGES, messages);
        model.addAttribute("deviceForm", deviceForm);
        return "device/addDevice";
    }

    @RequestMapping(value = "/editDevice", method = RequestMethod.GET)
    public String editDevice(@RequestParam("id") Long id, ModelMap model) throws CommonException {
        List<Message> messages = new ArrayList<>();
        if (id == null) {
            messages.add(messageSource.get(Constant.ErrorCode.NOT_EMPTY));
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
            return redirect("deviceList");
        }

        EditDeviceDTO editDeviceDTO = devicesService.getEditDevice(id);

        if (editDeviceDTO == null) {
            messages.add(messageSource.get(Constant.ErrorCode.NOT_FOUND_DATA));
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
            return redirect("deviceList");
        }

        messages = devicesService.validateEditPermission(editDeviceDTO.getId());

        if (!CollectionUtils.isEmpty(messages)) {
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
            return redirect("deviceList");
        }


        DeviceForm deviceForm = ConversionUtil.mapper(editDeviceDTO, DeviceForm.class);
        model.addAttribute("deviceForm", deviceForm);
        return "device/editDevice";
    }

    @RequestMapping(value = "/editDetail", method = RequestMethod.POST)
    public String editDetail(@Valid @ModelAttribute("deviceForm") DeviceForm deviceForm,
                             BindingResult bindingResult, ModelMap model) throws CommonException {
        // Validation Data Input.
        List<Message> messages = validatorUtil.validate(bindingResult);

        if (CollectionUtils.isEmpty(messages)) {
            if (deviceForm.getId() == null) {
                messages.add(messageSource.get(Constant.ErrorCode.NOT_EMPTY));
                SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
                SessionUtil.setAttribute("addEditResult", false);
                return redirect("deviceList");
            }

            EditDeviceDTO editDeviceDTO = devicesService.getEditDevice(deviceForm.getId());

            if (editDeviceDTO == null) {
                messages.add(messageSource.get(Constant.ErrorCode.NOT_FOUND_DATA));
                SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
                SessionUtil.setAttribute("addEditResult", false);
                return redirect("deviceList");
            }

            messages = devicesService.validateEditPermission(editDeviceDTO.getId());

            if (!CollectionUtils.isEmpty(messages)) {
                SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
                SessionUtil.setAttribute("addEditResult", false);
                return redirect("deviceList");
            }

            messages = devicesService.editDevice(deviceForm);

            if (CollectionUtils.isEmpty(messages)) {

                if(deviceForm.getStatus().equals(StatusEnum.ACTIVE.getValue())) {
                    Long divisionId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getDivisionsId();

                    DivisionsEntity divisionsEntity = divisionService.findById(divisionId);
                    Long companyId = Long.valueOf(CommonUtil.getCompanyID(divisionsEntity));

                    companyUsageService.updateCompanyUsage(companyId);
                }

                SessionUtil.setAttribute("addEditResult", true);
                return redirect("deviceList");
            }
            model.addAttribute(Constant.Session.MESSAGES, messages);
            model.addAttribute("deviceForm", deviceForm);
            return "device/editDevice";

        }
        model.addAttribute(Constant.Session.MESSAGES, messages);
        model.addAttribute("deviceForm", deviceForm);
        return "device/editDevice";
    }

    @RequestMapping(value = "/assignDeviceList", method = RequestMethod.GET)
    public String assignDeviceList(@RequestParam("id") Long id, ModelMap model, Integer offset, Integer maxResults) throws CommonException {

        List<Message> messages = devicesService.validatePermissionForAssign(id);
        if (CollectionUtils.isEmpty(messages)) {
            SearchAssignDeviceForm searchAssignDeviceForm = (SearchAssignDeviceForm) SessionUtil.getAttribute(SEARCH_ASSIGN_DEVICE_FORM);
            Integer count = 0;
            List<AssignDeviceDTO> assignDeviceDTOList;
            // in cased of initialize screen -> get default search result
            if (searchAssignDeviceForm == null) {
                searchAssignDeviceForm = new SearchAssignDeviceForm();
                assignDeviceDTOList = devicesService.getAssignDeviceList(id, offset, maxResults, false);
                count = devicesService.getAssignDeviceList(id, offset, maxResults, true).size();
            } else {
                assignDeviceDTOList = devicesService.searchAssignDeviceList(id, searchAssignDeviceForm, offset, maxResults, false);
                count = devicesService.searchAssignDeviceList(id, searchAssignDeviceForm, offset, maxResults, true).size();
            }

            List<Long> deviceIdList = new ArrayList<>();
            HashSet<DivisionDTO> divisionIdList = new HashSet<>();
            if (!CollectionUtils.isEmpty(assignDeviceDTOList)) {
                for (AssignDeviceDTO assignDeviceDTO : assignDeviceDTOList
                        ) {
                    deviceIdList.add(assignDeviceDTO.getDeviceId());
                    DivisionDTO divitionDTO = new DivisionDTO();
                    divitionDTO.setDivisionId(assignDeviceDTO.getDivisionId());
                    if (StringUtils.isNotBlank(assignDeviceDTO.getDivisionName())) {
                        divitionDTO.setDivisionName(assignDeviceDTO.getDivisionName());
                    }
                    divisionIdList.add(divitionDTO);
                }
            }

            Boolean isAssignNewScreen = (Boolean) SessionUtil.getAttribute("isAssignNewScreen");
            if (isAssignNewScreen != null && isAssignNewScreen) {
                SessionUtil.setAttribute("offset", null);
                SessionUtil.setAttribute("maxResults", null);
            }

            SessionUtil.setAttribute("isAssignNewScreen", null);

            List<Message> messageList = ConversionUtil.castList(SessionUtil.getAttribute(Constant.Session.MESSAGES), Message.class);
            setAddEditResultIntoModelMap(model);

            model.addAttribute("count", count);
            model.addAttribute("offset", offset);
            model.addAttribute(SEARCH_ASSIGN_DEVICE_FORM, searchAssignDeviceForm);
            model.addAttribute("deviceIdList", deviceIdList);
            model.addAttribute("divisionIdList", divisionIdList);
            model.addAttribute("deviceList", assignDeviceDTOList);
            model.addAttribute("assignUserId", id);
            model.addAttribute(Constant.Session.MESSAGES, messageList);

            SessionUtil.setAttribute("assignUserId", id);
            SessionUtil.setAttribute(Constant.Session.MESSAGES, null);
            SessionUtil.setAttribute("deleteResult", null);

            return "device/assignDeviceList";
        } else {
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
            return redirect("employeeList");
        }
    }

    @RequestMapping(value = "/searchAssignDevices", method = RequestMethod.GET)
    public String searchAssignDevices(@ModelAttribute("searchAssignDeviceForm") SearchAssignDeviceForm searchAssignDeviceForm, Integer offset, Integer maxResults) {
        logger.info("search assign device list");
        SessionUtil.setAttribute(SEARCH_ASSIGN_DEVICE_FORM, searchAssignDeviceForm);
        Long id = (Long) SessionUtil.getAttribute("assignUserId");
        return redirect("assignDeviceList?id=" + id);
    }

    @RequestMapping(value = "/searchAssignPaging", method = RequestMethod.GET)
    public String searchAssignPaging(Integer offset, Integer maxResults) {
        logger.info("search device list paging");
        Long id = (Long) SessionUtil.getAttribute("assignUserId");
        return pagingRedirectToList("assignDeviceList", offset, maxResults) + "&id=" + id;
    }


    @RequestMapping(value = "/removeAssign", method = RequestMethod.GET)
    public String removeAssign(@RequestParam("id") Long id) throws CommonException {
        Long userId = (Long) SessionUtil.getAttribute("assignUserId");
        List<Message> messageList = devicesService.removeAssign(userId, id);
        if (!CollectionUtils.isEmpty(messageList)) {
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messageList);
            SessionUtil.setAttribute("deleteResult", false);
        } else {
            SessionUtil.setAttribute("deleteResult", true);
        }
        return redirect("assignDeviceList?id=" + userId);
    }

    @RequestMapping(value = "/unassignAll", method = RequestMethod.GET)
    public String unassignAll(Integer offset, Integer maxResults) throws CommonException {
        Long id = (Long) SessionUtil.getAttribute("assignUserId");
        List<Message> messages = devicesService.validatePermissionForAssign(id);


        if (CollectionUtils.isEmpty(messages)) {
            SearchAssignDeviceForm searchAssignDeviceForm = (SearchAssignDeviceForm) SessionUtil.getAttribute(SEARCH_ASSIGN_DEVICE_FORM);
            List<AssignDeviceDTO> assignDeviceDTOList;
            if (searchAssignDeviceForm == null) {
                searchAssignDeviceForm = new SearchAssignDeviceForm();
                assignDeviceDTOList = devicesService.getAssignDeviceList(id, offset, maxResults, true);

            } else {
                assignDeviceDTOList = devicesService.searchAssignDeviceList(id, searchAssignDeviceForm, offset, maxResults, true);
            }

            SessionUtil.setAttribute(SEARCH_ASSIGN_DEVICE_FORM, searchAssignDeviceForm);
            SessionUtil.setAttribute("assignUserId", id);

            if (CollectionUtils.isEmpty(assignDeviceDTOList)) {
                messages.add(messageSource.get(Constant.ErrorCode.NOT_FOUND_DATA));
                SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
                SessionUtil.setAttribute("deleteResult", false);
            } else {
                List<Long> deviceList = new ArrayList<>();
                for (AssignDeviceDTO assignDeviceDTO : assignDeviceDTOList
                        ) {
                    deviceList.add(assignDeviceDTO.getDeviceId());
                }
                messages = devicesService.removeAssignAll(deviceList, id);

                if (CollectionUtils.isEmpty(messages)) {
                    SessionUtil.setAttribute("deleteResult", true);
                } else {
                    SessionUtil.setAttribute("deleteResult", false);
                    SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
                }
            }
            return redirect("assignDeviceList?id=" + id);

        } else {
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
            SessionUtil.setAttribute("deleteResult", false);
            return redirect("assignDeviceList?id=" + id);
        }
    }


    @RequestMapping(value = {"/assignNewDeviceList"}, method = RequestMethod.GET)
    public String assignNewDevice(@RequestParam("id") Long id, ModelMap model, Integer offset, Integer maxResults) throws CommonException {
        SessionUtil.setAttribute("isAssignNewScreen", true);
        //TODO Get default division
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();

        List<Message> messages = devicesService.validatePermissionForAssign(id);
        if (CollectionUtils.isEmpty(messages)) {

            // Get selected operator's belonged division
            Long divisionIdUser = userService.getUser(id).getDivisionsId();

            SearchDeviceForm searchDeviceForm = null;
            try {
                searchDeviceForm = (SearchDeviceForm) SessionUtil.getAttribute(SEARCH_DEVICE_FORM);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
            List<DeviceDTO> deviceDTOList;

            // in cased of initialized screen
            Integer totalCount = 0;
            if (searchDeviceForm == null) {
                searchDeviceForm = new SearchDeviceForm();
                deviceDTOList = devicesService.getDeviceList(divisionIdUser, offset, maxResults, false, true);
                totalCount = devicesService.getDeviceList(divisionIdUser, offset, maxResults, true, true).size();
            } else {
                deviceDTOList = devicesService.searchDeviceList(searchDeviceForm, offset, maxResults, false, true);
                totalCount = devicesService.searchDeviceList(searchDeviceForm, offset, maxResults, true, true).size();
            }

            List<Long> deviceIdList = new ArrayList<>();
            HashSet<DivisionDTO> divisionIdList = new HashSet<>();
            if (!CollectionUtils.isEmpty(deviceDTOList)) {
                for (DeviceDTO deviceDTO : deviceDTOList
                        ) {
                    deviceIdList.add(deviceDTO.getDeviceId());
                    DivisionDTO divitionDTO = new DivisionDTO();
                    divitionDTO.setDivisionId(deviceDTO.getDivisionId());
                    if (StringUtils.isNotBlank(deviceDTO.getDivisionName())) {
                        divitionDTO.setDivisionName(deviceDTO.getDivisionName());
                    }
                    divisionIdList.add(divitionDTO);
                }
            }

            List<Message> messageList = ConversionUtil.castList(SessionUtil.getAttribute(Constant.Session.MESSAGES), Message.class);

            Boolean addEditResult = (Boolean) SessionUtil.getAttribute("addEditResult");
            if (addEditResult != null) {
                if (addEditResult) {
                    model.addAttribute("addEditResult", true);
                } else {
                    model.addAttribute("addEditResult", false);
                }
            } else {
                model.addAttribute("addEditResult", null);
            }
            model.addAttribute("deviceList", deviceDTOList);
            model.addAttribute(SEARCH_DEVICE_FORM, searchDeviceForm);
            model.addAttribute("deviceIdList", deviceIdList);
            model.addAttribute("divisionIdList", divisionIdList);
            model.addAttribute("count", totalCount);
            model.addAttribute("offset", offset);
            model.addAttribute("assignUserId", id);
            model.addAttribute(Constant.Session.MESSAGES, messageList);
            SessionUtil.setAttribute(Constant.Session.MESSAGES, null);
            SessionUtil.setAttribute("addEditResult", null);

            return "device/assignNewDevice";
        } else {
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
            return redirect("assignDeviceList?id=" + id);
        }
    }

    @RequestMapping(value = "/searchAssignNewDevice", method = RequestMethod.GET)
    public String searchAssignNewDevice(@ModelAttribute("searchDeviceForm") SearchDeviceForm searchDeviceForm) {
        logger.info("search assign new device list");

        SessionUtil.setAttribute(SEARCH_DEVICE_FORM, searchDeviceForm);
        Long id = (Long) SessionUtil.getAttribute("assignUserId");
        return redirect("assignNewDeviceList?id=" + id);
    }

    @RequestMapping(value = "/searchAssignNewDevicePaging", method = RequestMethod.GET)
    public String searchAssignNewDevicePaging(Integer offset, Integer maxResults) {
        logger.info("search device list paging");
        Long id = (Long) SessionUtil.getAttribute("assignUserId");
        return pagingRedirectToList("assignNewDeviceList", offset, maxResults) + "&id=" + id;
    }

    @RequestMapping(value = "/assignDevice", method = RequestMethod.GET)
    public String assignDevice(@RequestParam("id") Long id) throws CommonException {
        Long userId = (Long) SessionUtil.getAttribute("assignUserId");
        List<Message> messageList = devicesService.assignDevice(userId, id);
        if (!CollectionUtils.isEmpty(messageList)) {
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messageList);
            SessionUtil.setAttribute("addEditResult", false);
        } else {
            SessionUtil.setAttribute("addEditResult", true);
        }
        return redirect("assignNewDeviceList?id=" + userId);
    }

    @RequestMapping(value = "/assignAllDevice", method = RequestMethod.GET)
    public String assignAllDevice(Integer offset, Integer maxResults) throws CommonException {
        Long id = (Long) SessionUtil.getAttribute("assignUserId");
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        List<Message> messages = devicesService.validatePermissionForAssign(id);
        if (CollectionUtils.isEmpty(messages)) {
            SearchDeviceForm searchDeviceForm = (SearchDeviceForm) SessionUtil.getAttribute(SEARCH_DEVICE_FORM);
            Long divivionIdUser = divisionService.findOnlyIdByUserId(userLoginId);
            List<DeviceDTO> deviceDTOList;
            if (searchDeviceForm == null) {
                searchDeviceForm = new SearchDeviceForm();
                deviceDTOList = devicesService.getDeviceList(divivionIdUser, offset, maxResults, true, true);
            } else {
                deviceDTOList = devicesService.searchDeviceList(searchDeviceForm, offset, maxResults, true, true);
            }

            SessionUtil.setAttribute(SEARCH_DEVICE_FORM, searchDeviceForm);
            SessionUtil.setAttribute("assignUserId", id);

            if (CollectionUtils.isEmpty(deviceDTOList)) {
                messages.add(messageSource.get(Constant.ErrorCode.NOT_FOUND_DATA));
                SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
                SessionUtil.setAttribute("addEditResult", false);
            } else {
                List<Long> deviceList = new ArrayList<>();
                for (DeviceDTO deviceDTO : deviceDTOList
                        ) {
                    deviceList.add(deviceDTO.getDeviceId());
                }
                messages = devicesService.assignAllDevice(deviceList, id);

                if (CollectionUtils.isEmpty(messages)) {
                    SessionUtil.setAttribute("addEditResult", true);
                } else {
                    SessionUtil.setAttribute("addEditResult", false);
                    SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
                }
            }
            return redirect("assignNewDeviceList?id=" + id);

        } else {
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
            SessionUtil.setAttribute("deleteResult", false);
            return redirect("assignNewDeviceList?id=" + id);
        }
    }

    @PostMapping(value = "/device/resetPasswordDevices")
    public ResponseEntity<List<Message>> resetPasswordDevices(@RequestBody DevicesResetPasswordDTO resetPasswordDTO)
            throws CommonException {
        List<Message> messages = new ArrayList<>();
        if (resetPasswordDTO.getDeviceId() == null) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.NOT_EMPTY, new String[]{"Id device not null"}));
        }
        if (StringUtils.isBlank(resetPasswordDTO.getPassword())) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.NOT_EMPTY, new String[]{"Password device not null"}));
        }
        if (messages.isEmpty()) {
            messages.addAll(devicesService.changePassword(resetPasswordDTO));
        }

        Pattern checkMinLength = Pattern.compile("(.{8,})");
        if (!checkMinLength.matcher(resetPasswordDTO.getPassword()).matches()) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.PASS_LENGTH, new String[]{}));

        }

        if (resetPasswordDTO.getPassword().length() > 200) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.PASS_MAXLENGTH, new String[]{}));
        }

        Pattern checkNumber = Pattern.compile("(?=.*[0-9])");
        if (!checkNumber.matcher(resetPasswordDTO.getPassword()).find()) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.PASS_NUMBER, new String[]{}));
        }
        Pattern checkLower = Pattern.compile("(?=.*[a-z])");
        if (!checkLower.matcher(resetPasswordDTO.getPassword()).find()) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.PASS_LOWERCASE, new String[]{}));
        }
        Pattern checkUpper = Pattern.compile("(?=.*[A-Z])");
        if (!checkUpper.matcher(resetPasswordDTO.getPassword()).find()) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.UPPER_CASE, new String[]{}));
        }

        Pattern checkSpecial = Pattern.compile("(?=.*[!#$%&'()*+-./:;<=>?@^_`{|}~])");
        if (!checkSpecial.matcher(resetPasswordDTO.getPassword()).find()) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.PASS_SPECIAl, new String[]{}));
        }
        if (resetPasswordDTO.getPassword().indexOf(" ") != -1) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.PASS_WHITESPACE, new String[]{}));
        }
        Pattern check = Pattern.compile("012|210|123|321|234|432|345|543|456|654|567|765|678|876|789|987");
        if (check.matcher(resetPasswordDTO.getPassword()).find()) {
            messages.add(messageSource.getMessage(Constant.ErrorCode.PASS_NUMERICAL, new String[]{}));
        }
        if (messages.isEmpty()) {
            messages.addAll(devicesService.changePassword(resetPasswordDTO));
        }

        return ResponseEntity.ok(messages);
    }


}
