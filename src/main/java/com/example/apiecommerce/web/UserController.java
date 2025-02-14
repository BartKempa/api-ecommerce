package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.address.dto.AddressDto;
import com.example.apiecommerce.domain.order.dto.OrderFullDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    @GetMapping("/user/{id}")
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
            description = "Partially update user details. Only provided fields will be updated."
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
    @PatchMapping("/details")
    public ResponseEntity<?> updateUser(
            Authentication authentication,
            @Valid @RequestBody UserUpdateDto userUpdateDto) {
        String userName = authentication.getName();
        try {
            userService.updateUser(userName, userUpdateDto);
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
            description = "Update user password. Only password can be updated."
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
    @PatchMapping("/password")
    public ResponseEntity<?> updateUserPassword(
            Authentication authentication,
            @Valid @RequestBody UserUpdatePasswordDto userUpdatePasswordDto) {
            String userName = authentication.getName();
            userService.updateUserPassword(userName, userUpdatePasswordDto);
            return ResponseEntity.noContent().build();
    }


    @Operation(
            summary = "Get user addresses",
            description = "Retrieve a list of user addresses"
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
            )
    })
    @GetMapping("/addresses")
    ResponseEntity<List<AddressDto>> getUserAddresses(
            Authentication authentication){
        String userName = authentication.getName();
        var addresses = userService.findAllActiveUserAddresses(userName);
        return ResponseEntity.ok(addresses);
    }


    @Operation(
            summary = "Get user orders",
            description = "Retrieve a list of user orders"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Found the list of user orders",
                    content =  @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderFullDto.class),
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
            )
    })
    @GetMapping("/orders")
    ResponseEntity<List<OrderFullDto>> getUserOrders(
            Authentication authentication){
        String userName = authentication.getName();
        var orders = userService.findAllUserOrders(userName);
        return ResponseEntity.ok(orders);
    }
}
