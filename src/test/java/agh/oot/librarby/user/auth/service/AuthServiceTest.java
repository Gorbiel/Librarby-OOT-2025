package agh.oot.librarby.user.auth.service;

import agh.oot.librarby.user.auth.config.JwtService;
import agh.oot.librarby.user.auth.dto.LoginRequest;
import agh.oot.librarby.user.auth.dto.RegisterReaderRequest;
import agh.oot.librarby.user.auth.repository.UserAccountRepository;
import agh.oot.librarby.user.model.UserAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private AuthService underTest;

    @Test
    void registerReader_success_savesAccountAndReader() {
        // given
        RegisterReaderRequest req = new RegisterReaderRequest(
                "john_doe",
                "Password123",
                "john@example.com",
                "John",
                "Doe",
                LocalDate.of(1990, 1, 1)
        );

        when(userAccountRepository.existsByUsername(req.username())).thenReturn(false);
        when(userAccountRepository.existsByEmail(req.email())).thenReturn(false);
        when(passwordEncoder.encode(req.password())).thenReturn("encodedPwd");
        when(userAccountRepository.save(any(UserAccount.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        underTest.registerReader(req);

        // then
        ArgumentCaptor<UserAccount> captor = ArgumentCaptor.forClass(UserAccount.class);
        verify(userAccountRepository, times(1)).save(captor.capture());
        UserAccount saved = captor.getValue();
        assertEquals("john_doe", saved.getUsername());
        assertEquals("john@example.com", saved.getEmail());
        assertNotNull(saved.getUserProfile());
    }

    @Test
    void registerReader_usernameExists_throws() {
        // given
        RegisterReaderRequest req = new RegisterReaderRequest(
                "existing",
                "Password123",
                "a@b.com",
                "A",
                "B",
                LocalDate.of(1990, 1, 1)
        );
        when(userAccountRepository.existsByUsername(req.username())).thenReturn(true);

        // when / then
        assertThrows(IllegalArgumentException.class, () -> underTest.registerReader(req));
        verify(userAccountRepository, never()).save(any());
    }

    @Test
    void login_success_verifiesAuthenticationAndGeneratesToken() {
        // given
        LoginRequest req = new LoginRequest("john_doe", "Password123");
        UserDetails userDetails = User.withUsername("john_doe")
                .password("ignored")
                .authorities("ROLE_READER")
                .build();

        when(customUserDetailsService.loadUserByUsername("john_doe")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("jwt-token-value");

        // when
        var response = underTest.login(req);

        // then
        assertNotNull(response);
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(customUserDetailsService).loadUserByUsername("john_doe");
        verify(jwtService).generateToken(userDetails);
    }
}