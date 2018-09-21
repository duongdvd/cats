package jp.co.willwave.aca.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        List<Parameter> parameters = new ArrayList<>();
        Parameter parameter = new ParameterBuilder()
                .name("token")
                .description("Description of header")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(true)
                .build();
        parameters.add(parameter);
        List<ApiKey> apiKeys = new ArrayList<>();
        apiKeys.add(apiKey());
        return new Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(apiKeys)
                .globalOperationParameters(parameters)
                .select()
                .apis(RequestHandlerSelectors.basePackage("jp.co.willwave.aca.web.restController"))
                .paths(regex("/api.*"))
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey("abcdef12345", "api_key", "header");
    }
}
