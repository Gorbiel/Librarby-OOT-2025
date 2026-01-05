package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.CopyStatus;

public record ExactBookCopyResponse(
        Long id,
        Long bookEditionId,
        String isbn,
        String bookTitle,
        CopyStatus status
) {
}
