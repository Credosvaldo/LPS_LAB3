package com.lps.api.controllers;

import com.lps.api.dtos.ChangePasswordDTO;
import com.lps.api.dtos.ForgetPasswordDTO;
import com.lps.api.dtos.auth.LoginRequest;
import com.lps.api.dtos.auth.LoginResponse;
import com.lps.api.models.NaturalPerson;
import com.lps.api.models.User;
import com.lps.api.security.JwtTokenProvider;
import com.lps.api.services.EmailSenderService;
import com.lps.api.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import jakarta.mail.MessagingException;

import java.io.UnsupportedEncodingException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private EmailSenderService emailSenderService;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticateUser() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        Authentication authentication = mock(Authentication.class);
        User user = new NaturalPerson();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userService.findByEmail(loginRequest.email())).thenReturn(user);
        when(tokenProvider.generateToken(authentication)).thenReturn("mockJwtToken");

        ResponseEntity<?> response = authController.authenticateUser(loginRequest);

        assertEquals(200, response.getStatusCode().value());
        LoginResponse loginResponse = (LoginResponse) response.getBody();
        assertEquals("isStudentOrTeacher", loginResponse.getUserType());
    }

    @Test
    public void testChangePassword() {
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO("oldPass", "newPass", null, null);

        doNothing().when(userService).changePassword(changePasswordDTO);

        ResponseEntity<?> response = authController.changePassword(changePasswordDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Password changed", response.getBody());
    }

    @Test
    public void testSendToken() throws UnsupportedEncodingException, MessagingException {
        ForgetPasswordDTO forgetPasswordDTO = new ForgetPasswordDTO("test@example.com");
        User user = new User();
        user.setEmail("test@example.com");

        when(userService.findByEmail(forgetPasswordDTO.email())).thenReturn(user);
        doNothing().when(userService).createPasswordResetTokenForUser(anyLong(), anyString());
        doNothing().when(emailSenderService).sendRecoveryPasswordMail(anyString(), anyString());

        ResponseEntity<?> response = authController.sendToken(forgetPasswordDTO);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("Email enviado para o usuário test@example.com", response.getBody());
    }

    @Test
    public void testSendToken_UserNotFound() {
        ForgetPasswordDTO forgetPasswordDTO = new ForgetPasswordDTO("nonexistent@example.com");

        when(userService.findByEmail(forgetPasswordDTO.email())).thenThrow(NoSuchElementException.class);

        try {
            authController.sendToken(forgetPasswordDTO);
        } catch (NoSuchElementException | UnsupportedEncodingException | MessagingException e) {
            assertEquals(NoSuchElementException.class, e.getClass());
        }
    }
}