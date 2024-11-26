package app.configs.security.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

public class ApiAuthSuccessHandler implements AuthenticationSuccessHandler {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest request,
                                            HttpServletResponse response,
                                            Authentication authentication) {

            response.setStatus(HttpServletResponse.SC_OK);
        }
    }
