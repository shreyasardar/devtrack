
package devtrack_api.config;

import devtrack_api.entity.User;
import devtrack_api.enums.UserRole;
import devtrack_api.repository.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap-admin.email:}")
    private String adminEmail;

    @Value("${app.bootstrap-admin.password:}")
    private String adminPassword;

    @Value("${app.bootstrap-admin.name:DevTrack Admin}")
    private String adminName;

    public AdminBootstrap(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        if (adminEmail == null || adminEmail.isBlank()
                || adminPassword == null || adminPassword.isBlank()) {

            System.out.println("ADMIN BOOTSTRAP: Skipped. No credentials configured.");
            return;
        }

        adminEmail = adminEmail.trim().toLowerCase();

        if (userRepository.existsByEmail(adminEmail)) {
            System.out.println(
                    "ADMIN BOOTSTRAP: Account already exists. No changes made."
            );
            return;
        }

        if (adminPassword.length() < 12
                || adminPassword.getBytes(
                        java.nio.charset.StandardCharsets.UTF_8
                ).length > 72) {

            throw new IllegalArgumentException(
                    "Admin password must be at least 12 characters "
                    + "and no more than 72 UTF-8 bytes."
            );
        }

        User admin = new User();
        admin.setName(adminName);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setRole(UserRole.ADMIN);

        userRepository.save(admin);

        System.out.println(
                "ADMIN BOOTSTRAP: Admin account created successfully."
        );
    }
}
