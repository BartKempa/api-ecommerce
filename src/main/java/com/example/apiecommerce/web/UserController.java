package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.address.dto.AddressDto;
import com.example.apiecommerce.domain.creditCard.dto.CreditCardForReturnDto;
import com.example.apiecommerce.domain.user.UserService;
import com.example.apiecommerce.domain.user.dto.UserRegistrationDto;
import com.example.apiecommerce.domain.user.dto.UserUpdateDto;
import com.example.apiecommerce.domain.user.dto.UserUpdatePasswordDto;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Operation(
            summary = "Get a user by its id",
            description = "Retrieve a user by its id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the user",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRegistrationDto.class),
                            examples = @ExampleObject(value = """
                                        {
                                                "id": 3,
                                                "email": "secondUser@mail.com",
                                                "password": "{noop}******",
                                                "firstName": "Daga",
                                                "lastName": "Szczepankowa",
                                                "phoneNumber": "506112233"
                                        }
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "User not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{id}")
    ResponseEntity<UserRegistrationDto> getUserById(
            @Parameter(
                    description = "id of user to be searched",
                    required = true,
                    example = "3")
            @PathVariable Long id){
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(
            summary = "Update details about a user",
            description = "Partially update user details by its ID. Only provided fields will be updated."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User updated successfully (no content returned)."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "User not found",
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
    public ResponseEntity<?> updateUser(
            @Parameter(
                    description = "ID of the user to be updated",
                    required = true,
                    example = "4")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Details of the user to update. Only non-null fields will be updated.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdateDto.class),
                            examples = @ExampleObject(value = """
                        {
                            "firstName" : "Bartosz",
                            "lastName" : "Kemp",
                            "phoneNumber" : "506506506"
                        }
                        """)
                    )
            )
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        try {
            userService.updateUser(id, userUpdateDto);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Delete user",
            description = "Delete user by its ID."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully (no content returned)."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "User not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(
                    description = "ID of the user to be deleted",
                    required = true,
                    example = "4")
            @PathVariable Long id) {
        try {
            userService.deleteUser(id);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Update user password",
            description = "Update user password by its ID. Only password can be updated."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "User password updated successfully (no content returned)."
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "User not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @PatchMapping("/password/{id}")
    public ResponseEntity<?> updateUserPassword(
            @Parameter(
                    description = "ID of the user to be updated",
                    required = true,
                    example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Password of the user to update.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserUpdatePasswordDto.class),
                            examples = @ExampleObject(value = """
                        {
                            "password" : "P@ssw0rd123!"                      
                        }
                        """)
                    )
            )
            @Valid @RequestBody UserUpdatePasswordDto userUpdatePasswordDto) {
            userService.updateUserPassword(id, userUpdatePasswordDto);
            return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Get user addresses by its id",
            description = "Retrieve a list of user addresses by its id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the list of user addresses",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AddressDto.class),
                            examples = @ExampleObject(value = """
                                        [
                                             {
                                                 "id": 1,
                                                 "streetName": "Pawia",
                                                 "buildingNumber": "123",
                                                 "apartmentNumber": "321",
                                                 "zipCode": "80800",
                                                 "city": "Sopot",
                                                 "userId": 3
                                             }
                                         ]
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "User not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{id}/addresses")
    ResponseEntity<List<AddressDto>> getUserAddresses(
            @Parameter(
                    description = "id of user to be searched",
                    required = true,
                    example = "3")
            @PathVariable Long id){
        var addresses = userService.findAllUserAddresses(id);
        return ResponseEntity.ok(addresses);
    }



    @Operation(
            summary = "Get all credit cards of a user",
            description = "Retrieve a list of user credit cards by its id"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the list of user credit cards",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreditCardForReturnDto.class),
                            examples = @ExampleObject(value = """
                                    [
                                        {
                                            "id": 1,
                                            "abbreviationCardNumber": "**** **** **** 9350",
                                            "abbreviationCardValidity": "**/**"
                                        }
                                    ]
                                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "message": "User not found",
                                        "timestamp": "2025-01-21T14:45:00"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{id}/creditCards")
    ResponseEntity<List<CreditCardForReturnDto>> getUserCreditCards(
            @Parameter(
                    description = "id of user to be searched",
                    required = true,
                    example = "4")
            @PathVariable @Valid @Min(1) Long id){
        var allUserCreditCards = userService.findAllUserCreditCards(id);
        return ResponseEntity.ok(allUserCreditCards);
    }
}
