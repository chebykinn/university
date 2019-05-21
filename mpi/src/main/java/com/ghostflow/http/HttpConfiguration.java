package com.ghostflow.http;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.net.CookieManager;
import java.net.CookiePolicy;

@Configuration
public class HttpConfiguration {

//    @Bean
//    public ErrorAttributes errorAttributes() {
//        return new DefaultErrorAttributes() {
//            @Override
//            public Map getErrorAttributes(RequestAttributes requestAttributes, boolean includeStackTrace) {
//                Map errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
//
//                Map error = new HashMap<>();
//                error.put("httpStatus", errorAttributes.get("status"));
//                error.put("error", Collections.singletonMap("message", errorAttributes.get("error")));
//
//                return error;
//            }
//        };
//    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OkHttpClient httpClient() {
        return new OkHttpClient.Builder()
                .cookieJar(new JavaNetCookieJar(new CookieManager(null, CookiePolicy.ACCEPT_ALL)))
                .build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                    .allowedMethods("HEAD", "GET", "PUT", "POST", "DELETE", "PATCH");
            }
        };
    }
}
