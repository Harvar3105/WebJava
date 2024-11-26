package app.configs.security.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

public class ApiAuthFailureHandler implements AuthenticationFailureHandler {
        @Override
        public void onAuthenticationFailure(HttpServletRequest request,
                                            HttpServletResponse response,
                                            AuthenticationException exception) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }