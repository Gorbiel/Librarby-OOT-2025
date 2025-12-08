// java
package agh.oot.librarby.user.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "readers")
public class Reader extends UserProfile {

    private Integer rentalLimit;

    private LocalDate dateOfBirth;

    protected Reader() {
        super();
    }

    public Reader(String firstName,
                  String lastName,
                  Integer rentalLimit,
                  LocalDate dateOfBirth) {
        super(firstName, lastName);
        this.rentalLimit = rentalLimit;
        this.dateOfBirth = dateOfBirth;
    }

    public Integer getRentalLimit() {
        return rentalLimit;
    }

    public void setRentalLimit(Integer rentalLimit) {
        this.rentalLimit = rentalLimit;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Reader)) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}