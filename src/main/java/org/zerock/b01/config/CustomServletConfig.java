package org.zerock.b01.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration //이 클래스가 Spring의 구성 클래스임을 나타냄
@EnableWebMvc // Spring MVC 구성을 활성화
public class CustomServletConfig implements WebMvcConfigurer {
//프로젝트의 HTML 파일에서 이러한 정적 리소스를 쉽게 사용할 수 있으며, 이를 통해 웹 페이지의 디자인과 동작을 구현할 수 있습니다.
    @Override // static폴더 제외 처리
    public void addResourceHandlers(ResourceHandlerRegistry registry) { //정적 리소스의 요청 경로와 해당 리소스가 위치한 실제 경로를 매핑

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
        registry.addResourceHandler("/fonts/**")
                .addResourceLocations("classpath:/static/fonts/");
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
        registry.addResourceHandler("/assets/**").
                addResourceLocations("classpath:/static/assets/");

    }

}
