package jp.co.willwave.aca.batch;

import jp.co.willwave.aca.exception.CommonException;
import jp.co.willwave.aca.model.constant.Constant;
import jp.co.willwave.aca.model.entity.MasterSysConfigsEntity;
import jp.co.willwave.aca.model.enums.UserRole;
import jp.co.willwave.aca.service.*;
import jp.co.willwave.aca.utilities.ApplicationContextUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class SafetyConfirmBatch extends BaseService {

    private final TaskScheduler taskScheduler;
    private final SafetyConfirmService safetyConfirmService;
    private final MasterSysConfigsService masterSysConfigsService;
    private final CompanyUsageService companyUsageService;
    private final AccessService accessService;

    public SafetyConfirmBatch(TaskScheduler taskScheduler,
                              SafetyConfirmService safetyConfirmService,
                              MasterSysConfigsService masterSysConfigsService,
                              CompanyUsageService companyUsageService,
                              AccessService accessService) {
        this.taskScheduler = taskScheduler;
        this.safetyConfirmService = safetyConfirmService;
        this.masterSysConfigsService = masterSysConfigsService;
        this.companyUsageService = companyUsageService;
        this.accessService = accessService;
    }

    @Value("${spring.time.check}")
    private Long time;
    @Value("${usage.cron.expression}")
    private String cronExpression;
    @Value("${usage.cron.timezone}")
    private String timeZoneString;

    @PostConstruct
    public void init() {
        startCheckSafetyConfirm();
        exportChange();
        setAccessPaths();
    }

    private void startCheckSafetyConfirm() {
        Long period = time;
        try {
            MasterSysConfigsEntity masterSysConfigsEntity = masterSysConfigsService.findByKey
                    (Constant.ConfigKey.TIME_CHECK_SAFETY_CONFIRM);
            if (masterSysConfigsEntity != null) {
                period = Long.valueOf(masterSysConfigsEntity.getValue());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        PeriodicTrigger periodicTrigger = new PeriodicTrigger(period, TimeUnit.MILLISECONDS);
        periodicTrigger.setFixedRate(true);
        taskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                safetyConfirmService.checkSafetyConfirmDevices();
            }
        }, periodicTrigger);
    }

    private void exportChange() {
        TimeZone timezone = TimeZone.getTimeZone(timeZoneString);
        CronTrigger cronTrigger = new CronTrigger(cronExpression, timezone);
        taskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                try {
                    companyUsageService.updateUsageForMonth();
                } catch (CommonException e) {

                }
            }
        }, cronTrigger);
    }

    /**
     * set access paths list {@link jp.co.willwave.aca.model.enums.UserRole NONE}
     */
    private void setAccessPaths() {
        ApplicationContextUtil.setAccessPaths(accessService.getByRoleId(UserRole.NONE.getRole()));
    }
}
