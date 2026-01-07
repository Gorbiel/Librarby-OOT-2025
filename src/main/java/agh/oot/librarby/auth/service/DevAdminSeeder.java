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
@Profile("dev") // Dodatkowe zabezpieczenie - tylko profil deweloperski
public class DevAdminSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevAdminSeeder.class);

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    // Wszystkie pola muszą mieć @Value, aby pobrać dane z env/properties
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
    @Transactional // Zapewnia poprawny zapis powiązanych encji Account <-> Admin
    public void run(String... args) {
        // 1. Sprawdzamy czy użytkownik już istnieje (idempotentność)
        if (userAccountRepository.existsByUsername(adminUsername) ||
                userAccountRepository.existsByEmail(adminEmail)) {
            log.info("DevAdminSeeder: Administrator '{}' already exists. Skipping creation.", adminUsername);
            return;
        }

        log.info("DevAdminSeeder: Creating dev administrator account...");

        // 2. Tworzymy konto użytkownika
        UserAccount account = new UserAccount(
                adminUsername,
                adminEmail,
                passwordEncoder.encode(adminPassword),
                UserRole.ADMIN
        );

        // 3. Tworzymy profil admina
        Admin admin = new Admin(
                adminFirstName,
                adminLastName
        );

        // 4. Łączymy relację (zakładając relację dwustronną)
        account.setUserProfile(admin);
        admin.setUserAccount(account);

        // 5. Zapisujemy
        userAccountRepository.save(account);
        log.info("DevAdminSeeder: Successfully created admin: {}", adminUsername);
    }
}