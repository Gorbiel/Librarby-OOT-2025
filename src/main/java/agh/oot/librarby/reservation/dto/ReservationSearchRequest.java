package agh.oot.librarby.reservation.dto;

import agh.oot.librarby.reservation.model.ReservationStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record ReservationSearchRequest(
        @Schema(description = "ID of the reader", example = "5", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Long readerId,

        @Schema(description = "ID of the book", example = "10", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        Long bookId,

        @Schema(description = "Status of the reservation", example = "ACTIVE", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        ReservationStatus status,

        @Schema(description = "Sort direction for the results", example = "ASC", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        String sortDirection, // "ASC" or "DESC"

        @Schema(description = "Maximum number of results to return", example = "50", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
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

