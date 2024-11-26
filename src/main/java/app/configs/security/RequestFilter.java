package app.configs.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Enumeration;

@Log4j2
public class RequestFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                if (headerName.toLowerCase().contains("Authorization".toLowerCase())){
                    log.warn("Header: {} = {}", headerName, headerValue);
                }
            }
        }
        log.warn("Request ended!!!!! \n");

        Collection<String> responseHeaderNames = response.getHeaderNames();
        if (responseHeaderNames != null) {
            for (var header : responseHeaderNames){
                String value = response.getHeader(header);
                log.warn("Header: {} = {}", header, value);
            }
        }
        log.warn("Response ended!!!!! \n");

        filterChain.doFilter(request, response);
    }
}
