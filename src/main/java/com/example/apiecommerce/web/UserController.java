package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.user.UserService;
import com.example.apiecommerce.domain.user.dto.UserRegistrationDto;
import com.example.apiecommerce.domain.user.dto.UserUpdateDto;
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
import org.springframework.web.bind.annotation.*;

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
                    content = @Content) })
    @GetMapping("/{id}")
    ResponseEntity<UserRegistrationDto> getUserById(
            @Parameter(description = "id of user to be searched", required = true, example = "3")
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
                    description = "User not found."
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID of the user to be updated", required = true, example = "4")
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
                    description = "User not found."
            )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(
            @Parameter(description = "ID of the user to be deleted", required = true, example = "4")
            @PathVariable Long id) {
        try {
            userService.deleteUser(id);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
