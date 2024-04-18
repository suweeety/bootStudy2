package org.zerock.b01.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import springfox.documentation.builders.ApiInfoBuilder;
//import springfox.documentation.builders.PathSelectors;
//import springfox.documentation.builders.RequestHandlerSelectors;
//import springfox.documentation.service.ApiInfo;
//import springfox.documentation.spi.DocumentationType;
//import springfox.documentation.spring.web.plugins.Docket;

@OpenAPIDefinition( // OpenAPI 정의를 설정하는 데 사용됩니다. 여기서는 title과 version 정보를 제공
        info = @Info(title = "Zerock App",version = "v1"))
@RequiredArgsConstructor //생성자 자동주입
@Configuration //스프링의 구성 클래스임을 나타냄. 스프링 빈을 설정하는데 사용
public class SwaggerConfig {  // json 테스트용 코드 필수 -> http://localhost:8080/swagger-ui/index.html
//Swagger의 GroupedOpenApi 빈을 생성. 이 빈은 특정 API 그룹에 대한 Swagger 정의를 나타냄.
    @Bean
    public GroupedOpenApi chatOpenApi() {
        String[] paths = {"/**"};

        return GroupedOpenApi.builder()
                .group("Zerock OPEN API v1")
                .pathsToMatch(paths)
                .build();
    }



    // 부트 3 안됨
    // json 테스트용 코드 필수 -> http://localhost/swagger-ui/index.html
    // CustomServletConfig 필수
//    @Bean
//    public Docket api() {
//        return new Docket(DocumentationType.OAS_30)
//                .useDefaultResponseMessages(false)
//                .select()
//                .apis(RequestHandlerSelectors.basePackage("org.zerock.b01.controller"))
//                .paths(PathSelectors.any())
//                .build()
//                .apiInfo(apiInfo());
//
//    }
//
//    private ApiInfo apiInfo() {
//        return new ApiInfoBuilder()
//                .title("Boot 01 Project Swagger")
//                .build();
//    }
}