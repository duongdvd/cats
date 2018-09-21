package jp.co.willwave.aca.advice;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Properties;

@ControllerAdvice(
        basePackages = "jp.co.willwave.aca.controller")
@Component
public class AttributeInitializationControllerAdvice {
    /**
     * ロガー定義
     */
    private static Logger logger = Logger.getLogger(AttributeInitializationControllerAdvice.class);

    /**
     * 設定プロパティ
     */
    @Autowired
    protected Properties configProperties;

    /**
     * 共通属性設定
     *
     * @param model
     */
    @ModelAttribute
    public void addCommonAttribute(Model model) {
        logger.debug("addCommonAttribute");
        model.addAttribute("version", configProperties.getProperty("version"));
    }
}
