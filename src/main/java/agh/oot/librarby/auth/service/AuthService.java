package agh.oot.librarby.auth.service;

import agh.oot.librarby.auth.config.JwtService;
import agh.oot.librarby.auth.dto.LoginRequest;
import agh.oot.librarby.auth.dto.LoginResponse;
import agh.oot.librarby.auth.dto.RegisterReaderRequest;
import agh.oot.librarby.user.repository.UserAccountRepository;
import agh.oot.librarby.user.model.Reader;
import agh.oot.librarby.user.model.UserAccount;
import agh.oot.librarby.user.model.UserRole;
import jakarta.transaction.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService customUserDetailsService;
    private static final int RENTAL_LIMIT = 5;

    public AuthService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, CustomUserDetailsService customUserDetailsService) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Transactional
    public void registerReader(RegisterReaderRequest request) {

        if (userAccountRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (userAccountRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already in use");
        }

        UserAccount account = new UserAccount(
                request.username(),
                request.email(),
                passwordEncoder.encode(request.password()),
                UserRole.READER
        );

        Reader reader = new Reader(
                request.firstName(),
                request.lastName(),
                RENTAL_LIMIT,
                request.dateOfBirth()
        );

        account.setUserProfile(reader);
        reader.setUserAccount(account);

        userAccountRepository.save(account);
    }

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        UserDetails user = customUserDetailsService.loadUserByUsername(request.username());

        String jwtToken = jwtService.generateToken(user);

        return new LoginResponse(jwtToken);
    }

}