package egovframework.com.security;

import arami.logs.UserActivityInterceptor;
import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;

import egovframework.com.config.HtmlCharacterEscapes;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * fileName       : WebMvcConfig
 * author         : crlee
 * date           : 2023/07/13
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023/07/13        crlee       최초 생성
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	private final ObjectMapper objectMapper;

    private final UserActivityInterceptor userActivityInterceptor;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new CustomAuthenticationPrincipalResolver());
    }

    @Bean
    public HttpMessageConverter<?> htmlEscapingConverter() {
        ObjectMapper copy = objectMapper.copy();
        copy.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
        return new MappingJackson2HttpMessageConverter(copy);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userActivityInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico",
                        "/error",
                        "/*.html",
                        "/static/**"
                );
    }

    /**
     * multipart/form-data 의 {@code @RequestPart("data")} 에 파일로 JSON을 붙일 때,
     * 클라이언트(Postman 등)가 파트 Content-Type 을 {@code application/octet-stream} 으로 보내는 경우가 있어
     * Jackson 이 해당 파트도 JSON 으로 읽을 수 있게 한다.
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof MappingJackson2HttpMessageConverter jackson) {
                ArrayList<MediaType> types = new ArrayList<>(jackson.getSupportedMediaTypes());
                if (types.stream().noneMatch(mt -> MediaType.APPLICATION_OCTET_STREAM.equals(mt))) {
                    types.add(MediaType.APPLICATION_OCTET_STREAM);
                    jackson.setSupportedMediaTypes(types);
                }
            }
        }
    }

}