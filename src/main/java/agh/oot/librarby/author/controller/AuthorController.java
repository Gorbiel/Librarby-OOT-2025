package agh.oot.librarby.author.controller;

import agh.oot.librarby.author.dto.AuthorResponse;
import agh.oot.librarby.author.dto.MultipleAuthorsResponse;
import agh.oot.librarby.author.service.AuthorService;
import agh.oot.librarby.exception.ApiErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Authors", description = "Public endpoints for browsing authors")
@RestController
@RequestMapping(
        path = "/api/v1/authors",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @Operation(
            summary = "List authors",
            description = "Returns all authors, or filters by query string (case-insensitive)"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Authors retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query parameter",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping
    public ResponseEntity<MultipleAuthorsResponse> listAuthors(
            @Parameter(description = "Case-insensitive search", example = "fitz")
            @RequestParam(required = false) String q
    ) {
        MultipleAuthorsResponse body = authorService.listAuthors(q);
        return ResponseEntity.ok(body);
    }

    @Operation(summary = "Get author by ID")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Author retrieved successfully",
                    content = @Content(schema = @Schema(implementation = AuthorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Author not found",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))
            )
    })
    @GetMapping("/{authorId}")
    public ResponseEntity<AuthorResponse> getAuthorById(
            @Parameter(description = "Author ID", example = "5", required = true)
            @PathVariable Long authorId
    ) {
        AuthorResponse body = authorService.getAuthorById(authorId);
        return ResponseEntity.ok(body);
    }
}
