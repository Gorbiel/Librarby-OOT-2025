package agh.oot.librarby.exception;

import java.time.LocalDateTime;
import java.util.Map;

public interface ErrorResponse {
    LocalDateTime timestamp();

    int status();

    String error();

    String message();

    String path();

    Map<String, Object> metadata();
}