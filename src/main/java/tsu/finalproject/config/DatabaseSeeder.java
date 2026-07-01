package tsu.finalproject.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tsu.finalproject.feature.user.UserRepository;
import tsu.finalproject.feature.user.entity.Admin;
import tsu.finalproject.feature.user.enums.Role;

@Component
@RequiredArgsConstructor
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() == 0) {
            Admin rootAdmin = Admin.builder()
                                      .firstName("System")
                                      .lastName("Administrator")
                                      .email("admin@university.edu")
                                      .passwordHash(passwordEncoder.encode("admin123"))
                                      .universityId("ADM-0001")
                                      .role(Role.ROLE_ADMIN)
                                      .deactivated(false)
                                      .build();

            userRepository.save(rootAdmin);
        }
    }
}