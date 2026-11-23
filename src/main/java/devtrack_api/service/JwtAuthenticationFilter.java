
package devtrack_api.service;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("\n========== JWT FILTER ==========");
        System.out.println("Request: " + request.getMethod()
                + " " + request.getRequestURI());

        final String authHeader = request.getHeader("Authorization");

        System.out.println("Authorization header present: "
                + (authHeader != null));

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No valid Bearer token found.");

            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        try {
            String email = jwtService.extractEmail(token);

            System.out.println("JWT successfully decoded.");
            System.out.println("Extracted email: " + email);

            if (email != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder.getContext()
                        .setAuthentication(authentication);

                System.out.println("Authentication successful.");
                System.out.println("Authorities: "
                        + authentication.getAuthorities());

            } else {
                System.out.println(
                        "Email is null or authentication already exists."
                );
            }

        } catch (Exception exception) {
            System.out.println("JWT AUTHENTICATION FAILED:");
            exception.printStackTrace();

            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}
