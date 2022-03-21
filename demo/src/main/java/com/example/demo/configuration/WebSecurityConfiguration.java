package com.example.demo.configuration;

import com.example.demo.controllers.filters.JwtRequestFilter;
import com.example.demo.securities.BasicAuthenticationProvider;
import com.example.demo.securities.DemoLogoutHandler;
import com.example.demo.securities.DemoLogoutSuccessHandler;
import com.example.demo.service.JwtUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Resource
    private JwtUserDetailsService jwtUserDetailsService;
    @Resource
    private BasicAuthenticationProvider authenticationProvider;
    @Resource
    private JwtRequestFilter jwtRequestFilter;
    @Resource
    private DemoLogoutHandler demoLogoutHandler;

    @Resource
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(jwtUserDetailsService).passwordEncoder(jwtUserDetailsService.passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests()
                    .antMatchers("/api/login").permitAll()
                    .antMatchers("/api/refreshToken").permitAll()
                    .antMatchers("/api/logout").permitAll()
                    .anyRequest().authenticated()
                .and()
                .logout()
                    .logoutUrl("/api/logout")
                    .addLogoutHandler(new SecurityContextLogoutHandler())
                    .addLogoutHandler((request, response, auth) -> {
                        for (Cookie cookie : request.getCookies()) {
                            String cookieName = cookie.getName();
                            Cookie cookieToDelete = new Cookie(cookieName, null);
                            cookieToDelete.setMaxAge(0);
                            response.addCookie(cookieToDelete);
                        }
                    })
                    .addLogoutHandler(demoLogoutHandler)
                    .logoutSuccessHandler(new DemoLogoutSuccessHandler())
        ;

        // Add a filter to validate the tokens with every request
        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
    }

}
