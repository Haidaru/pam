//package com.svipb.pam.security;
//
//import com.svipb.pam.repository.AdminRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.access.AccessDecisionManager;
//import org.springframework.security.access.expression.SecurityExpressionHandler;
//import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig extends SecurityConfigurerAdapter {
//
//    @Bean
//    public SecurityMatcher securityMatcher() {
//        return new SecurityMatcher();
//    }
//    @Bean
//    public CustomUserDetailsService customUserDetailsService(AdminRepository adminRepository) {
//        return new CustomUserDetailsService(adminRepository);
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//    @Bean
//    public AccessDecisionManager accessDecisionManager() {
//        return new CustomAccessDecisionManager();
//    }
//
//    @Bean
//    public SecurityExpressionHandler SecurityExpressionHandler() {
//        return new CustomSecurityExpressionHandler();
//    }
//    @Configuration
//    public static class WebSecurityConfig extends SecurityConfigurerAdapter {
//
//        @Autowired
//        private CustomUserDetailsService customUserDetailsService;
//
//        @Autowired
//        private SecurityMatcher securityMatcher;
//
//        @Autowired
//        private AccessDecisionManager accessDecisionManager;
//
//        @Autowired
//        private CustomSecurityExpressionHandler CustomSecurityExpressionHandler;
//}
