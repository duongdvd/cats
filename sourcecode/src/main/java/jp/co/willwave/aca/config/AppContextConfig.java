package jp.co.willwave.aca.config;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
@EnableWebMvc
public class AppContextConfig implements WebMvcConfigurer {
  public static String RESOURCE_SERVER = "";
  public static String RESOURCE_BACKUP_DB = "";

  @Override
  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
    configurer.enable();
  }


  @Bean(name = "multipartResolver")
  public MultipartResolver getMultipartResolver() {
    CommonsMultipartResolver resover = new CommonsMultipartResolver();
    // 2MB
    resover.setMaxUploadSize(2 * 1024 * 1024);
    return resover;
  }

  @InitBinder
  public void initBinder(WebDataBinder binder) {
    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
    sdf.setLenient(true);
    binder.registerCustomEditor(Date.class, new CustomDateEditor(sdf, true));
  }
}
