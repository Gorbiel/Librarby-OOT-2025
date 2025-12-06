package agh.oot.librarby.book.model;

public enum AgeRating {
    EVERYONE(0),
    TODDLER(3),
    CHILDREN(7),
    TEENAGER(13),
    ADULT(18);

    private final int minimalAge;

    AgeRating(int minimalAge) {
        this.minimalAge = minimalAge;
    }

    public int getMinimalAge() {
        return minimalAge;
    }

    public boolean isAgeSufficient(int age) {
        return age >= minimalAge;
    }
}
