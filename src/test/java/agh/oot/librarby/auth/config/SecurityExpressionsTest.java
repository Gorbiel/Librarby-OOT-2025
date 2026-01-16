package agh.oot.librarby.auth.config;

import agh.oot.librarby.auth.model.CustomUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecurityExpressionsTest {

    private final SecurityExpressions securityExpressions = new SecurityExpressions();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void isOwner_shouldReturnTrue_whenUserIdMatches() {
        // Given
        Long userId = 123L;
        CustomUserDetails userDetails = new CustomUserDetails(
                userId,
                "testuser",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_READER"))
        );
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        boolean result = securityExpressions.isOwner(userId);

        // Then
        assertTrue(result);
    }

    @Test
    void isOwner_shouldReturnFalse_whenUserIdDoesNotMatch() {
        // Given
        CustomUserDetails userDetails = new CustomUserDetails(
                123L,
                "testuser",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_READER"))
        );
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        boolean result = securityExpressions.isOwner(456L);

        // Then
        assertFalse(result);
    }

    @Test
    void isOwner_shouldReturnFalse_whenNotAuthenticated() {
        // Given
        SecurityContextHolder.clearContext();

        // When
        boolean result = securityExpressions.isOwner(123L);

        // Then
        assertFalse(result);
    }

    @Test
    void getCurrentUserId_shouldReturnUserId_whenAuthenticated() {
        // Given
        Long userId = 123L;
        CustomUserDetails userDetails = new CustomUserDetails(
                userId,
                "testuser",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_READER"))
        );
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        // When
        Long result = securityExpressions.getCurrentUserId();

        // Then
        assertEquals(userId, result);
    }

    @Test
    void getCurrentUserId_shouldReturnNull_whenNotAuthenticated() {
        // Given
        SecurityContextHolder.clearContext();

        // When
        Long result = securityExpressions.getCurrentUserId();

        // Then
        assertNull(result);
    }
}

