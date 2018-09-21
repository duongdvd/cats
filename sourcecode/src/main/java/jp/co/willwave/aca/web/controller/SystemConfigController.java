package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.constants.ConfigEnum;
import jp.co.willwave.aca.dto.api.DivisionDTO;
import jp.co.willwave.aca.dto.api.SystemConfigDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.entity.DivisionsHasSysConfigsEntity;
import jp.co.willwave.aca.service.DivisionService;
import jp.co.willwave.aca.service.SystemConfigService;
import jp.co.willwave.aca.utilities.CatStringUtil;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.SystemConfigForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@Controller
public class SystemConfigController extends AbstractController {

    private final DivisionService divisionService;
    private final SystemConfigService systemConfigService;

    @Autowired
    public SystemConfigController(DivisionService divisionService, SystemConfigService systemConfigService) {
        this.divisionService = divisionService;
        this.systemConfigService = systemConfigService;
    }

    @RequestMapping(value = {"/systemConfig"}, method = RequestMethod.GET)
    public String openSystemConfig(@RequestParam(value = "divisionId", required = false) Long divisionId, ModelMap model)
        throws CommonException {
        Long userId = ((UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO)).getId();
        DivisionDTO divisionDTO = divisionService.findDivisionsRelative(userId);

        if (divisionDTO == null) {
            model.addAttribute(Constant.Session.MESSAGES,
                messageSource.getMessage(Constant.ErrorCode.NOT_EXISTS, new String[]{"divisionId"}));
        } else {
            List<DivisionsEntity> divisionsEntities = divisionDTO.getDivisionRelatives();
            model.addAttribute("divisionList", divisionsEntities);

            // Get system config of selected division.
            DivisionDTO divisionDtoDisplay = getDivisionDisplay(divisionId, divisionDTO);
            SystemConfigDTO systemConfigDTO = systemConfigService.findByDivisionId(divisionDtoDisplay.getDivisionId());

            SystemConfigForm systemConfigForm = new SystemConfigForm();
            if (systemConfigDTO != null) {
                systemConfigForm = ConversionUtil.mapper(systemConfigDTO, SystemConfigForm.class);
            } else {
                systemConfigForm.setDivisionId(divisionDtoDisplay.getDivisionId());
                systemConfigForm.setDivisionName(divisionDtoDisplay.getDivisionName());
                if (divisionDtoDisplay.getParentDivisionId() != null) {
                    DivisionsEntity parent = divisionService.findById(divisionDtoDisplay.getParentDivisionId());
                    systemConfigForm.setParentDivisionName(parent.getDivisionName());
                }
            }

            systemConfigForm.setCanEditFlg(divisionDtoDisplay.isCanEditFlg());

            if(systemConfigForm.getNotificationTime() == null){
                systemConfigForm.setSetNotificationTime(false);
            }
            if(systemConfigForm.getNotificationEmail() == null){
                systemConfigForm.setSetNotificationEmail(false);
            }
            if(systemConfigForm.getMobileIcon() == null){
                systemConfigForm.setSetMobileIconFile(false);
            }
            if(systemConfigForm.getCustomerIcon() == null){
                systemConfigForm.setSetCustomerIconFile(false);
            }
            if(systemConfigForm.getNotificationTime() == null){
                systemConfigForm.setSetNotificationTime(false);
            }
            if(systemConfigForm.getTimeMessage() == null){
                systemConfigForm.setSetTimeMessage(false);
            }
            if(systemConfigForm.getTravelTimeAlert() == null){
                systemConfigForm.setSetTravelTimeAlert(false);
            }
            if(systemConfigForm.getDistanceFinished() == null){
                systemConfigForm.setSetDistanceFinished(false);
            }
            if(systemConfigForm.getStartEndPointColor() == null){
                systemConfigForm.setSetStartEndPointColor(false);
            }
            if(systemConfigForm.getArrivedPoint() == null){
                systemConfigForm.setSetArrivedPointColor(false);
            }
            if(systemConfigForm.getNotArrivedPoint() == null){
                systemConfigForm.setSetNotArrivedPointColor(false);
            }
            model.addAttribute("systemConfigForm", systemConfigForm);
        }

        return "systemConfig/systemConfig";
    }

    @RequestMapping(value = {"/updateSystemConfig"}, method = RequestMethod.POST)
    public String updateSystemConfig(@Valid @ModelAttribute("systemConfigForm") SystemConfigForm form,
                                     BindingResult result, ModelMap model) throws CommonException {
        List<Message> messages = validatorUtil.validate(result);
        if (CollectionUtils.isEmpty(messages)) {
            Map<ConfigEnum, DivisionsHasSysConfigsEntity> divisionConfigMap = buildSystemConfig(form, messages);
            if (CollectionUtils.isEmpty(messages)) {
                messages.addAll(systemConfigService.updateDivisionConfig(divisionConfigMap, form));
            }
        }

        model.addAttribute(Constant.Session.MESSAGES, messages);
        model.addAttribute("systemConfigForm", form);

        Long userId = ((UserInfo) SessionUtil.getAttribute(Constant.Session.USER_LOGIN_INFO)).getId();
        DivisionDTO divisionDTO = divisionService.findDivisionsRelative(userId);
        if (divisionDTO == null) {
            model.addAttribute(Constant.Session.MESSAGES,
                    messageSource.getMessage(Constant.ErrorCode.NOT_EXISTS, new String[]{"divisionId"}));
        } else {
            List<DivisionsEntity> divisionsEntities = divisionDTO.getDivisionRelatives();
            model.addAttribute("divisionList", divisionsEntities);
        }

        if(!form.isSetNotificationTime()){
            form.setNotificationTime(null);
        }
        if(!form.isSetNotificationEmail()){
            form.setNotificationEmail(null);
        }
        if(!form.isSetMobileIconFile()){
            form.setMobileIcon(null);
        }
        if(!form.isSetCustomerIconFile()){
            form.setCustomerIconFile(null);
        }
        if(!form.isSetTimeMessage()){
            form.setTimeMessage(null);
        }
        if(!form.isSetTravelTimeAlert()){
            form.setTravelTimeAlert(null);
        }
        if(!form.isSetDistanceFinished()){
            form.setDistanceFinished(null);
        }
        if(!form.isSetStartEndPointColor()){
            form.setStartEndPointColor(null);
        }
        if(!form.isSetArrivedPointColor()){
            form.setArrivedPoint(null);
        }
        if(!form.isSetNotArrivedPointColor()){
            form.setNotArrivedPoint(null);
        }

        return "systemConfig/systemConfig";
    }

    private Map<ConfigEnum, DivisionsHasSysConfigsEntity> buildSystemConfig(SystemConfigForm form, List<Message> messages) throws CommonException {
        Map<ConfigEnum, DivisionsHasSysConfigsEntity> divisionsConfigsMap = systemConfigService.findDivisionsConfig(form.getDivisionId());

        if (form.isSetMobileIconFile() && form.getMobileIconFile().isEmpty() && StringUtils.isEmpty(form.getMobileIcon())) {
            messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"systemConfig.mobileIcon"}));
        }

        if (form.isSetCustomerIconFile() && form.getCustomerIconFile().isEmpty() && StringUtils.isEmpty(form.getCustomerIcon())) {
            messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"systemConfig.customerIcon"}));
        }

        if (CollectionUtils.isEmpty(messages)) {
            updateOrCreateDivisionConfig(divisionsConfigsMap, ConfigEnum.NOTIFICATION_TIME,
                form.getNotificationTime(), form.isSetNotificationTime(), form.getDivisionId(), messages);

            updateOrCreateDivisionConfig(divisionsConfigsMap, ConfigEnum.NOTIFICATION_EMAIL,
                form.getNotificationEmail(), form.isSetNotificationEmail(), form.getDivisionId(), messages);

            updateOrCreateDivisionConfig(divisionsConfigsMap, ConfigEnum.MOBILE_ICON,
                "mobileIcon", form.isSetMobileIconFile(), form.getDivisionId(), messages);

            updateOrCreateDivisionConfig(divisionsConfigsMap, ConfigEnum.CUSTOMER_ICON,
                "customerIcon", form.isSetCustomerIconFile(), form.getDivisionId(), messages);

            updateOrCreateDivisionConfig(divisionsConfigsMap, ConfigEnum.TIME_MESSAGE,
                form.getTimeMessage(), form.isSetTimeMessage(), form.getDivisionId(), messages);

            updateOrCreateDivisionConfig(divisionsConfigsMap, ConfigEnum.TRAVEL_TIME_ALERT,
                form.getTravelTimeAlert(), form.isSetTravelTimeAlert(), form.getDivisionId(), messages);

            updateOrCreateDivisionConfig(divisionsConfigsMap, ConfigEnum.DISTANCE_FINISHED,
                form.getDistanceFinished(), form.isSetDistanceFinished(), form.getDivisionId(), messages);

            updateOrCreateDivisionConfig(divisionsConfigsMap, ConfigEnum.START_END_POINT_COLOR,
                form.getStartEndPointColor(), form.isSetStartEndPointColor(), form.getDivisionId(), messages);

            updateOrCreateDivisionConfig(divisionsConfigsMap, ConfigEnum.ARRIVED_POINT,
                form.getArrivedPoint(), form.isSetArrivedPointColor(), form.getDivisionId(), messages);

            updateOrCreateDivisionConfig(divisionsConfigsMap, ConfigEnum.NOT_ARRIVED_POINT,
                form.getNotArrivedPoint(), form.isSetNotArrivedPointColor(), form.getDivisionId(), messages);
        }

        return divisionsConfigsMap;
    }


    private void updateOrCreateDivisionConfig(Map<ConfigEnum, DivisionsHasSysConfigsEntity> divisionsConfigsMap,
                                              ConfigEnum configKey, String newValue, boolean isSet, Long divisionId, List<Message> messages) {
        DivisionsHasSysConfigsEntity divisionConfig = divisionsConfigsMap.get(configKey);
        if (divisionConfig == null) {
            divisionConfig = new DivisionsHasSysConfigsEntity(divisionId);
            divisionConfig.setKey(configKey);
        }

        divisionConfig.setDeleteFlg(!isSet);
        if (isSet) {
            if (CatStringUtil.isEmpty(newValue)) {
                messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{"systemConfig."+ configKey.toString()}));
            }

            if (ConfigEnum.NOTIFICATION_TIME.equals(configKey)
                    || ConfigEnum.TIME_MESSAGE.equals(configKey)
                    || ConfigEnum.TRAVEL_TIME_ALERT.equals(configKey)
                    || ConfigEnum.DISTANCE_FINISHED.equals(configKey)) {
                if (!CatStringUtil.isNumeric(newValue)) {
                    messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.FORMAT_INVALID, new String[]{"systemConfig."+ configKey.toString()}));
                }
            }
            divisionConfig.setValue(newValue);
            divisionsConfigsMap.put(configKey, divisionConfig);
        }
    }

    private DivisionDTO getDivisionDisplay(Long divisionId, DivisionDTO divisionDTO) {
        if (divisionId != null && !divisionDTO.getDivisionId().equals(divisionId)) {
            List<DivisionsEntity> divisionsEntities = divisionDTO.getParentList();
            for (DivisionsEntity d : divisionsEntities) {
                if (d.getId().equals(divisionId)) {
                    return new DivisionDTO(d.getId(), d.getDivisionName(), d.getParentDivisionsId(), false);
                }
            }

            List<DivisionsEntity> childrenList = divisionDTO.getChildrenList();
            for (DivisionsEntity d : childrenList) {
                if (d.getId().equals(divisionId)) {
                    return new DivisionDTO(d.getId(), d.getDivisionName(), d.getParentDivisionsId(), true);
                }
            }
        }

        return new DivisionDTO(divisionDTO.getDivisionId(), divisionDTO.getDivisionName(), divisionDTO.getParentDivisionId(), true);
    }
}
