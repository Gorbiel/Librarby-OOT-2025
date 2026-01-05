package agh.oot.librarby.util;

/**
 * Utility class for test helper methods.
 */
public final class TestEntityUtils {

    private TestEntityUtils() {
        // Prevent instantiation
    }

    /**
     * Sets the ID field of a JPA entity using reflection.
     * This is useful for unit tests where entities are created without persistence.
     *
     * @param entity the entity to set the ID on
     * @param id     the ID value to set
     */
    public static void setId(Object entity, Long id) {
        try {
            var idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set ID on entity: " + entity.getClass().getSimpleName(), e);
        }
    }
}
