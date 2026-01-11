package agh.oot.librarby.rental.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Response containing a list of rentals")
public record MultipleRentalsResponse(

        @Schema(description = "List of rentals")
        List<RentalResponse> rentals
) {}
