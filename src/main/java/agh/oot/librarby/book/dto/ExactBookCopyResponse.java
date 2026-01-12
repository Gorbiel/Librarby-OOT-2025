package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.CopyStatus;

public record ExactBookCopyResponse(
        Long id,
        BookEditionResponse bookEdition,
        CopyStatus status
) {

}
