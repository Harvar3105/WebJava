package app.configs.security.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class ApiAccessDeniedHandler implements AccessDeniedHandler {

        @Override
        public void handle(HttpServletRequest request,
                           HttpServletResponse response,
                           AccessDeniedException arg2) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
