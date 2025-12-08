package agh.oot.librarby.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "librarians")
public class Librarian extends UserProfile {

    // Protected no-args constructor required by JPA
    protected Librarian() {
        super();
    }

    // Public constructor delegating to UserProfile
    public Librarian(String firstName, String lastName) {
        super(firstName, lastName);
    }
}
