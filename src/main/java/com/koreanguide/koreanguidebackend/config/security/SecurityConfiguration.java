package com.koreanguide.koreanguidebackend.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public SecurityConfiguration(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void configure(HttpSecurity httpSecurity)throws Exception{
//        Spring Security에서 제공하는 기본 HTTP 인증 기능 비활성화
        httpSecurity.httpBasic().disable()
//                JWT를 사용하기 떄문에 CSRF 비활성
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/api/signin",
                        "/api/signup",
                        "/api/exception",
                        "/api/refresh").permitAll()
                .antMatchers(HttpMethod.POST, "/post").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/post").permitAll()
                .antMatchers(HttpMethod.PUT, "/post").permitAll()
                .antMatchers(HttpMethod.DELETE, "/post").permitAll()

                .anyRequest().permitAll()
                .and()
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class); /*UsernamePasswordAuthenticationFilter 앞에 JwtAuthenticationFilter 추가*/
    }

    @Override
    public void configure(WebSecurity webSecurity){
        webSecurity.ignoring().antMatchers("/v2/api-docs", "/swagger-resources/**",
                "/swagger-ui.html", "/webjars/**", "/swagger/**", "/sign-api/exception");
    }
}
