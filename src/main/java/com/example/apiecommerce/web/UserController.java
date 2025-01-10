package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.user.UserService;
import com.example.apiecommerce.domain.user.dto.UserRegistrationDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get a user by its id", description = "Retrieve a user by its id" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Found the user",
                    content =  @Content(mediaType = "application/json",
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
                                    """))),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content) })
    @GetMapping("/{id}")
    ResponseEntity<UserRegistrationDto> getUserById(@Parameter(description = "id of user to be searched", required = true, example = "3")
            @PathVariable Long id){
        return userService.findUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }




}
