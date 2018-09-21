package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.report.DeviceForMonthlyReportDTO;
import jp.co.willwave.aca.dto.report.RouteForMonthlyReportScreenDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.web.form.report.ReportFormSearch;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

public interface ReporterService {
    List<RouteForMonthlyReportScreenDTO> searchRouteReportForScreen(ReportFormSearch reportFormSearch, Integer offset, Integer maxResults, Boolean noPaging) throws CommonException;

    List<DeviceForMonthlyReportDTO> searchRouteReportForDownload(ReportFormSearch reportFormSearch) throws CommonException;

    Long countRouteReport(ReportFormSearch reportFormSearch);

    void printMonthlyReport(Workbook book, ReportFormSearch reportFormSearch) throws CommonException;
}
