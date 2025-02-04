package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.delivery.DeliveryService;
import com.example.apiecommerce.domain.delivery.dto.DeliveryDto;
import com.example.apiecommerce.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {
    private final DeliveryService deliveryService;

    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }

    @Operation(
            summary = "Create a new delivery",
            description = "Create a new delivery and add it to the database")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Delivery created successfully",
                    content =  @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "deliveryName": "Kurier DPP",
                                      "deliveryTime": "1-2 dni",
                                      "deliveryCharge": 12.50
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
    @PostMapping
    ResponseEntity<DeliveryDto> addDelivery(
            @Valid @RequestBody DeliveryDto deliveryDto){
        DeliveryDto saveDelivery = deliveryService.saveDelivery(deliveryDto);
        URI savedDeliveryUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saveDelivery.getId())
                .toUri();
        return ResponseEntity.created(savedDeliveryUri).body(saveDelivery);
    }


}
