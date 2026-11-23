
package devtrack_api.config;

import devtrack_api.service.JwtAuthenticationFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

   .authorizeHttpRequests(auth -> auth
    .dispatcherTypeMatchers(
        jakarta.servlet.DispatcherType.ERROR
    ).permitAll()
    .requestMatchers("/api/auth/**", "/api/health").permitAll()
    .anyRequest().authenticated()
)

            .exceptionHandling(exception -> exception

                .authenticationEntryPoint((request, response, authException) -> {

                    System.out.println("SECURITY: AUTHENTICATION ENTRY POINT");
                    System.out.println("Request: " + request.getMethod()
                            + " " + request.getRequestURI());
                    System.out.println("Reason: " + authException.getMessage());

                    response.sendError(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Unauthorized"
                    );
                })

                .accessDeniedHandler((request, response, accessDeniedException) -> {

                    System.out.println("SECURITY: ACCESS DENIED HANDLER");
                    System.out.println("Request: " + request.getMethod()
                            + " " + request.getRequestURI());
                    System.out.println("Reason: "
                            + accessDeniedException.getMessage());
                    System.out.println("Authentication: "
                            + org.springframework.security.core.context
                            .SecurityContextHolder.getContext()
                            .getAuthentication());

                    response.sendError(
                        HttpStatus.FORBIDDEN.value(),
                        "You do not have permission to access this resource"
                    );
                })
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}
