package agh.oot.librarby.book.model;

public enum CopyStatus {
    AVAILABLE,      // Available for rental
    BORROWED,       // Currently with a reader
    RESERVED,       // Reserved (awaiting pickup)
    LOST,           // Lost
    UNAVAILABLE     // Unavailable (e.g., under renovation)

}
