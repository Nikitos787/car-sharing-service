package project.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import project.model.user.RoleName;
import project.security.jwt.JwtTokenFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String MANAGER = RoleName.MANAGER.name();
    private static final String CUSTOMER = RoleName.CUSTOMER.name();

    private final UserDetailsService userDetailsService;
    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers(HttpMethod.POST, "/register", "login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/payments/success/{id}",
                                        "/payments/cancel/{id}").permitAll()
                                .requestMatchers(HttpMethod.GET, "/test",
                                        "/swagger-ui/**", "/swagger-ui.html",
                                        "/v3/api-docs/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/cars").hasRole(MANAGER)
                                .requestMatchers(HttpMethod.GET, "/cars")
                                .hasAnyRole(MANAGER, CUSTOMER)
                                .requestMatchers(HttpMethod.GET, "/cars/by-params")
                                .hasAnyRole(MANAGER, CUSTOMER)
                                .requestMatchers(HttpMethod.GET, "/cars/{id}")
                                .hasAnyRole(MANAGER, CUSTOMER)
                                .requestMatchers(HttpMethod.PUT, "/cars/{id}")
                                .hasRole(MANAGER)
                                .requestMatchers(HttpMethod.DELETE, "/cars/{id}")
                                .hasRole(MANAGER)
                                .requestMatchers(HttpMethod.POST, "/cars/add/{id}")
                                .hasRole(MANAGER)
                                .requestMatchers(HttpMethod.DELETE, "/cars/remove/{id}")
                                .hasRole(MANAGER)
                                .requestMatchers(HttpMethod.GET, "/payments/my-payments")
                                .hasRole(CUSTOMER)
                                .requestMatchers(HttpMethod.GET, "/payments")
                                .hasRole(MANAGER)
                                .requestMatchers(HttpMethod.POST, "/payments")
                                .hasAnyRole(MANAGER, CUSTOMER)
                                .requestMatchers(HttpMethod.GET, "/rentals")
                                .hasRole(MANAGER)
                                .requestMatchers(HttpMethod.POST, "/rentals")
                                .hasAnyRole(MANAGER, CUSTOMER)
                                .requestMatchers(HttpMethod.PUT, "/rentals/{id}/return")
                                .hasRole(MANAGER)
                                .requestMatchers(HttpMethod.GET, "/rentals/my-rentals")
                                .hasRole(CUSTOMER)
                                .requestMatchers(HttpMethod.GET, "/rentals/{id}")
                                .hasRole(MANAGER)
                                .requestMatchers(HttpMethod.GET, "/users/me")
                                .hasAnyRole(CUSTOMER, MANAGER)
                                .requestMatchers(HttpMethod.PUT, "/users/{id}/role")
                                .hasRole(MANAGER)
                                .requestMatchers(HttpMethod.PUT, "/users/me")
                                .hasAnyRole(MANAGER, CUSTOMER)
                                .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .httpBasic(AbstractHttpConfigurer::disable)
                .userDetailsService(userDetailsService)
                .build();
    }
}
