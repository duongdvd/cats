package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dto.report.RouteForDailyReportScreenDTO;
import jp.co.willwave.aca.dto.report.RouteForDailyReportDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.web.form.SearchExportDailyForm;
import org.apache.poi.ss.usermodel.Workbook;

import java.text.ParseException;
import java.util.List;

public interface ExportService {

    /**
     * search data of daily report for displayed on the screen<br/>
     * if login user is operator, then get data relate to routes which create by login user
     * @param searchExportDailyForm search form
     * @param offset
     * @param maxResults
     * @param noPaging if true then get all
     * @return
     * @throws ParseException
     */
    List<RouteForDailyReportScreenDTO> searchDailyReportForScreen(SearchExportDailyForm searchExportDailyForm, Integer offset, Integer maxResults, Boolean noPaging) throws ParseException;

    /**
     * search data of daily report for download
     * @param searchExportDailyForm search form
     * @return
     * @throws ParseException
     */
    List<RouteForDailyReportDTO> searchDailyReportForDownload(SearchExportDailyForm searchExportDailyForm) throws ParseException;

    /**
     *  daily report print handler
     * @param workbook workbook
     * @param searchExportDailyForm
     * @throws CommonException
     */
    void printDailyReport(Workbook workbook, SearchExportDailyForm searchExportDailyForm) throws CommonException, ParseException;
}
