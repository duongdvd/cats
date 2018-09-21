package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.config.ExportConfig;
import jp.co.willwave.aca.dto.report.RouteForMonthlyReportScreenDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.service.DivisionService;
import jp.co.willwave.aca.service.ReporterService;
import jp.co.willwave.aca.utilities.FileUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.report.DivisionForm;
import jp.co.willwave.aca.web.form.report.ReportFormSearch;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ReporterMonthController extends AbstractController {
    private final ReporterService reporterService;
    private final DivisionService divisionService;
    private final ExportConfig exportConfig;

    public ReporterMonthController(ReporterService reporterService,
                                   DivisionService divisionService, ExportConfig exportConfig) {
        this.reporterService = reporterService;
        this.divisionService = divisionService;
        this.exportConfig = exportConfig;
    }

    @RequestMapping(value = "/reportMonth", method = RequestMethod.GET)
    public String reportView(Model model, Integer offset, Integer maxResults) throws CommonException {
        UserInfo loginUser = SessionUtil.getLoginUser();
        ReportFormSearch reportFormSearch = (ReportFormSearch) SessionUtil.getAttribute("searchReportForm");

        if (reportFormSearch == null) {
            reportFormSearch = new ReportFormSearch();
        }
        reportFormSearch.setDriverName(StringUtils.trimWhitespace(reportFormSearch.getDriverName()));
        reportFormSearch.setPlateNumber(StringUtils.trimWhitespace(reportFormSearch.getPlateNumber()));

        // find list of managed division by login user
        List<DivisionForm> divisionList = divisionService.getDivisionForm(loginUser.getId());
        if (loginUser.isOperator()) {
            reportFormSearch.setUserId(loginUser.getId());
        } else if (reportFormSearch.getDivisionId() == null) {
            List<Long> managedDivisionIds = divisionList.stream().map(d -> d.getId()).collect(Collectors.toList());
            reportFormSearch.setManagedDivisionIds(managedDivisionIds);
        } else {
            List<Long> managedDivisionIds = divisionService.getListFamilyDivision(reportFormSearch.getDivisionId())
                    .stream().map(d -> d.getId()).collect(Collectors.toList());
            reportFormSearch.setManagedDivisionIds(managedDivisionIds);
        }

        List<RouteForMonthlyReportScreenDTO> routeReports = reporterService.searchRouteReportForScreen(reportFormSearch, offset, maxResults, false);
        Long count = reporterService.countRouteReport(reportFormSearch);

        model.addAttribute("searchReportForm", reportFormSearch);
        model.addAttribute("divisionList", divisionList);
        model.addAttribute("routesReport", routeReports);
        model.addAttribute("offset", offset);
        model.addAttribute("maxResults", maxResults);
        model.addAttribute("count", count);

        return "/report/ReportMonth";
    }

    @RequestMapping(value = "/searchReportMonthPaging", method = RequestMethod.GET)
    public String searchReportPaging(Integer offset, Integer maxResults) {
        logger.info("search route actual list paging");
        return pagingRedirectToList("reportMonth", offset, maxResults);
    }

    @RequestMapping(value = "/searchReportMonth", method = RequestMethod.GET)
    public String searchReport(@ModelAttribute("searchReportForm") ReportFormSearch searchReportForm) {
        logger.info("search Report list paging");
        SessionUtil.setAttribute("searchReportForm", searchReportForm);
        return redirect("reportMonth");
    }

    @RequestMapping(value = "/downloadMonthReport", method = RequestMethod.POST)
    public String downloadMonthReport(DailyReportParam selectedRouteIds, HttpServletResponse response) throws Exception {
        ReportFormSearch reportFormSearch = (ReportFormSearch) SessionUtil.getAttribute("searchReportForm");
        UserInfo loginUser = SessionUtil.getLoginUser();
        if (reportFormSearch == null) {
            reportFormSearch = new ReportFormSearch();
            reportFormSearch.setDriverName(StringUtils.trimWhitespace(reportFormSearch.getDriverName()));
            reportFormSearch.setPlateNumber(StringUtils.trimWhitespace(reportFormSearch.getPlateNumber()));
        }
        // set list of selected route
        reportFormSearch.setSelectedRouteIds(selectedRouteIds.getRouteIds());
        if (loginUser.isOperator()) {
            reportFormSearch.setUserId(loginUser.getId());
        } else if (reportFormSearch.getDivisionId() == null){
            // find list of managed division by login user
            List<DivisionForm> divisionList = divisionService.getDivisionForm(loginUser.getId());
            List<Long> managedDivisionIds = divisionList.stream().map(d -> d.getId()).collect(Collectors.toList());
            reportFormSearch.setManagedDivisionIds(managedDivisionIds);
        } else {
            // find list of managed division by division id
            List<Long> managedDivisionIds = divisionService.getListFamilyDivision(reportFormSearch.getDivisionId())
                    .stream().map(d -> d.getId()).collect(Collectors.toList());
            reportFormSearch.setManagedDivisionIds(managedDivisionIds);
        }

        // format file name
        String yearMonth = "";
        if(reportFormSearch !=null && !StringUtils.isEmpty(reportFormSearch.getMonth())) {
            yearMonth = reportFormSearch.getMonth();
        }
        String exportName = String.format(exportConfig.getMonthExportName(), yearMonth);

        ReportFormSearch finalReportFormSearch = reportFormSearch;
        FileUtil.downloadExcel(response, exportConfig.getMonthTemplateName(), exportName, (workbook) -> {
            reporterService.printMonthlyReport(workbook, finalReportFormSearch);
        });
        return "";
    }

    /**
     * use to bind list of selected route id for export
     */
    @Data
    @NoArgsConstructor
    public static class DailyReportParam {
        private List<Integer> routeIds;
    }
}
