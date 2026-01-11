package agh.oot.librarby.reservation.service;

import agh.oot.librarby.reservation.repository.ReservationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component("reservationSecurity")
public class ReservationSecurity {

    private final ReservationRepository reservationRepository;

    public ReservationSecurity(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public boolean isOwnerOrPrivileged(Long reservationId, Long currentUserId, Authentication auth) {
        if (hasAdminRole(auth)) return true;

        return reservationRepository.findById(reservationId)
                .map(res -> res.getReader().getId().equals(currentUserId))
                .orElse(false);
    }

    public boolean canSearch(Long readerId, Long bookId, Long currentUserId, Authentication auth) {
        boolean isAdmin = hasAdminRole(auth);

        if (readerId != null) {
            return isAdmin || readerId.equals(currentUserId);
        } else {
            return isAdmin;
        }
    }

    public boolean hasAdminRole(Authentication auth) {
        if (auth == null) return false;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_LIBRARIAN"));
    }
}