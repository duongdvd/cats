package jp.co.willwave.aca.web.controller;

import jp.co.willwave.aca.config.ExportConfig;
import jp.co.willwave.aca.dto.api.DivisionDTO;
import jp.co.willwave.aca.dto.report.RouteForDailyReportScreenDTO;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.service.ExportService;
import jp.co.willwave.aca.utilities.FileUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.SearchExportDailyForm;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.*;

@Controller
public class ExportDailyController extends AbstractController {

    private final ExportService exportService;
    private final ExportConfig exportConfig;

    @Autowired
    public ExportDailyController(ExportService exportService, ExportConfig exportConfig) {
        this.exportService = exportService;
        this.exportConfig = exportConfig;
    }

    @RequestMapping(value = {"/exportDailyList"}, method = RequestMethod.GET)
    public String exportDailyView(ModelMap model, Integer offset, Integer maxResults) throws ParseException {
        SearchExportDailyForm searchExportDailyForm = (SearchExportDailyForm) SessionUtil.getAttribute("SearchExportDailyForm");


        List<RouteForDailyReportScreenDTO> routeForDailyReportScreenDTOList = exportService.searchDailyReportForScreen(null, offset, maxResults, true);

        HashSet<DivisionDTO> divisionIdList = new HashSet<>();
        if (!CollectionUtils.isEmpty(routeForDailyReportScreenDTOList)) {
            for (RouteForDailyReportScreenDTO routeForDailyReportScreenDTO : routeForDailyReportScreenDTOList) {
                DivisionDTO divitionDTO = new DivisionDTO();
                divitionDTO.setDivisionId(routeForDailyReportScreenDTO.getDivisionId());
                if (StringUtils.isNotBlank(routeForDailyReportScreenDTO.getDivisionName())) {
                    divitionDTO.setDivisionName(routeForDailyReportScreenDTO.getDivisionName());
                }
                divisionIdList.add(divitionDTO);
            }
        }

        searchExportDailyForm = Optional.ofNullable(searchExportDailyForm).orElse(new SearchExportDailyForm());
        routeForDailyReportScreenDTOList = exportService.searchDailyReportForScreen(searchExportDailyForm, offset, maxResults, false);
        Integer count = exportService.searchDailyReportForScreen(searchExportDailyForm, offset, maxResults, true).size();

        Map<String, List<RouteForDailyReportScreenDTO>> map = new HashMap<>();

        model.addAttribute("SearchExportDailyForm", searchExportDailyForm);
        model.addAttribute("divisionIdList", divisionIdList);
        model.addAttribute("count", count);
        model.addAttribute("offset", offset);
        model.addAttribute("map", map);
        model.addAttribute("exportList", routeForDailyReportScreenDTOList);
        model.addAttribute(Constant.Session.MESSAGES, SessionUtil.getAndRemoveMessages());
        return "export/daily";
    }

    @RequestMapping(value = "/searchExportDaily", method = RequestMethod.GET)
    public String search(@ModelAttribute("SearchExportDailyForm") SearchExportDailyForm searchExportDailyForm, Integer offset, Integer maxResults) {
        logger.info("search export daily list");

        SessionUtil.setAttribute("SearchExportDailyForm", searchExportDailyForm);
        return redirect("exportDailyList");
    }

    @RequestMapping(value = "/searchExportDailyPaging", method = RequestMethod.GET)
    public String searchPaging(Integer offset, Integer maxResults) {
        logger.info("search export daily paging");
        return pagingRedirectToList("exportDailyList", offset, maxResults);
    }

    @RequestMapping(value = "/downloadDailyReport", method = RequestMethod.POST)
    public String downloadDailyReport(DailyReportParam selectedRouteIds, HttpServletResponse response) throws Exception {
        SearchExportDailyForm searchExportDailyForm = (SearchExportDailyForm) SessionUtil.getAttribute("SearchExportDailyForm");
        searchExportDailyForm = Optional.ofNullable(searchExportDailyForm).orElse(new SearchExportDailyForm());
        // set list of selected route
        searchExportDailyForm.setSelectedRouteIds(selectedRouteIds.getRouteIds());
        String actualDate = "";
        if(StringUtils.isNotBlank(searchExportDailyForm.getActualDate())) {
            actualDate = searchExportDailyForm.getActualDate();
        }

        String exportName = String.format(exportConfig.getDailyExportName(), actualDate);

        SearchExportDailyForm finalSearchExportDailyForm = searchExportDailyForm;
        FileUtil.downloadExcel(response, exportConfig.getDailyTemplateName(), exportName, (workbook) -> {
            exportService.printDailyReport(workbook, finalSearchExportDailyForm);
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
