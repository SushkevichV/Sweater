package by.sva.sweater.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import by.sva.sweater.util.RedirectInterceptor;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	@Value("${upload.path}") // получает значение переменной upload.path из application.properties
	private String uploadPath;
	
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/login").setViewName("login");
	}
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/img/**")
				.addResourceLocations("file://" + uploadPath + "/");
		registry.addResourceHandler("/static/**")
				.addResourceLocations("classpath:/static/");
	}
	
	// Регистрация класса, который уведомляет turbolink о редиректах
	// Применяется при использовании turbolink
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new RedirectInterceptor());
	}

}
