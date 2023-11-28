//package com.svipb.pam.security;
//
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.security.web.util.matcher.RequestMatcher;
//
//public class SecurityMatcher implements RequestMatcher {
//    private static final String ALLOWED_HEADER_NAME = "X-Allow-Without-Auth";
//
//    @Override
//    public boolean matches(HttpServletRequest request) {
//        // Check if the request contains a specific header that allows access without authentication
//        String allowedHeaderValue = request.getHeader(ALLOWED_HEADER_NAME);
//        // If the specified header is present and has the value 'true', return false (don't require authentication)
//        return !"true".equalsIgnoreCase(allowedHeaderValue);
//
//        // For all other requests (without the specified header), return true (require authentication)
//    }
//}
