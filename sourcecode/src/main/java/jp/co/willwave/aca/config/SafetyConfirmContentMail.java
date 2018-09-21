package jp.co.willwave.aca.config;

import jp.co.willwave.aca.model.constant.Constant;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@Configuration
@PropertySource(value = "classpath:safetyconfirmmailcontentconfig.properties", encoding = Constant.UTF_8)
public class SafetyConfirmContentMail {

    @Value("${safety.confirm.content.subject}")
    private String safetyConfirmContentSubject;
    @Value("${safety.confirm.content.template.file.path}")
    private String safetyConfirmContentTemplateFilePath;
}
