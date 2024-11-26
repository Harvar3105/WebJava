package app.configs.security.handlers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public class ApiLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest,
                                HttpServletResponse response,
                                Authentication authentication) {

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
