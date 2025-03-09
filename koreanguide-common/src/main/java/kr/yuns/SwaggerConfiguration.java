package kr.yuns;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;

@OpenAPIDefinition(
        info = @Info(title = "KOREAN GUIDE API Spec",
                description = "KOREAN GUIDE API Spec",
                version = "v1"))
@RequiredArgsConstructor
@Configuration
public class SwaggerConfiguration {
    // @Bean
    // public GroupedOpenApi chatOpenApi() {
    //     // String[] paths = {"/v1/**"};

    //     return GroupedOpenApi.builder()
    //             .group("KOREAN GUIDE API (v1)")
    //             // .pathsToMatch(paths)
    //             .build();
    // }
}