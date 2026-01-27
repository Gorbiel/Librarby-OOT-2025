package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.CopyStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateExactBookCopyRequest(
        @Schema(description = "Status of the book copy", example = "AVAILABLE", requiredMode = Schema.RequiredMode.REQUIRED)
        CopyStatus status
) {}
