package agh.oot.librarby.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

@Schema(description = "Standard error response returned by the API when a request fails")
public record ApiErrorResponse(

        @Schema(
                description = "Time when the error occurred (ISO-8601)",
                example = "2026-01-07T09:56:45.566570068"
        )
        LocalDateTime timestamp,

        @Schema(
                description = "HTTP status code",
                example = "418"
        )
        int status,

        @Schema(
                description = "HTTP status name",
                example = "I_AM_A_TEAPOT"
        )
        String error,

        @Schema(
                description = "Detailed error message",
                example = "I am a teapot and cannot brew coffee"
        )
        String message,

        @Schema(
                description = "Request path that caused the error",
                example = "/api/v1/tea/2137"
        )
        String path,
        Map<String, Object> metadata
) {
    public ApiErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this(timestamp, status, error, message, path, Collections.emptyMap());
    }
}
