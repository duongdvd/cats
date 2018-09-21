package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.constants.StatusEnum;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.Message;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.entity.UsersEntity;
import jp.co.willwave.aca.service.CompaniesService;
import jp.co.willwave.aca.service.CompanyUsageService;
import jp.co.willwave.aca.service.UserService;
import jp.co.willwave.aca.utilities.ConversionUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.CompanyForm;
import jp.co.willwave.aca.web.form.SearchCompanyForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

@Controller
public class CompaniesController extends AbstractController {
    private final CompaniesService companiesService;
    private final UserService userService;
    private final CompanyUsageService companyUsageService;

    @Autowired
    public CompaniesController(CompaniesService companiesService, UserService userService, CompanyUsageService companyUsageService) {
        this.companiesService = companiesService;
        this.userService = userService;
        this.companyUsageService = companyUsageService;
    }

    @RequestMapping(value = {"/companyList"}, method = RequestMethod.GET)
    public String companiesView(ModelMap model, Integer offset, Integer maxResults) throws CommonException, ParseException {
        setAddEditResultIntoModelMap(model);

        SearchCompanyForm form = (SearchCompanyForm) SessionUtil.getAttribute("searchCompanyForm");
        form = (form == null ? new SearchCompanyForm() : form);

        List<DivisionsEntity> companies = companiesService.searchCompanies(form, offset, maxResults, true);
        model.addAttribute("companyList", companies);
        model.addAttribute("searchCompanyForm", form);

        List<Message> messageList = ConversionUtil.castList(SessionUtil.getAttribute(Constant.Session.MESSAGES), Message.class);
        model.addAttribute(Constant.Session.MESSAGES, messageList);

        Integer count = companiesService.searchCompanies(form, offset, maxResults, false).size();
        model.addAttribute("count", count);
        model.addAttribute("offset", offset);

        HashSet<Long> divisionIdList = new HashSet<>();
        if (!CollectionUtils.isEmpty(companies)) {
            for (DivisionsEntity d : companies) {
                divisionIdList.add(d.getId());
            }
        }
        model.addAttribute("divisionIdList", divisionIdList);

        SessionUtil.setAttribute(Constant.Session.MESSAGES, null);
        SessionUtil.setAttribute("deleteResult", null);
        SessionUtil.setAttribute("addEditResult", null);
        return "companies/companies";
    }

    @RequestMapping(value = "/searchCompanyPaging", method = RequestMethod.GET)
    public String searchPaging(Integer offset, Integer maxResults) {
        logger.info("search device list paging");
        return pagingRedirectToList("companyList", offset, maxResults);
    }

    @RequestMapping(value = "searchCompanies", method = RequestMethod.GET)
    public String search(@ModelAttribute("searchCompanyForm") SearchCompanyForm searchCompanyForm) {
        SessionUtil.setAttribute("searchCompanyForm", searchCompanyForm);
        return redirect("companyList");
    }
        /**
		 * @return view of detail company.
		 * @author KhanhNV
		 * @since 2018
		 */
    @RequestMapping(value = "deleteCompany", method = RequestMethod.GET)
    public String deleteCompany(@RequestParam("id") Long id) throws CommonException {
        UserInfo userLogin = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO));
        List<Message> messages = companiesService.deleteCompany(id);

        if (!CollectionUtils.isEmpty(messages)) {
            SessionUtil.setAttribute(Constant.Session.MESSAGES, messages);
            SessionUtil.setAttribute("deleteResult", false);
        } else {
            SessionUtil.setAttribute("deleteResult", true);
        }
        return redirect("companyList");
    }

    /**
     * @return add company page.
     * @author KhanhNV
     * @since 2018
     */
    @RequestMapping(value = "addCompanyView", method = RequestMethod.GET)
    public String addView(ModelMap model) {
        CompanyForm companyForm = new CompanyForm();
        model.addAttribute("CompanyForm", companyForm);
        SessionUtil.setAttribute("modeEdit", false);
        return "companies/addCompany";
    }

    @RequestMapping(value = "editCompany/{id}", method = RequestMethod.GET)
    public String editView(@PathVariable("id") Long id, ModelMap model) throws CommonException {
        UserInfo userLogin = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO));
        DivisionsEntity companyEntity = companiesService.getCompany(id);
        UsersEntity usersEntity = userService.getUser(companyEntity.getUsersId());
        CompanyForm companyForm = new CompanyForm();

        companyForm.setDescription(companyEntity.getDescription());
        companyForm.setDivisionAddress(companyEntity.getDivisionAddress());
        companyForm.setDivisionName(companyEntity.getDivisionName());
        companyForm.setFirstName(usersEntity.getFirstName());
        companyForm.setLastName(usersEntity.getLastName());
        companyForm.setLoginId(usersEntity.getLoginId());
        companyForm.setUserEmail(usersEntity.getUserEmail());
        companyForm.setStatus(companyEntity.getStatus());

        companyForm.setPasswd(null);
        companyForm.setConfirmPasswd(null);
        model.addAttribute("CompanyForm", companyForm);
        model.addAttribute("modeEdit", true);
        SessionUtil.setAttribute("modeEdit", true);
        SessionUtil.setAttribute("id", companyEntity.getId());
        return "companies/addCompany";
    }

    /**
     * @return Submit add company page.
     * @author KhanhNV
     * @since 2018
     */
    @RequestMapping(value = "addCompany", method = RequestMethod.POST)
    public String add(@Valid @ModelAttribute("CompanyForm") CompanyForm companyForm, BindingResult bindingResult, ModelMap model)
        throws CommonException {
        Boolean modeEdit = (Boolean) SessionUtil.getAttribute("modeEdit");
        // Validation Data Input.
        List<Message> messages = validatorUtil.validate(bindingResult);
        if (CollectionUtils.isEmpty(messages)) {
            if (modeEdit) {
                messages = companiesService.validateForm(companyForm, false);
            } else {
                messages = companiesService.validateForm(companyForm, true);
            }
        }
        if (CollectionUtils.isEmpty(messages)) {
            if (!modeEdit) {
                UsersEntity usersEntity = new UsersEntity();
                usersEntity.setPasswd(companyForm.getPasswd().trim());
                usersEntity.setFirstName(companyForm.getFirstName().trim());
                usersEntity.setLastName(companyForm.getLastName().trim());
                usersEntity.setLoginId(companyForm.getLoginId().trim());
                usersEntity.setUserEmail(companyForm.getUserEmail());
                if (companyForm.getStatus() == StatusEnum.ACTIVE.getValue()) {
                    usersEntity.setStatus(true);
                } else {
                    usersEntity.setStatus(false);
                }

                DivisionsEntity companiesEntity = new DivisionsEntity();
                companiesEntity.setDivisionAddress(companyForm.getDivisionAddress());
                companiesEntity.setDivisionName(companyForm.getDivisionName());
                companiesEntity.setDivisionMail(companyForm.getUserEmail());
                companiesEntity.setDescription(companyForm.getDescription());
                companiesEntity.setStatus(companyForm.getStatus());

                messages = companiesService.createCompany(usersEntity, companiesEntity);
                if (CollectionUtils.isEmpty(messages)) {
                    if (companiesEntity.getStatus() == StatusEnum.ACTIVE.getValue()) {
                        companyUsageService.updateCompanyUsage(companiesEntity.getId());
                    }
                }

            } else {

                Long id = (Long) SessionUtil.getAttribute("id");

                DivisionsEntity companiesEntity = companiesService.getCompany(id);
                if (companiesEntity != null) {
                    companiesEntity.setDivisionAddress(companyForm.getDivisionAddress());
                    companiesEntity.setDivisionName(companyForm.getDivisionName());
                    companiesEntity.setDescription(companyForm.getDescription());
                    companiesEntity.setStatus(companyForm.getStatus());

                    UsersEntity usersEntity = userService.getUser(companiesEntity.getUsersId());
                    if (usersEntity != null) {
                        usersEntity.setPasswd(Optional.ofNullable(companyForm.getPasswd()).orElse("").trim());
                        usersEntity.setFirstName(companyForm.getFirstName().trim());
                        usersEntity.setLastName(companyForm.getLastName().trim());
                        usersEntity.setLoginId(companyForm.getLoginId().trim());
                        usersEntity.setUserEmail(companyForm.getUserEmail());
                        if (companyForm.getStatus() == StatusEnum.ACTIVE.getValue()) {
                            usersEntity.setStatus(true);
                        } else {
                            usersEntity.setStatus(false);
                        }

                        messages = companiesService.update(usersEntity, companiesEntity);
                        if (CollectionUtils.isEmpty(messages)) {
                            if (companiesEntity.getStatus() == StatusEnum.ACTIVE.getValue()) {
                                companyUsageService.updateCompanyUsage(companiesEntity.getId());
                            }

                            SessionUtil.setAttribute("modeEdit", null);
                            SessionUtil.setAttribute("id", null);
                            SessionUtil.setAttribute("addEditResult", true);
                            return redirect("companyList");
                        }

                    } else {
                        messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_EMPTY, new String[]{id.toString()}));
                        model.addAttribute(Constant.Session.MESSAGES, messages);
                        model.addAttribute("companyForm", companyForm);
                        return "companies/addCompany";
                    }
                } else {
                    messages.add(messageSource.getWithParamKeys(Constant.ErrorCode.NOT_FOUND_DATA, new String[]{" user id of company " + id}));
                    model.addAttribute(Constant.Session.MESSAGES, messages);
                    model.addAttribute("companyForm", companyForm);
                    return "companies/addCompany";
                }
            }
        }
        if (!CollectionUtils.isEmpty(messages)) {
            model.addAttribute(Constant.Session.MESSAGES, messages);
            model.addAttribute("companyForm", companyForm);
            return "companies/addCompany";
        } else {
            SessionUtil.setAttribute("modeEdit", null);
            SessionUtil.setAttribute("addEditResult", true);
            return redirect("companyList");
        }
    }

    @RequestMapping(value = "updateCompany", method = RequestMethod.POST)
    public String update(@Valid @ModelAttribute("companyForm") CompanyForm companyForm, BindingResult bindingResult, ModelMap model)
        throws CommonException {
        // Validation Data Input.
        List<Message> messages = validatorUtil.validate(bindingResult);
        if (CollectionUtils.isEmpty(messages)) {
            messages = companiesService.validateForm(companyForm, true);
        }
        if (CollectionUtils.isEmpty(messages)) {
            DivisionsEntity companiesEntity = ConversionUtil.mapper(companyForm, DivisionsEntity.class);
            companiesService.updateCompany(companiesEntity);
            return redirect("searchCompanies");
        } else {
            model.addAttribute(Constant.Session.MESSAGES, messages);
            model.addAttribute("companyForm", companyForm);
            return "companies/addCompany";
        }
    }
}
