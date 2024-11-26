package app.configs.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;

@Log4j2
public class RequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        log.warn("Request URI: {}", request.getRequestURI());
//        log.warn("HTTP Method: {}", request.getMethod());
        log.warn("NEW REQUEST HEADER:\n");

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            boolean thereIsAuthHeader = false;
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                if (headerName.trim().equals("Authorization")){
                    thereIsAuthHeader = true;
                    log.warn("Header: {} = {}", headerName, headerValue);
                }
            }
            if (!thereIsAuthHeader){
                log.warn("No Authorization header found!");
            }
        }

        filterChain.doFilter(request, response);
    }
}
