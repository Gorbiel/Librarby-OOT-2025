package agh.oot.librarby.auth.config;

import agh.oot.librarby.auth.model.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Custom security expressions for use in @PreAuthorize annotations.
 * Provides methods to check user identity and permissions.
 */
@Component("securityExpressions")
public class SecurityExpressions {

    /**
     * Checks if the currently authenticated user has the specified user ID.
     *
     * @param userId the user ID to check against
     * @return true if the authenticated user's ID matches the provided userId, false otherwise
     */
    public boolean isOwner(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId().equals(userId);
        }

        return false;
    }

    /**
     * Gets the ID of the currently authenticated user.
     *
     * @return the user ID, or null if not authenticated or principal is not CustomUserDetails
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getId();
        }

        return null;
    }

    /**
     * Checks if the currently authenticated user has the READER role.
     *
     * @return true if the user has ROLE_READER, false otherwise
     */
    public boolean isReader() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_READER".equals(a.getAuthority()));
    }

    /**
     * Checks if the currently authenticated user has the ADMIN or LIBRARIAN role.
     *
     * @return true if the user has ROLE_ADMIN or ROLE_LIBRARIAN, false otherwise
     */
    public boolean isAdminOrLibrarian() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()) ||
                        "ROLE_LIBRARIAN".equals(a.getAuthority()));
    }
}

