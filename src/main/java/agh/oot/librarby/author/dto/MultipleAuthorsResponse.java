package agh.oot.librarby.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "Response containing a list of authors")
public record MultipleAuthorsResponse(
        @Schema(description = "List of authors")
        List<AuthorResponse> authors
) {}
