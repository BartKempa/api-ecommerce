package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.user.UserService;
import com.example.apiecommerce.domain.user.dto.UserRegistrationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;
    private final ObjectMapper objectMapper;

    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
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

    @Operation(summary = "Update details about user", description = "Update details about user by its id" )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204",
                    description = "User updated successfully",
                    content =  @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRegistrationDto.class))),
            @ApiResponse(responseCode = "500",
                    description = "Internal server error",
                    content = @Content),
            @ApiResponse(responseCode = "404",
                    description = "User not found",
                    content = @Content) })
    @PatchMapping("/{id}")
    ResponseEntity<?> updateUser(@Parameter(description = "id of user to be updated", required = true, example = "4")
                                 @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                         description = "Details of the user to update.", required = true,
                                         content = @Content(mediaType = "application/json",
                                                 schema = @Schema(implementation = UserRegistrationDto.class),
                                                 examples = @ExampleObject(value = """
                    {
                        "firstName" : "Bartosz",
                        "lastName" : "Kemp",
                        "phoneNumber" : "506506506"
                    }""")))
            @Valid @PathVariable Long id, @RequestBody JsonMergePatch patch){
        try {
            UserRegistrationDto userRegistrationDto = userService.findUserById(id).orElseThrow();
            UserRegistrationDto userPatched = applyPatch(userRegistrationDto, patch);
            userService.updateUser(userPatched);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.internalServerError().build();
        } catch (NoSuchElementException e){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private UserRegistrationDto applyPatch(UserRegistrationDto userRegistrationDto, JsonMergePatch patch) throws JsonPatchException, JsonProcessingException {
        JsonNode jsonUserNode = objectMapper.valueToTree(userRegistrationDto);
        JsonNode jsonUserPatchNode = patch.apply(jsonUserNode);
        return objectMapper.treeToValue(jsonUserPatchNode, UserRegistrationDto.class);
    }
}
