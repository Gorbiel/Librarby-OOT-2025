package agh.oot.librarby.publisher.controller;

import agh.oot.librarby.publisher.dto.MultiplePublishersResponse;
import agh.oot.librarby.publisher.dto.PublisherCreateRequest;
import agh.oot.librarby.publisher.dto.PublisherResponse;
import agh.oot.librarby.publisher.dto.PublisherUpdateRequest;
import agh.oot.librarby.publisher.service.PublisherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Publisher", description = "Endpoints for managing publishers")
@RequestMapping(
        path = "/api/v1/publishers",
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class PublisherController {

    private final PublisherService publisherService;

    public PublisherController(PublisherService publisherService) {
        this.publisherService = publisherService;
    }

    @Operation(
            summary = "Get all publishers",
            description = "Retrieve a list of all publishers, optionally filtered by a query string."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Publishers retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid query parameter"
            )
    })
    @GetMapping
    public ResponseEntity<MultiplePublishersResponse> getAllPublishers(@RequestParam(required = false) String q) {
        return ResponseEntity.ok(publisherService.getAllPublishers(q));
    }

    @Operation(
            summary = "Get publisher by ID",
            description = "Retrieve a publisher by its unique ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Publisher retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Publisher not found"
            )
    })
    @GetMapping(path = "/{publisherId}")
    public ResponseEntity<PublisherResponse> getPublisherById(
            @Parameter(description = "ID of the publisher to retrieve", example = "1")
            @PathVariable Long publisherId
    ) {
        PublisherResponse response = publisherService.getPublisherById(publisherId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Create a new publisher",
            description = "Create a new publisher with the provided details."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Publisher created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PublisherResponse> createPublisher(@Valid @RequestBody PublisherCreateRequest request) {
        PublisherResponse response = publisherService.createPublisher(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Update an existing publisher",
            description = "Update the details of an existing publisher by its ID."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Publisher updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request data"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Publisher not found"
            )
    })
    @PreAuthorize("hasAnyRole('ADMIN','LIBRARIAN')")
    @PutMapping(path = "/{publisherId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PublisherResponse> updatePublisher(
            @Parameter(description = "ID of the publisher to update", example = "1")
            @PathVariable Long publisherId,
            @Valid @RequestBody PublisherUpdateRequest request
    ) {
        PublisherResponse response = publisherService.updatePublisher(publisherId, request);
        return ResponseEntity.ok(response);
    }
}
