package jp.co.willwave.aca.service;

import jp.co.willwave.aca.config.ExportConfig;
import jp.co.willwave.aca.dao.DivisionsDao;
import jp.co.willwave.aca.dao.RouteDetailDao;
import jp.co.willwave.aca.dao.RoutesDao;
import jp.co.willwave.aca.dto.report.DeviceForMonthlyReportDTO;
import jp.co.willwave.aca.dto.report.RouteForMonthlyReportScreenDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.utilities.CatStringUtil;
import jp.co.willwave.aca.utilities.DateUtil;
import jp.co.willwave.aca.utilities.FileUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.report.ReportFormSearch;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static jp.co.willwave.aca.utilities.DateUtil.DD;

@Service
@Transactional(rollbackFor = Exception.class)
public class ReporterServiceImpl implements ReporterService {
    private static Logger logger = Logger.getLogger(ReporterServiceImpl.class);

    private final RoutesDao routesDao;
    private final RouteDetailDao routeDetailDao;
    private final ExportConfig exportConfig;
    private final DivisionService divisionService;
    private final DivisionsDao divisionsDao;

    @Autowired
    public ReporterServiceImpl(RoutesDao routesDao, RouteDetailDao routeDetailDao, ExportConfig exportConfig, DivisionService divisionService, DivisionsDao divisionsDao) {
        this.routesDao = routesDao;
        this.routeDetailDao = routeDetailDao;
        this.exportConfig = exportConfig;
        this.divisionService = divisionService;
        this.divisionsDao = divisionsDao;
    }

    @Override
    public List<RouteForMonthlyReportScreenDTO> searchRouteReportForScreen(ReportFormSearch reportFormSearch, Integer offset, Integer maxResults, Boolean noPaging) throws CommonException {
        List<RouteForMonthlyReportScreenDTO> routesActualObject = routesDao.searchReportMonthForScreen(reportFormSearch, offset, maxResults, noPaging);
        return routesActualObject;
    }

    @Override
    public Long countRouteReport(ReportFormSearch reportFormSearch) {
        return routesDao.countSearchReportMonth(reportFormSearch);
    }

    @Override
    public List<DeviceForMonthlyReportDTO> searchRouteReportForDownload(ReportFormSearch reportFormSearch) throws CommonException {
        return routesDao.searchReportMonthForDownload(reportFormSearch);
    }
    @Override
    public void printMonthlyReport(Workbook workbook, ReportFormSearch reportFormSearch) throws CommonException {
        // get company name
        UserInfo loginUser = SessionUtil.getLoginUser();
        DivisionsEntity company = divisionService.findCompanyByDivisionId(loginUser.getDivisionsId());
        String companyName = company == null? "" : company.getDivisionName();

        Sheet sheet0 = workbook.getSheetAt(0);
        // search export data
        List<DeviceForMonthlyReportDTO> datas = this.searchRouteReportForDownload(reportFormSearch);
        YearMonth yearMonth = YearMonth.parse(reportFormSearch.getMonth(), DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DASH));

        FileUtil.copySheet(sheet0, (sheet, device) -> {
            // set sheet name (deviceId_loginId)
            workbook.setSheetName(workbook.getSheetIndex(sheet), String.join("_",
                    CatStringUtil.stringValue(device.getDeviceId()),
                    FileUtil.replaceSheetNameInvalidChar(device.getLoginId())));

            FileUtil.cell(sheet, "year").setCellValue(yearMonth.getYear());
            FileUtil.cell(sheet, "month").setCellValue(yearMonth.getMonthValue());
            FileUtil.cell(sheet, "title").setCellValue(String.join(" ", companyName, device.getDivisionName()));
            FileUtil.cell(sheet, "loginId").setCellValue(device.getLoginId());
            FileUtil.cell(sheet, "carInfo").setCellValue(String.join(" ", device.getCarMaker(), device.getCarType()));
            FileUtil.cell(sheet, "plateNumber").setCellValue(device.getPlateNumber());

            FileUtil.verticalCopyRange(sheet, "row", 0, (range, route) -> {
                range.cell("actualStartDay").setCellValue(DateUtil.convertSimpleDateFormat(route.getActualStartDate(), DD));
                range.cell("actualDriverName").setCellValue(route.getActualDriverName());
                range.cell("totalDistance").setCellValue(route.getTotalDistance() != null? String.valueOf(route.getTotalDistance()): "");
                range.cell("routeChain").setCellValue(route.formatVisitedPlaces());
            }, device.getRoutes());
        }, datas);
    }
}
