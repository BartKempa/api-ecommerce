package com.example.apiecommerce.web;

import com.example.apiecommerce.domain.user.User;
import com.example.apiecommerce.domain.user.UserRepository;
import com.example.apiecommerce.domain.user.UserService;
import com.example.apiecommerce.domain.user.auth.AuthenticationRequest;
import com.example.apiecommerce.domain.user.dto.UserRegistrationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Transactional
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldRegisterUser() throws Exception {
        //given
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("test@mail.pl");
        userRegistrationDto.setPassword("Password123!@#");
        userRegistrationDto.setFirstName("Piotr");
        userRegistrationDto.setLastName("Kowalski");
        userRegistrationDto.setPhoneNumber("567567567");

        //when
        MvcResult result = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        String contentAsString = result.getResponse().getContentAsString();

        //then
        assertTrue(contentAsString.contains("test@mail.pl"));
        assertTrue(contentAsString.contains("Piotr"));
        assertTrue(contentAsString.contains("Kowalski"));
        assertTrue(contentAsString.contains("567567567"));
        assertTrue(contentAsString.contains("567567567"));
    }
    @Test
    void shouldFailWhenRegisterUserAndValidationFail() throws Exception {
        //given
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("testmailpl");
        userRegistrationDto.setPassword("pass");
        userRegistrationDto.setFirstName("Piotr");
        userRegistrationDto.setLastName("Kowalski");
        userRegistrationDto.setPhoneNumber("567567567");

        //when & that
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email[0]").value("must be a well-formed email address"));
    }
    @Test
    void shouldFailWhenRegisteringUserWithExistingEmail() throws Exception {
        //given
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("user@mail.com");
        userRegistrationDto.setPassword("Password123!@#");
        userRegistrationDto.setFirstName("Piotr");
        userRegistrationDto.setLastName("Kowalski");
        userRegistrationDto.setPhoneNumber("567567567");

        //when & then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email[0]").value("Email already exists"));
    }
    @Test
    void shouldFailWhenRegisteringUserWithoutEmail() throws Exception {
        //given
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("");
        userRegistrationDto.setPassword("Password123!@#");
        userRegistrationDto.setFirstName("Piotr");
        userRegistrationDto.setLastName("Kowalski");
        userRegistrationDto.setPhoneNumber("567567567");

        //when & then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email[0]").value("must not be blank"));
    }
    @Test
    void shouldFailWhenRegisteringUserWithoutPassword() throws Exception {
        //given
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("test@mail.pl");
        userRegistrationDto.setPassword("");
        userRegistrationDto.setLastName("Kowalski");
        userRegistrationDto.setPhoneNumber("567567567");

        //when & then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    void shouldSaveUserWithEncodedPassword() {
        //given
        UserRegistrationDto userRegistrationDto = new UserRegistrationDto();
        userRegistrationDto.setEmail("test@mail.pl");
        userRegistrationDto.setPassword("Password123!@#");
        userRegistrationDto.setFirstName("Piotr");
        userRegistrationDto.setLastName("Kowalski");
        userRegistrationDto.setPhoneNumber("567567567");

        //when
        userService.registerWithDefaultRole(userRegistrationDto);
        Optional<User> savedUser = userRepository.findByEmail("test@mail.pl");

        //then
        assertTrue(savedUser.isPresent());
        assertNotEquals("Password123!@#", savedUser.get().getPassword());
        assertTrue(passwordEncoder.matches("Password123!@#", savedUser.get().getPassword()));
    }
    @Test
    public void shouldLoginSuccessfullyAndReturnToken() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("admin@mail.com", "adminpass");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
    @Test
    public void shouldFailLoginWithInvalidPassword() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("admin@mail.com", "wrongpassword");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.token").doesNotExist());
    }
}