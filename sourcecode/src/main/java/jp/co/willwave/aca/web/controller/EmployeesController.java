package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.dto.api.quickblox.QuickBloxUserResponse;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.service.CompanyUsageService;
import jp.co.willwave.aca.service.DivisionService;
import jp.co.willwave.aca.service.QuickBloxService;
import jp.co.willwave.aca.service.UserService;
import jp.co.willwave.aca.utilities.CommonUtil;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.DivisionSelectForm;
import jp.co.willwave.aca.web.form.EmployeeForm;
import jp.co.willwave.aca.web.form.RoleForm;
import jp.co.willwave.aca.web.form.SearchEmployeeForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

@Controller
public class EmployeesController extends AbstractController {

    private final UserService userService;
    private final DivisionService divisionService;
    private final CompanyUsageService companyUsageService;
    private final QuickBloxService quickBloxService;

    public EmployeesController(UserService userService,
                               DivisionService divisionService, CompanyUsageService companyUsageService,
                               QuickBloxService quickBloxService) {
        this.userService = userService;
        this.divisionService = divisionService;
        this.companyUsageService = companyUsageService;
        this.quickBloxService = quickBloxService;
    }

    @RequestMapping(value = {"employeeList"}, method = RequestMethod.GET)
    public String employeeView(Integer offset, ModelMap model) throws CommonException {
        List<RoleForm> roleList = CommonUtil.getRoleListDefault();

        SearchEmployeeForm searchEmployeeForm = (SearchEmployeeForm) SessionUtil.getAttribute("searchEmployeeForm");
        searchEmployeeForm = (searchEmployeeForm == null ? new SearchEmployeeForm() : searchEmployeeForm);

        if (searchEmployeeForm.getRoleId() == null) {
            List<Integer> roleIds = roleList.stream().map(x -> Integer.valueOf(x.getId().toString())).collect(Collectors
                    .toList());
            searchEmployeeForm.setRoleIds(roleIds);
        }

        List<DivisionSelectForm> divisionList = divisionService.getDivisionManaged(getLoginUserId());
        if (searchEmployeeForm.getDivisionsId() == null) {
            List<Long> divisionIds = divisionList.stream().map(DivisionSelectForm::getId).collect(Collectors.toList());
            searchEmployeeForm.setDivisionsIds(divisionIds);
        }

        List<EmployeeForm> employees = userService.searchEmployees(searchEmployeeForm, offset, Constant.MAX_SEARCH_RESULT);
        Long totalCount = userService.countSearchEmployees(searchEmployeeForm);
        if (!CollectionUtils.isEmpty(employees)) {
            Map<Integer, String> rolesMap = roleList.stream().collect(
                    Collectors.toMap(RoleForm::getId, RoleForm::getName));
            Map<Long, String> divisionMap = divisionList.stream().collect(
                    Collectors.toMap(DivisionSelectForm::getId, DivisionSelectForm::getName));
            employees.forEach(employeeForm -> {
                employeeForm.setRoleName(rolesMap.get(employeeForm.getRoleId()));
                employeeForm.setDivisionName(   divisionMap.get(employeeForm.getDivisionsId()));
            });
        }

        model.addAttribute("employeeList", employees);
        model.addAttribute("searchEmployeeForm", searchEmployeeForm);
        model.addAttribute("divisionList", divisionList);
        model.addAttribute("roleList", roleList);
        model.addAttribute("offset", offset);
        model.addAttribute("count", totalCount);
        return "employee/employee";
    }

    @RequestMapping(value = "searchEmployee", method = RequestMethod.GET)
    public String search(@ModelAttribute("searchEmployeeForm") SearchEmployeeForm searchEmployeeForm) {
        logger.info("search employee list");
        SessionUtil.setAttribute("searchEmployeeForm", searchEmployeeForm);
        return redirect("employeeList");
    }

    @RequestMapping(value = "/searchEmployeePaging", method = RequestMethod.GET)
    public String searchPaging(Integer offset, Integer maxResults) {
        logger.info("search device list paging");
        return pagingRedirectToList("employeeList", offset, maxResults);
    }

    @RequestMapping(value = "addEmployee", method = RequestMethod.POST)
    public String addOrEdit(@Valid @ModelAttribute("employeeForm") EmployeeForm employeeForm,
                            BindingResult bindingResult, Model model) throws CommonException {
        UsersEntity usersEntity = ConversionUtil.mapper(employeeForm, UsersEntity.class);
        List<Message> messages = validatorUtil.validate(bindingResult);
        if (CollectionUtils.isEmpty(messages)) {
            if (employeeForm.getModeEdit()) {
                UsersEntity oldUsers = userService.getUser(usersEntity.getId());
                if (UserRole.OPERATOR.getRole().equals(oldUsers.getRoleId())
                        || UserRole.VIEWER.getRole().equals(oldUsers.getRoleId())) {
                    oldUsers.setRoleId(usersEntity.getRoleId());
                }
                oldUsers.setFirstName(usersEntity.getFirstName());
                oldUsers.setLastName(usersEntity.getLastName());
                oldUsers.setUserAddress(usersEntity.getUserAddress());
                oldUsers.setUserEmail(usersEntity.getUserEmail());
                oldUsers.setFaxNumber(usersEntity.getFaxNumber());
                oldUsers.setFixedPhone(usersEntity.getFixedPhone());
                oldUsers.setMobilePhone(usersEntity.getMobilePhone());
                oldUsers.setLoginId(usersEntity.getLoginId());
                messages.addAll(userService.updateUser(oldUsers));
            } else {
                // create quickblox account for operator
                if (UserRole.OPERATOR.getRole().equals(usersEntity.getRoleId())){
                    DivisionsEntity divisionsEntity = divisionService.findById(usersEntity.getDivisionsId());
                    String companyRootCode = CommonUtil.getCompanyID(divisionsEntity);
                    String companyCode = quickBloxService.generateQuickBloxTagByCompanyId(Long.valueOf(companyRootCode));

                    QuickBloxUserResponse quickBloxUserResponse = quickBloxService.createQuickBloxUser(companyCode, usersEntity.getLoginId());

                    if (quickBloxUserResponse.getErrorMessage() != null) {
                        messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.QUICKBLOX_ERROR, new String[]{quickBloxUserResponse.getErrorMessage()}));
                    } else {
                        // update quickblox acc info
                        usersEntity.setCallName(quickBloxUserResponse.getLogin());
                        usersEntity.setCallPassword(quickBloxUserResponse.getPassword());
                        usersEntity.setUserTags(quickBloxUserResponse.getUser_tags());
                        usersEntity.setCallId(quickBloxUserResponse.getId());
                        // create user
                        messages.addAll(userService.createUser(usersEntity));
                    }
                } else {
                    // create viewer account
                    if (UserRole.VIEWER.getRole().equals(usersEntity.getRoleId())){
                        messages.addAll(userService.createUser(usersEntity));
                    }
                }
            }

            if (CollectionUtils.isEmpty(messages)) {
                if (usersEntity.getStatus()) {
                    Long divisionId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getDivisionsId();

                    DivisionsEntity divisionsEntity = divisionService.findById(divisionId);
                    Long companyId = Long.valueOf(CommonUtil.getCompanyID(divisionsEntity));

                    companyUsageService.updateCompanyUsage(companyId);
                }

                return redirect("searchEmployee");
            }
        }

        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        List<DivisionSelectForm> divisionList = divisionService.getDivisionManaged(userLoginId);
        List<RoleForm> roleList = CommonUtil.getRoleOperatorAndView();
        model.addAttribute("divisionList", divisionList);
        model.addAttribute("roleList", roleList);
        model.addAttribute("modeEdit", false);
        model.addAttribute(Constant.Session.MESSAGES, messages);
        return "/employee/addEmployee";
    }

    @RequestMapping(value = "change/status/{id}", method = RequestMethod.GET)
    public String editView(@PathVariable("id") Long id) throws CommonException {
        UsersEntity usersEntity = userService.getUser(id);
        usersEntity.setStatus(!usersEntity.getStatus());
        if (UserRole.VIEWER.getRole().equals(usersEntity.getRoleId())
                || UserRole.OPERATOR.getRole().equals(usersEntity.getRoleId())) {
            userService.updateUser(usersEntity);
        }
        return redirect("employeeList");
    }

    @RequestMapping(value = "editEmployee/{id}", method = RequestMethod.GET)
    public String editView(@PathVariable("id") Long id, ModelMap model) throws CommonException {
        UsersEntity usersEntity = userService.getUser(id);
        model.addAttribute("employeeForm", ConversionUtil.mapper(usersEntity, EmployeeForm.class));
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        List<DivisionSelectForm> divisionList = divisionService.getDivisionManaged(userLoginId);
        List<RoleForm> roleList;
        if (UserRole.DIVISION_DIRECTOR.getRole().equals(usersEntity.getRoleId())) {
            roleList = CommonUtil.getRoleListDefault();
        } else {
            roleList = CommonUtil.getRoleOperatorAndView();
        }
        model.addAttribute("divisionList", divisionList);
        model.addAttribute("roleList", roleList);
        model.addAttribute("modeEdit", true);
        return "/employee/addEmployee";
    }

    @RequestMapping(value = "deleteEmployee/{id}", method = RequestMethod.GET)
    public String deleteCompany(@PathVariable("id") Long id) throws CommonException {
        userService.delete(id);
        return redirect("employeeList");
    }

    @RequestMapping(value = "addEmployeeView", method = RequestMethod.GET)
    public String addView(ModelMap model) throws CommonException {
        EmployeeForm employeeForm = new EmployeeForm();
        model.addAttribute("employeeForm", employeeForm);
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();
        List<DivisionSelectForm> divisionList = divisionService.getDivisionManaged(userLoginId);
        List<RoleForm> roleList = CommonUtil.getRoleOperatorAndView();
        model.addAttribute("divisionList", divisionList);
        model.addAttribute("roleList", roleList);
        model.addAttribute("modeEdit", false);
        return "employee/addEmployee";
    }


}
