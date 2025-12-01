package com.lps.api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lps.api.dtos.ChangePasswordDTO;
import com.lps.api.dtos.ForgetPasswordDTO;
import com.lps.api.dtos.auth.LoginRequest;
import com.lps.api.models.Department;
import com.lps.api.models.Institution;
import com.lps.api.models.Professor;
import com.lps.api.models.User;
import com.lps.api.repositories.DepartmentRepository;
import com.lps.api.repositories.InstitutionRepository;
import com.lps.api.repositories.UserRepository;
import com.lps.api.services.EmailSenderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private EmailSenderService emailSenderService;

    private User testUser;
    private String testPassword = "password123";

    @BeforeEach
    public void setUp() {
        // Configuração dos dados de teste - executado antes de cada teste
        // O @Transactional garante que os dados serão removidos após cada teste
        
        // Criar instituição de teste
        Institution institution = new Institution();
        institution.setName("Test University");
        institution = institutionRepository.save(institution);
        
        // Criar departamento de teste
        Department department = new Department();
        department.setName("Computer Science");
        department.setInstitution(institution);
        department = departmentRepository.save(department);
        
        // Criar um professor de teste com senha criptografada
        Professor professor = new Professor();
        professor.setName("Test Professor");
        professor.setEmail("professor@example.com");
        professor.setPassword(passwordEncoder.encode(testPassword));
        professor.setCpf("12345678901");
        professor.setBalance(0L);
        professor.setDepartment(department);
        
        testUser = userRepository.save(professor);
    }

    @Test
    public void testAuthenticateUser_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest(testUser.getEmail(), testPassword);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", notNullValue()))
                .andExpect(jsonPath("$.userType", is("isTeacher")))
                .andExpect(jsonPath("$.id", is(testUser.getId().intValue())));
    }

    @Test
    public void testAuthenticateUser_InvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest(testUser.getEmail(), "wrongpassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void testAuthenticateUser_UserNotFound() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", testPassword);

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void testChangePassword_WithInvalidToken() throws Exception {
        // Testar mudança de senha com token inválido
        ChangePasswordDTO changePasswordDTO = new ChangePasswordDTO(
            "invalidToken123", 
            testUser.getEmail(),
            "newPassword123", 
            "newPassword123"
        );

        mockMvc.perform(post("/auth/changePassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(changePasswordDTO)))
                .andExpect(status().is5xxServerError());
    }

    @Test
    public void testSendToken_Success() throws Exception {
        ForgetPasswordDTO forgetPasswordDTO = new ForgetPasswordDTO(testUser.getEmail());
        
        // Mock do serviço de email para não enviar email real
        doNothing().when(emailSenderService).sendRecoveryPasswordMail(anyString(), anyString());

        mockMvc.perform(post("/auth/forgotPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgetPasswordDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Email enviado para o usuário")))
                .andExpect(content().string(containsString(testUser.getEmail())));
    }

    @Test
    public void testSendToken_UserNotFound() throws Exception {
        ForgetPasswordDTO forgetPasswordDTO = new ForgetPasswordDTO("nonexistent@example.com");

        mockMvc.perform(post("/auth/forgotPassword")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgetPasswordDTO)))
                .andExpect(status().is5xxServerError());
    }
}