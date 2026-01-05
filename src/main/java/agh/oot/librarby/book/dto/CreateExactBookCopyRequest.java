package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.CopyStatus;
import jakarta.validation.constraints.NotNull;

public record CreateExactBookCopyRequest(
        @NotNull(message = "Book edition ID is required")
        Long bookEditionId,

        CopyStatus status
) {
}
