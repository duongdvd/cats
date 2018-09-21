package jp.co.willwave.aca.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:exportconfig.properties")
public class ExportConfig {
    @Value("${cat.daily.template.name}")
    private String dailyTemplateName;
    @Value("${cat.month.template.name}")
    private String monthTemplateName;

    @Value("${cat.daily.export.name}")
    private String dailyExportName;
    @Value("${cat.month.export.name}")
    private String monthExportName;

    public ExportConfig() {
    }

    public String getDailyTemplateName() {
        return dailyTemplateName;
    }

    public String getMonthTemplateName() {
        return monthTemplateName;
    }

    public String getDailyExportName() {
        return dailyExportName;
    }

    public String getMonthExportName() {
        return monthExportName;
    }
}
