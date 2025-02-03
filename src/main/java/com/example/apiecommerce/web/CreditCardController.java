package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.creditCard.CreditCardService;
import com.example.apiecommerce.domain.creditCard.dto.CreditCardDto;
import com.example.apiecommerce.domain.creditCard.dto.CreditCardForReturnDto;
import com.example.apiecommerce.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/creditCards")
public class CreditCardController {
    private final CreditCardService creditCardService;

    public CreditCardController(CreditCardService creditCardService) {
        this.creditCardService = creditCardService;
    }

    @Operation(
            summary = "Create a new credit card",
            description = "Create a new credit card and add it to the database"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Credit card created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreditCardForReturnDto.class),
                            examples = @ExampleObject(value = """
                                        {
                                             "id": 1,
                                             "abbreviationCardNumber": "42108 **** **** ****",
                                             "abbreviationCardValidity": "1*/**",
                                             "abbreviationCardCVV": "1**"
                                         }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                    {
                        "message": "Invalid input",
                        "timestamp": "2025-01-21T14:45:00"
                    }
                    """)
                    )
            )
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    ResponseEntity<CreditCardForReturnDto> addCreditCard(@Valid @RequestBody CreditCardDto creditCardDto, Authentication authentication){
        String email = authentication.getName();
        CreditCardForReturnDto addedCreditCard = creditCardService.addCreditCard(email, creditCardDto);
        URI addedCreditCardUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addedCreditCard.getId())
                .toUri();
        return ResponseEntity.created(addedCreditCardUri).body(addedCreditCard);
    }


    @Operation(
            summary = "Get a credit card by its id",
            description = "Retrieve a credit card by its id" )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the credit card",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreditCardForReturnDto.class),
                            examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "abbreviationCardNumber": "**** **** **** 9350",
                              "abbreviationCardValidity": "**/**"
                            }
                        """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Credit card not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Credit card not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{id}")
    ResponseEntity<CreditCardForReturnDto> findCreditCardById(
            @Parameter(
                    description = "id of credit card to be searched.",
                    required = true,
                    example = "1")
            @PathVariable @Valid @Min(1) Long id){
        return creditCardService.getCreditCardById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Credit card not found"));
    }


    @Operation(
            summary = "Delete a credit card",
            description = "Delete a credit card by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Credit card successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Credit card not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Credit card not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteCreditCardById(
            @Parameter(
                    description = "id of the credit card to be deleted",
                    required = true,
                    example = "1"
            )
            @PathVariable @Valid @Min(1) Long id) {
        creditCardService.deleteCreditCard(id);
        return ResponseEntity.noContent().build();
    }
}
