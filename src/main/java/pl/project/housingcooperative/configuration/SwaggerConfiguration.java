package pl.project.housingcooperative.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration{

    @Bean
    public Docket smppClientSimulatorApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(
                        new ApiInfoBuilder()
                                .termsOfServiceUrl(null)
                                .version("1.0.0")
                                .title("społdzielnia mieszkaniowa wita i żegna ")
                                .build()
                )
                .ignoredParameterTypes(AuthenticationPrincipal.class)
                .select().apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .build();

    }

}