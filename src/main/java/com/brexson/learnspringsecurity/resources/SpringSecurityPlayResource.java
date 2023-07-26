package com.brexson.learnspringsecurity.resources;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringSecurityPlayResource {

    //    @GetMapping("/csrf-token")
//    public CsrfToken retrieveCsrfToken(HttpServletRequest request) {
//        return (CsrfToken) request.getAttribute("_csrf");
//    }
    @GetMapping("/csrf-token")
    public CsrfTokenDto getCsrfToken(HttpServletRequest request) {
        // Obtain the CSRF token from the request
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        // Add the CSRF token as a response header for the client-side JavaScript to access
        if (csrfToken != null) {
            String token = csrfToken.getToken();
            String headerName = csrfToken.getHeaderName();
            return new CsrfTokenDto(headerName, token);
        } else {
            return null; // or handle the case when CSRF token is not available
        }
    }

    // Inner class to represent the CSRF token in the response
    private static class CsrfTokenDto {
        private final String headerName;
        private final String token;

        public CsrfTokenDto(String headerName, String token) {
            this.headerName = headerName;
            this.token = token;
        }
        // Getters for JSON serialization (if necessary)
        public String getHeaderName() {
            return headerName;
        }
        public String getToken() {
            return token;
        }
    }

}
