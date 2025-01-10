package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.user.UserService;
import com.example.apiecommerce.domain.user.auth.AuthenticationRequest;
import com.example.apiecommerce.domain.user.dto.UserRegistrationDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final UserService userService;

    public AuthenticationController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create a new user", description = "Register a new user with default role and save to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "User created successfully",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserRegistrationDto.class),
                    examples = @ExampleObject(value = """
                            {
                                "id": 1,
                                "email": "bartek@mail.com",
                                "password": "{bcrypt}$2a$10$YFlY0VgNX50aEElAjGlDfO7/LL8gX2jJudFP05tDHTpXMWL5P1rIy",
                                "firstName": "Bartek",
                                "lastName": "Kempiak",
                                "phoneNumber": "123456789"
                            }"""))),
            @ApiResponse(responseCode = "400",
                    description = "Invalid input provided",
                    content = @Content) })
    @PostMapping("/register")
    ResponseEntity<UserRegistrationDto> registerUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Details of the user to register.", required = true,
            content = @Content(mediaType = "application/json",
            schema = @Schema(implementation = UserRegistrationDto.class),
            examples = @ExampleObject(value = """
                    {
                        "email" : "bartek@mail.com",
                        "password" : "super",
                        "firstName" : "Bartek",
                        "lastName" : "Kempiak",
                        "phoneNumber" : "123456789"
                    }""")))
            @Valid @RequestBody UserRegistrationDto userRegistrationDto){
        UserRegistrationDto savedUserDto = userService.registerWithDefaultRole(userRegistrationDto);
        URI savedUserUri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUserDto.getId())
                .toUri();
        return ResponseEntity.created(savedUserUri).body(savedUserDto);
    }

    @Operation(summary = "Login user", description = "Authenticate the user and return a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User logged in successfully and token returned.",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                 {
                                    "token": "eyJhbGciOiJIUzI1NiJ9..."
                                }"""))),
            @ApiResponse(responseCode = "401",
                    description = "Invalid username or password",
                    content = @Content) })
    @PostMapping("/login")
    public ResponseEntity<?> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User credentials for login.", required = true,
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthenticationRequest.class),
                    examples = @ExampleObject(value = """
                            {
                                "username" : "bartek@mail.com",
                                "password" : "super"
                            }""")))
            @RequestBody AuthenticationRequest authenticationRequest) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication.getName().equals(authenticationRequest.getUsername())) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.status(401).body("Invalid username or password");
            }
    }
}
