package app.configs.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@EnableWebSecurity
@PropertySource("classpath:/application.properties")
public class OAuthSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http) throws Exception {

        http.authorizeHttpRequests(httpSecurity ->
                httpSecurity.anyRequest().authenticated())
                .oauth2Login(withDefaults());

        return http.build();
    }

    @Bean
    public ClientRegistrationRepository
            clientRegistrationRepository(Environment env) {

        String clientId = env.getProperty("google.clientId");
        String clientSecret = env.getProperty("google.clientSecret");

        var reg = CommonOAuth2Provider.GOOGLE.getBuilder("google")
                .clientId(clientId).clientSecret(clientSecret).build();

        return new InMemoryClientRegistrationRepository(reg);
    }
}