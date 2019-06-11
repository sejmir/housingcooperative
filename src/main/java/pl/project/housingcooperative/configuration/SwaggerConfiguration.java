package pl.project.housingcooperative.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

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
                .paths(PathSelectors.any())
                .build()
                .securitySchemes(List.of(apiKey()))
                .securityContexts(List.of(securityContext()));

    }

    private static ApiKey apiKey() {
        return new ApiKey("Basic Auth", "AUTHORIZATION", "header");
    }

    private static SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(path -> isPathSecured(path))
                .build();
    }

    private static List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference("JWT", authorizationScopes));
    }

    private static boolean isPathSecured(String servletPath) {
        return BasicAuthHeaderFilter.SECURED_PATH_MATCHERS.stream()
                .map(PathSelectors::ant)
                .anyMatch(pathSelector -> pathSelector.apply(servletPath));
    }

}