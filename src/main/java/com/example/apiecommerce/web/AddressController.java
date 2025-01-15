package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.address.AddressService;
import com.example.apiecommerce.domain.address.dto.AddressDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
                    content = @Content) })
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
}
