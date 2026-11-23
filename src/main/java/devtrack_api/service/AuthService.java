
package devtrack_api.service;

import devtrack_api.dto.RegisterRequest;
import devtrack_api.dto.LoginRequest;
import devtrack_api.dto.AuthResponse;
import devtrack_api.entity.User;
import devtrack_api.enums.UserRole;
import devtrack_api.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(
                "Email is already registered"
            );
        }

        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail().trim().toLowerCase());

        // Never store the original password.
        user.setPassword(
            passwordEncoder.encode(request.getPassword())
        );

        // New users get the DEVELOPER role by default.
        user.setRole(UserRole.DEVELOPER);

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser.getEmail());

        return new AuthResponse(
            token,
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(
            request.getEmail().trim().toLowerCase()
        ).orElseThrow(() -> new IllegalArgumentException(
            "Invalid email or password"
        ));

        if (!passwordEncoder.matches(
                request.getPassword(), user.getPassword())) {

            throw new IllegalArgumentException(
                "Invalid email or password"
            );
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(
            token,
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }
}
