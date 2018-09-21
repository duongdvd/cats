package jp.co.willwave.aca.service;

import jp.co.willwave.aca.dao.DivisionsDao;
import jp.co.willwave.aca.dao.RouteDetailDao;
import jp.co.willwave.aca.dto.report.RouteForDailyReportScreenDTO;
import jp.co.willwave.aca.dto.report.RouteForDailyReportDTO;
import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.UserInfo;
import jp.co.willwave.aca.model.entity.DivisionsEntity;
import jp.co.willwave.aca.utilities.CatStringUtil;
import jp.co.willwave.aca.utilities.DateUtil;
import jp.co.willwave.aca.utilities.FileUtil;
import jp.co.willwave.aca.utilities.SessionUtil;
import jp.co.willwave.aca.web.form.SearchExportDailyForm;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static jp.co.willwave.aca.model.constant.Constant.Session.USER_LOGIN_INFO;

@Service
@Transactional(rollbackFor = Exception.class)
public class ExportServiceImpl implements ExportService {
    private final Logger logger = Logger.getLogger(this.getClass());

    private final DivisionsDao divisionsDao;

    private final RouteDetailDao routeDetailDao;

    private final DivisionService divisionService;

    @Autowired
    public ExportServiceImpl(DivisionsDao divisionsDao, RouteDetailDao routeDetailDao, DivisionService divisionService) {
        this.divisionsDao = divisionsDao;
        this.routeDetailDao = routeDetailDao;
        this.divisionService = divisionService;
    }

    @Override
    public List<RouteForDailyReportScreenDTO> searchDailyReportForScreen(SearchExportDailyForm searchExportDailyForm, Integer offset, Integer maxResults, Boolean noPaging) throws ParseException {
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();

        List<Long> divisionIdList = new ArrayList<>();
        List<DivisionsEntity> divisionsEntities = null;

        DivisionsEntity divisionManageByLoginUser = divisionsDao.findManagedDivisionByUserID(userLoginId);
        if (divisionManageByLoginUser != null) divisionIdList.add(divisionManageByLoginUser.getId());

        if (!CollectionUtils.isEmpty(divisionIdList)) divisionsEntities = divisionsDao.findDivisionChildListByDivisionId(divisionIdList.get(0));

        if (!CollectionUtils.isEmpty(divisionsEntities)) {
            for (DivisionsEntity d : divisionsEntities) {
                divisionIdList.add(d.getId());
            }
        }

        List<RouteForDailyReportScreenDTO> routeForDailyReportScreenDTOList = routeDetailDao.searchDailyReportForScreen(searchExportDailyForm, offset, maxResults, noPaging, divisionIdList);

        return routeForDailyReportScreenDTOList;
    }

    @Override
    public List<RouteForDailyReportDTO> searchDailyReportForDownload(SearchExportDailyForm searchExportDailyForm) throws ParseException {
        Long userLoginId = ((UserInfo) SessionUtil.getAttribute(USER_LOGIN_INFO)).getId();

        List<Long> divisionIdList = new ArrayList<>();
        List<DivisionsEntity> divisionsEntities = null;

        DivisionsEntity divisionManageByLoginUser = divisionsDao.findManagedDivisionByUserID(userLoginId);
        if (divisionManageByLoginUser != null) divisionIdList.add(divisionManageByLoginUser.getId());

        if (!CollectionUtils.isEmpty(divisionIdList)) divisionsEntities = divisionsDao.findDivisionChildListByDivisionId(divisionIdList.get(0));

        if (!CollectionUtils.isEmpty(divisionsEntities)) {
            for (DivisionsEntity d : divisionsEntities) {
                divisionIdList.add(d.getId());
            }
        }

        List<RouteForDailyReportDTO> exportDailyDTOList = routeDetailDao.searchDailyReportForDownload(searchExportDailyForm, divisionIdList);

        return exportDailyDTOList;
    }

    @Override
    public void printDailyReport(Workbook workbook, SearchExportDailyForm searchExportDailyForm) throws ParseException, CommonException {

        // get company name
        UserInfo loginUser = SessionUtil.getLoginUser();
        DivisionsEntity company = divisionService.findCompanyByDivisionId(loginUser.getDivisionsId());
        String companyName = company == null? "" : company.getDivisionName();

        Sheet sheet0 = workbook.getSheetAt(0);
        List<RouteForDailyReportDTO> datas = this.searchDailyReportForDownload(searchExportDailyForm);

        FileUtil.copySheet(sheet0, (sheet, route) -> {
            //set sheet name (routeId_routeName)
            workbook.setSheetName(workbook.getSheetIndex(sheet), String.join("_",
                    CatStringUtil.stringValue(route.getRouteId()),
                    FileUtil.replaceSheetNameInvalidChar(route.getRouteName())));

            FileUtil.cell(sheet, "title").setCellValue(String.join(" ", companyName, route.getDivisionName()));
            FileUtil.cell(sheet, "actualRouteName").setCellValue(route.getRouteName());
            FileUtil.cell(sheet, "startDate").setCellValue(DateUtil.convertSimpleDateFormat(route.getActualStartDateTime(), DateUtil.YYYY_MM_DD_SLASH));
            FileUtil.cell(sheet, "driverName").setCellValue(route.getActualDriverName());
            FileUtil.cell(sheet, "plateNumber").setCellValue(route.getActualPlateNumber());
            FileUtil.cell(sheet, "planStartPlace").setCellValue(route.getPlanStartPlace());
            FileUtil.cell(sheet, "planEndPlace").setCellValue(route.getPlanEndPlace());
            FileUtil.cell(sheet, "actualStartTime").setCellValue(DateUtil.convertSimpleDateFormat(route.getActualStartDateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS_SLASH));
            FileUtil.cell(sheet, "actualEndTime").setCellValue(DateUtil.convertSimpleDateFormat(route.getActualEndDateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS_SLASH));

            FileUtil.verticalCopyRange(sheet, "row", 0, (range, routeDetail) -> {
                range.cell("startPlace").setCellValue(routeDetail.getStartPlace());
                range.cell("startTime").setCellValue(DateUtil.convertSimpleDateFormat(routeDetail.getStartDateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS_SLASH));
                range.cell("endPlace").setCellValue(routeDetail.getEndPlace());
                range.cell("endTime").setCellValue(DateUtil.convertSimpleDateFormat(routeDetail.getEndDateTime(), DateUtil.YYYY_MM_DD_HH_MM_SS_SLASH));
            }, route.getRouteDetails());
        }, datas);
    }
}
