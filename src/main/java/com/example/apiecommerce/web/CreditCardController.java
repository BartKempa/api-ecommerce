package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.category.dto.CategoryDto;
import com.example.apiecommerce.domain.creditCard.CreditCardService;
import com.example.apiecommerce.domain.creditCard.dto.CreditCardDto;
import com.example.apiecommerce.domain.creditCard.dto.CreditCardForReturnDto;
import com.example.apiecommerce.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
                                        "categoryName": "Piwo"
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
    @PostAuthorize("isAuthenticated()")
    @PostMapping
    ResponseEntity<CreditCardForReturnDto> addCreditCard(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credit card to created",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreditCardDto.class),
                            examples = @ExampleObject(value = """
                    {
                        "categoryName":"Piwo"
                    }
                    """)
                    )
            )
            @Valid @RequestBody CreditCardDto creditCardDto,
            Authentication authentication){
        String email = authentication.getName();
        CreditCardForReturnDto addedCreditCard = creditCardService.addCreditCard(email, creditCardDto);
        URI addedCreditCardUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(addedCreditCard.getId())
                .toUri();
        return ResponseEntity.created(addedCreditCardUri).body(addedCreditCard);
    }

}
