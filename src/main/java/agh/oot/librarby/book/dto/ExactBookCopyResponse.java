package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.CopyStatus;
import io.swagger.v3.oas.annotations.media.Schema;

public record ExactBookCopyResponse(
        @Schema(description = "Exact book copy ID", example = "100")
        Long id,

        @Schema(description = "Information about the book edition to which this copy belongs")
        BookEditionResponse bookEdition,

        @Schema(description = "Status of the book copy", example = "AVAILABLE")
        CopyStatus status
) {}
