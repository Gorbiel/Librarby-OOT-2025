package agh.oot.librarby.publisher.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response containing a list of publishers")
public record MultiplePublishersResponse(
        @Schema(description = "List of publishers")
        List<PublisherResponse> publishers
) {}
