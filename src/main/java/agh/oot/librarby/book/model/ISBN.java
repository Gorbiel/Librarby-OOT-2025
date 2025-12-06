package agh.oot.librarby.book.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class ISBN {

    @Column(nullable = false, length = 17)
    private String value;

    protected ISBN() {
    }

    public ISBN(String value) {
        String normalized = normalize(value);
        if (normalized == null || !isValid(normalized)) {
            throw new IllegalArgumentException("Invalid ISBN: " + value);
        }
        this.value = normalized;
    }

    public String getValue() {
        return value;
    }

    private String normalize(String raw) {
        if (raw == null) return null;
        return raw.replace("-", "").trim();
    }

    private boolean isValid(String isbn) {
        if (isbn == null) return false;
        String cleaned = isbn.replace("-", "").trim();
        if (cleaned.length() == 10) return isValidIsbn10(cleaned);
        if (cleaned.length() == 13) return isValidIsbn13(cleaned);
        return false;
    }

    private boolean isValidIsbn10(String isbn) {
        if (isbn == null || isbn.length() != 10) return false;
        int sum = 0;
        for (int i = 0; i < 10; i++) {
            char c = isbn.charAt(i);
            int value;
            if (i == 9 && (c == 'X' || c == 'x')) {
                value = 10;
            } else if (Character.isDigit(c)) {
                value = c - '0';
            } else {
                return false;
            }
            sum += value * (10 - i);
        }
        return sum % 11 == 0;
    }

    private boolean isValidIsbn13(String isbn) {
        if (isbn == null || isbn.length() != 13) return false;
        int sum = 0;
        for (int i = 0; i < 13; i++) {
            char c = isbn.charAt(i);
            if (!Character.isDigit(c)) return false;
            int digit = c - '0';
            sum += (i % 2 == 0) ? digit : digit * 3;
        }
        return sum % 10 == 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ISBN)) return false;
        ISBN isbn = (ISBN) o;
        return Objects.equals(value, isbn.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
