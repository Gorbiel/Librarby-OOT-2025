package agh.oot.librarby.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "admins")
public class Admin extends UserProfile {

    // Protected no-args constructor required by JPA
    protected Admin() {
        super();
    }

    // Public constructor delegating to UserProfile
    public Admin(String firstName, String lastName) {
        super(firstName, lastName);
    }
}
