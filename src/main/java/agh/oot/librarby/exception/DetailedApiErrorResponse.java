package agh.oot.librarby.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record DetailedApiErrorResponse(
        // TODO add Swagger annotations
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> validationErrors
) {
}
