package agh.oot.librarby.auth.config;

import agh.oot.librarby.auth.model.CustomUserDetails;
import agh.oot.librarby.review.repository.ReviewRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Custom security expressions for use in @PreAuthorize annotations.
 * Provides methods to check user identity and permissions.
 */
@Component("securityExpressions")
public class SecurityExpressions {

    private final ReviewRepository reviewRepository;

    public SecurityExpressions(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

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

    /**
     * Checks if the currently authenticated user is an admin or has the ADMIN role.
     *
     * @return true if the user has ROLE_ADMIN, false otherwise
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }

    /**
     * Checks if the currently authenticated user is either an admin or the owner of the resource.
     *
     * @param userId the user ID to check ownership against
     * @return true if the user is admin or owner, false otherwise
     */
    public boolean isAdminOrOwner(Long userId) {
        return isAdmin() || isOwner(userId);
    }

    /**
     * Checks if the currently authenticated user is the owner of a review or has admin/librarian privileges.
     *
     * @param reviewId      the ID of the review
     * @param currentUserId the ID of the currently authenticated user
     * @param auth          the authentication object
     * @return true if user is owner or has admin/librarian role, false otherwise
     */
    public boolean isReviewOwnerOrPrivileged(Long reviewId, Long currentUserId, Authentication auth) {
        if (hasAdminOrLibrarianRole(auth)) {
            return true;
        }

        return reviewRepository.existsByIdAndReaderId(reviewId, currentUserId);
    }

    /**
     * Checks if the user has admin or librarian role.
     *
     * @param auth the authentication object
     * @return true if user has admin or librarian role, false otherwise
     */
    private boolean hasAdminOrLibrarianRole(Authentication auth) {
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_LIBRARIAN"));
    }
}

