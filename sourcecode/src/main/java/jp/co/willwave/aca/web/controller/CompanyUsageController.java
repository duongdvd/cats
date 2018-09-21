package jp.co.willwave.aca.web.controller;


import jp.co.willwave.aca.dto.api.CompanyUsageDTO;
import jp.co.willwave.aca.dto.api.DivisionDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.service.CompanyUsageService;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.SearchEmployeeForm;
import jp.co.willwave.aca.web.form.SearchUsageForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.text.ParseException;
import java.util.HashSet;
import java.util.List;

import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

@Controller
public class CompanyUsageController extends AbstractController {

    private final CompanyUsageService companyUsageService;

    @Autowired
    public CompanyUsageController(CompanyUsageService companyUsageService) {
        this.companyUsageService = companyUsageService;
    }

    @RequestMapping(value = {"/usageStatus"}, method = RequestMethod.GET)
    public String usageStatusList(ModelMap model, Integer offset, Integer maxResults) throws ParseException {
        HashSet<DivisionDTO> companyIdList = new HashSet<>();
        List<DivisionsEntity> companyList = companyUsageService.getCompanyList();
        if (!CollectionUtils.isEmpty(companyList)) {
            for (DivisionsEntity company : companyList) {
                DivisionDTO divisionDTO = new DivisionDTO();
                divisionDTO.setDivisionId(company.getId());
                divisionDTO.setDivisionName(company.getDivisionName());
                companyIdList.add(divisionDTO);
            }
        }

        SearchUsageForm searchUsageForm = (SearchUsageForm) SessionUtil.getAttribute("SearchUsageForm");
        searchUsageForm = (searchUsageForm == null ? new SearchUsageForm() : searchUsageForm);
        model.addAttribute("SearchUsageForm", searchUsageForm);

        List<CompanyUsageDTO> companyUsageDTOList = companyUsageService.searchCompanyUsageList(searchUsageForm, offset, maxResults, true);
        model.addAttribute("companyUsageDTOList", companyUsageDTOList);
        model.addAttribute("companyIdList", companyIdList);

        Integer count = companyUsageService.searchCompanyUsageList(searchUsageForm, offset, maxResults, false).size();
        model.addAttribute("count", count);
        model.addAttribute("offset", offset);

        return "usage/usageStatus";
    }

    @RequestMapping(value = "/searchUsageStatusPaging", method = RequestMethod.GET)
    public String searchReportPaging(Integer offset, Integer maxResults) {
        logger.info("search usage status list paging");
        return pagingRedirectToList("usageStatus", offset, maxResults);
    }

    @RequestMapping(value = "/searchUsageStatus", method = RequestMethod.GET)
    public String searchReport(@ModelAttribute("SearchUsageForm") SearchUsageForm searchUsageForm) {
        logger.info("search usage status");
        SessionUtil.setAttribute("SearchUsageForm", searchUsageForm);
        return redirect("usageStatus");
    }

    @RequestMapping(value = "/testNewMonth", method = RequestMethod.GET)
    public String testNewMonth() throws CommonException {
        companyUsageService.updateUsageForMonth();
        return redirect("usageStatus");
    }
}
