package com.lps.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.lps.api.dtos.StudentRegisterDto;
import com.lps.api.models.Address;
import com.lps.api.models.Company;
import com.lps.api.models.Course;
import com.lps.api.models.Department;
import com.lps.api.models.Institution;
import com.lps.api.repositories.DepartmentRepository;
import com.lps.api.repositories.InstitutionRepository;
import com.lps.api.services.CompanyService;
import com.lps.api.services.CourseService;
import com.lps.api.services.StudentService;

@Configuration
@EnableWebMvc
@Component
public class WebConfig implements WebMvcConfigurer, CommandLineRunner {
  
        @Autowired
        private InstitutionRepository institutionRepository;
        
        @Autowired
        private DepartmentRepository departmentRepository;
        
        @Autowired
        private CourseService courseService;

        @Autowired
        private CompanyService companyService;

        @Autowired
        private StudentService studentService;

        @Autowired
        private final PasswordEncoder encoder = new BCryptPasswordEncoder();



        @Override
        public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*").allowedMethods("GET", "POST", "PUT", "DELETE");
        }

        @Value("${spring.profiles.active}")
        private String activeProfile;

        @Override
        public void run(String... args) throws Exception {
                if(!activeProfile.equals("test")) {
                        return;
                }

                Company company1 = new Company(null, "Empresa1", "empresa1@gmail.com", "123456");
                companyService.save(company1);
                
                Institution institution1 = new Institution(null, "Puc Minas");
                institutionRepository.saveAll(Arrays.asList(institution1));

                Department department1 = new Department(1L, "Engenharia de Software", institution1);
                departmentRepository.saveAll(Arrays.asList(department1));
                
                Course course1 = new Course(null, "Engenharia de Software", department1);
                course1 = courseService.save(course1);

                StudentRegisterDto student1 = new StudentRegisterDto("Pedro", "Pedronll@outlook.com", "123456", "12345678912", 20.00, "123", course1.getId(), new Address(null, "rua", 123, "casa", "bairro", "cidade", "estado", "cep"));
                studentService.save(student1);
                
        }
}