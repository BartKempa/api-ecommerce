package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.delivery.DeliveryService;
import com.example.apiecommerce.domain.delivery.dto.DeliveryDto;
import com.example.apiecommerce.domain.delivery.dto.DeliveryUpdateDto;
import com.example.apiecommerce.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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


    @Operation(
            summary = "Get all deliveries",
            description = "Retrieve a list of all deliveries")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Got the list of all deliveries",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = DeliveryDto.class)),
                            examples = @ExampleObject(value = """
                                [
                                    {
                                        "id": 1,
                                        "deliveryName": "Kurier DPP",
                                        "deliveryTime": "1-2 dni",
                                        "deliveryCharge": 12.5
                                    },
                                    {
                                        "id": 2,
                                        "deliveryName": "Poczta",
                                        "deliveryTime": "3-4 dni",
                                        "deliveryCharge": 10.5
                                    }
                                ]                                
                                """
                            )
                    )
            )
    })
    @GetMapping
    ResponseEntity<List<DeliveryDto>> getAllDeliveries(){
        return ResponseEntity.ok(deliveryService.findAllActiveDeliveries());
    }


    @Operation(
            summary = "Get a delivery by its id",
            description = "Retrieve a delivery by its id" )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the delivery",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryDto.class),
                            examples = @ExampleObject(value = """
                            {
                            "id": 1,
                            "deliveryName": "Kurier DPP",
                            "deliveryTime": "1-2 dni",
                            "deliveryCharge": 12.5
                            }
                        """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Delivery not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Delivery not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{id}")
    ResponseEntity<DeliveryDto> getDeliveryById(
            @Parameter(
                    description = "id of delivery to be searched",
                    required = true,
                    example = "1"
            )
            @PathVariable @Min(1) Long id){
        return deliveryService.findDeliveryById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Delete a delivery",
            description = "Delete a delivery by its id")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Delivery deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Delivery not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "delivery not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteDeliveryById(
            @Parameter(
                    description = "id of delivery to be deleted",
                    required = true,
                    example = "1")
            @PathVariable @Min(1) Long id){
        deliveryService.deleteDelivery(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Update details about a delivery",
            description = "Partially update delivery details by its ID. Only provided fields will be updated."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Delivery updated successfully (no content returned)."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Delivery not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Delivery not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
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
    @PatchMapping("/{id}")
    ResponseEntity<?> updateDelivery(
            @Parameter(
                    description = "id of the delivery to be updated",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,
            @Valid @RequestBody DeliveryUpdateDto deliveryUpdateDto) {
        deliveryService.updateDelivery(id, deliveryUpdateDto);
        return ResponseEntity.noContent().build();
    }
}
