package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.user.UserService;
import com.example.apiecommerce.domain.user.auth.AuthenticationRequest;
import com.example.apiecommerce.domain.user.dto.UserConfirmationRegistrationDto;
import com.example.apiecommerce.domain.user.dto.UserRegistrationDto;
import com.example.apiecommerce.exception.ApiError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@Validated
public class AuthenticationController {
    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }


    @Operation(
            summary = "Create a new user",
            description = "Register a new user with default role and save to the database."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "User created successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserRegistrationDto.class),
                            examples = @ExampleObject(value = """
                            {
                                "id": 1,
                                "email": "bartek@mail.com",
                                "firstName": "Bartek",
                                "lastName": "Kempiak",
                                "phoneNumber": "123456789"
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
    @PostMapping("/register")
    ResponseEntity<UserConfirmationRegistrationDto> registerUser(
            @Valid @RequestBody UserRegistrationDto userRegistrationDto){
        UserConfirmationRegistrationDto userConfirmationRegistrationDto = userService.registerWithDefaultRole(userRegistrationDto);
        URI savedUserUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(userConfirmationRegistrationDto.getId())
                .toUri();
        return ResponseEntity.created(savedUserUri).body(userConfirmationRegistrationDto);
    }


    @Operation(
            summary = "Login user",
            description = "Authenticate the user and return a JWT token."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "User logged in successfully and token returned.",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                 {
                                    "token": "eyJhbGciOiJIUzI1NiJ9..."
                                }"""
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Invalid username or password",
                    content = @Content) })
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthenticationRequest authenticationRequest) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.getName().equals(authenticationRequest.getUsername())) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(401).body("Invalid username or password");
            }
    }
}
