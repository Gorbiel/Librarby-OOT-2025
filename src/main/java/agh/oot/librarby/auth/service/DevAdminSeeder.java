package agh.oot.librarby.auth.service;

import agh.oot.librarby.exception.GlobalExceptionHandler;
import agh.oot.librarby.user.model.Admin;
import agh.oot.librarby.user.model.UserAccount;
import agh.oot.librarby.user.model.UserRole;
import agh.oot.librarby.user.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev")
public class DevAdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevAdminSeeder.class);

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.dev.admin.firstname:Admin}")
    private String adminFirstName;

    @Value("${app.dev.admin.lastname:Default}")
    private String adminLastName;

    @Value("${app.dev.admin.username:admin}")
    private String adminUsername;

    @Value("${app.dev.admin.email:admin@example.com}")
    private String adminEmail;

    @Value("${app.dev.admin.pass}")
    private String adminPassword;

    public DevAdminSeeder(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userAccountRepository.existsByUsername(adminUsername) ||
                userAccountRepository.existsByEmail(adminEmail)) {
            log.info("DevAdminSeeder: Administrator '{}' already exists. Skipping creation.", adminUsername);
            return;
        }

        log.info("DevAdminSeeder: Creating dev administrator account...");

        UserAccount account = new UserAccount(
                adminUsername,
                adminEmail,
                passwordEncoder.encode(adminPassword),
                UserRole.ADMIN
        );

        Admin admin = new Admin(
                adminFirstName,
                adminLastName
        );

        account.setUserProfile(admin);
        admin.setUserAccount(account);

        userAccountRepository.save(account);
        log.info("DevAdminSeeder: Successfully created admin: {}", adminUsername);
    }
}