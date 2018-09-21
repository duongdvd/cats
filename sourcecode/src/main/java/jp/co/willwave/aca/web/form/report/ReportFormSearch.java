package jp.co.willwave.aca.web.form.report;

import jp.co.willwave.aca.constants.DateConstant;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.utilities.DateUtil;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ReportFormSearch {
    private Long divisionId;
    private String driverName;
    private String plateNumber;
    private String month = DateUtil.convertSimpleDateFormat(new Date(), DateConstant.DATE_MONTH_FORMAT);

    /**
     * is the login user id when the login user is OPERATOR
     */
    private Long userId;
    /**
     * is the list of managed divisions id when the login user is DIVISION/COMPANY ADMIN
     */
    private List<Long> managedDivisionIds;

    /**
     * list of selected route for export<br/>
     * Manually set value in Controller
     */
    private List<Integer> selectedRouteIds;
}
