package com.memo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.memo.common.FileManagerService;
import com.memo.interceptor.PermissionInterceptor;

@Configuration // 설정을 위한 spring bean
public class WebMvcConfig implements WebMvcConfigurer {//인터페이스 구현
	
	@Autowired
	private PermissionInterceptor interceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry
		.addInterceptor(interceptor)
		.addPathPatterns("/**")
		.excludePathPatterns("/error", "/static/**")
		;
	}
	
	
	// 웹 이미지 path와 서버에 업로드 된 이미지와 매핑 설정 -> 주소를 치고 들어가면 해당 이미지를 브라우저에서 볼수있는 방법
	@Override
	// 스프링부트 에서 이미지업로드 매핑 설정
	public void addResourceHandlers(ResourceHandlerRegistry registry) {// 주소를 치고들어가면 이미지를 볼수있는것/spring boot에서 웹이미지 매핑 설정 !!
		registry
		.addResourceHandler("/images/**")  // web image path  http://localhost/images/null_1699018082972
		.addResourceLocations("file:///" + FileManagerService.FILE_UPLOAD_PATH); // 실제 파일 위치
	}
}