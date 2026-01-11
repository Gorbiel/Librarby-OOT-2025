package agh.oot.librarby.reservation.dto;

import agh.oot.librarby.reservation.model.ReservationStatus;

public record ReservationSearchRequest(
        Long readerId,
        Long bookId,
        ReservationStatus status,
        String sortDirection, // "ASC" or "DESC"
        Integer limit
) {
    public ReservationSearchRequest {
        if (sortDirection != null && !sortDirection.equalsIgnoreCase("ASC") && !sortDirection.equalsIgnoreCase("DESC")) {
            throw new IllegalArgumentException("Sort direction must be 'ASC' or 'DESC'");
        }
        if (limit != null && limit <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0");
        }
    }

    public String getSortDirection() {
        return sortDirection != null ? sortDirection.toUpperCase() : "ASC";
    }

    public Integer getLimit() {
        return limit != null ? limit : Integer.MAX_VALUE;
    }
}

