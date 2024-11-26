package app.configs.security.jwt;

import app.configs.security.TokenInfo;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Log4j2
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final String jwtKey;

    public JwtAuthorizationFilter(String jwtKey) {
        this.jwtKey = jwtKey;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {

        String tokenString = request.getHeader("Authorization");

        if (tokenString == null) {
            log.warn("Authorization header missing, skipping filter.");
            chain.doFilter(request, response);
            return;
        }

        log.warn("Authorization header received: {}", tokenString);

        TokenInfo tokenInfo = new JwtHelper(jwtKey).decode(tokenString);

        log.warn("Decoded token: {}", tokenInfo);

        var authorities = tokenInfo.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        var springToken = new UsernamePasswordAuthenticationToken(
                tokenInfo.getUsername(), null, authorities);

        SecurityContextHolder.getContext().setAuthentication(springToken);

        log.warn("User authenticated: {}", tokenInfo.getUsername());

        chain.doFilter(request, response);
    }
}