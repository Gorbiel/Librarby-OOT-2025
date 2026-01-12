package agh.oot.librarby.book.dto;

import agh.oot.librarby.book.model.CopyStatus;

public record CreateExactBookCopyRequest(
        Long bookEditionId,
        CopyStatus status) {

}
