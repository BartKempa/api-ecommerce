package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.address.AddressService;
import com.example.apiecommerce.domain.address.dto.AddressDto;
import com.example.apiecommerce.domain.address.dto.AddressUpdateDto;
import com.example.apiecommerce.domain.product.dto.ProductDto;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {
    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }


    @Operation(
            summary = "Create a new address",
            description = "Create a new address and add it to the database"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Address created successfully",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                    "id": 1,
                                    "streetName": "Pawia",
                                    "buildingNumber": "123",
                                    "apartmentNumber": "321",
                                    "zipCode": "80800",
                                    "city": "Sopot",
                                    "userId": 1
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
    ResponseEntity<AddressDto> addAddress(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of address to save.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressDto.class),
                            examples = @ExampleObject(value = """
                            {
                                "streetName": "Pawia",
                                "buildingNumber": "123",
                                "apartmentNumber": "321",
                                "zipCode": "80800",
                                "city": "Sopot",
                                "userId": 1
                            }
                            """)
                    )
            )
            @Valid @RequestBody AddressDto addressDto){
        AddressDto savedAddress = addressService.saveAddress(addressDto);
        URI savedAddressUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedAddress.getId())
                .toUri();
        return ResponseEntity.created(savedAddressUri).body(savedAddress);
    }


    @Operation(
            summary = "Delete an address",
            description = "Delete an address by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Address successfully deleted"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Address not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteAddress(
            @Parameter(
                    description = "ID of the address to be deleted",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Update details about a address",
            description = "Partially update address details by its ID. Only provided fields will be updated."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Address updated successfully (no content returned)."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Address not found",
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
    ResponseEntity<?> updateAddress(
            @Parameter(
                    description = "ID of the address to be updated",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the address to update. Only non-null fields will be updated.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressUpdateDto.class),
                            examples = @ExampleObject(value = """
                        {
                                "streetName": "Pawia",
                                "buildingNumber": "123",
                                "apartmentNumber": "321",
                                "zipCode": "80800",
                                "city": "Sopot",
                                "
                        }
                        """)
                    )
            )
            @Valid @RequestBody AddressUpdateDto addressUpdateDto) {
            addressService.updateAddress(id, addressUpdateDto);
            return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Get a address by its id",
            description = "Retrieve a address by its id" )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the address",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressDto.class),
                            examples = @ExampleObject(value = """
                            {
                              "id": 1,
                              "streetName": "Pawia",
                              "buildingNumber": "123",
                              "apartmentNumber": "321",
                              "zipCode": "80800",
                              "city": "Sopot",
                              "userId": 1
                            }
                        """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Address not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "Address not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{id}")
    ResponseEntity<AddressDto> getAddressById(
            @Parameter(
                    description = "Id of address to be searched.",
                    required = true,
                    example = "1")
            @PathVariable @Min(1) Long id){
        return addressService.findAddressById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));
    }
}
