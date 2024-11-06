package app.configs.security.jwt;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import app.configs.security.ApiAuthenticationFilter;
import app.configs.security.TokenInfo;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public class JwtAuthenticationFilter extends ApiAuthenticationFilter {

    private final String jwtKey;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   String url, String jwtKey) {

        super(authenticationManager, url);

        this.jwtKey = jwtKey;
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain, Authentication authResult) {

        User user = (User) authResult.getPrincipal();

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String token = new JwtHelper(jwtKey)
                .encode(new TokenInfo(user.getUsername(), roles));

        response.addHeader("Authorization", "Bearer " + token);
    }
}
