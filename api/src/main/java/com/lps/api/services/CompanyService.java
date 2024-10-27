package com.lps.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lps.api.models.Company;
import com.lps.api.repositories.CompanyRepository;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class CompanyService {
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public List<Company> findAll() {
        return companyRepository.findAll();
    }

    public Company findById(Long id) {
        return companyRepository.findById(id).orElse(null);
    }

    public Company save(Company company) {
        if(company.getName().isEmpty()) {
            throw new IllegalArgumentException("Nome da empresa não pode ser vazio");	
        }
        if(company.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email da empresa não pode ser vazio");	
        }
        if(company.getPassword().length() < 6) {
            throw new IllegalArgumentException("Senha da empresa tem que ter ao menos 6 digitos");	
        }

        var newPassword = encoder.encode(company.getPassword());
        company.setPassword(newPassword);
        
        return companyRepository.save(company);
    }

    public void deleteById(Long id) {
        companyRepository.deleteById(id);
    }

    public Company updateCompany(Long id, Company company) {
        Optional<Company> existingCompany = companyRepository.findById(id);

        if (!existingCompany.isPresent()) {
            throw new RuntimeException("Professor not found with id " + id);
        }

        if(company.getName().isEmpty()) {
            throw new IllegalArgumentException("Nome da empresa não pode ser vazio");	
        }
        
        Company updatedCompany = existingCompany.get();
        updatedCompany.setName(company.getName());

        return companyRepository.save(updatedCompany);
 
    }

}
