package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.CopyStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record CreateExactBookCopyRequest(
        @Schema(description = "ID of the book edition to which this copy belongs", example = "15", requiredMode = Schema.RequiredMode.REQUIRED)
        Long bookEditionId,

        @Schema(description = "Status of the book copy", example = "AVAILABLE", requiredMode = Schema.RequiredMode.REQUIRED)
        CopyStatus status
) {}
