package app.configs;

import app.configs.security.handlers.ApiAccessDeniedHandler;
import app.configs.security.handlers.ApiEntryPoint;
import app.configs.security.jwt.JwtAuthenticationFilter;
import app.configs.security.jwt.JwtAuthorizationFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import javax.sql.DataSource;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@PropertySource("classpath:/application.properties")
public class SecurityConfig {

    @Value("${jwt.signing.key}")
    private String jwtKey;

    private final MvcRequestMatcher.Builder mvc;

    public SecurityConfig(HandlerMappingIntrospector introspector) {
        this.mvc = new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(conf -> conf
                .requestMatchers(mvcMatcher("/version")).permitAll()
                .requestMatchers(mvcMatcher("/login")).permitAll()
//                .requestMatchers(mvcMatcher("/users")).permitAll() //For Testing
                .anyRequest().authenticated());

        http.with(new FilterConfigurer(), Customizer.withDefaults());

        http.exceptionHandling(conf -> conf
                .authenticationEntryPoint(new ApiEntryPoint())
                .accessDeniedHandler(new ApiAccessDeniedHandler()));

        http.csrf(csrf -> csrf.ignoringRequestMatchers(mvcMatcher("/**")));

        return http.build();
    }

    public class FilterConfigurer extends AbstractHttpConfigurer<FilterConfigurer, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) {
            AuthenticationManager manager = http.getSharedObject(AuthenticationManager.class);

            JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(manager, "/api/login", jwtKey);
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(jwtKey);
            http.addFilterBefore(jwtAuthorizationFilter, AuthorizationFilter.class);
        }
    }

    @Bean
    public UserDetailsService users(
//            @Qualifier("HsqlDataSource") DataSource dataSource
            @Qualifier("PostgresDataSource") DataSource dataSource
    ) {
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);

//        userDetailsManager.setUsersByUsernameQuery("SELECT userName, password, enabled, firstName FROM USERS WHERE userName = ?");
//        userDetailsManager.setAuthoritiesByUsernameQuery("SELECT user_id, authority FROM AUTHORITIES WHERE user_id = ?");

        userDetailsManager.setUsersByUsernameQuery("SELECT userName, password, enabled FROM users WHERE userName = ?");
        userDetailsManager.setAuthoritiesByUsernameQuery(
                "SELECT u.userName, a.authorityName FROM authorities a LEFT JOIN users u ON a.user_id = u.id WHERE u.userName = ?"
        );

        return userDetailsManager;
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.debug(false);
    }

    private MvcRequestMatcher mvcMatcher(String pattern) {
        return mvc.pattern(pattern);
    }
}