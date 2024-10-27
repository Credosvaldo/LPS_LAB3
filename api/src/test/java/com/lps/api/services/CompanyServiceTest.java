package com.lps.api.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.lps.api.models.Company;
import com.lps.api.repositories.CompanyRepository;

class CompanyServiceTest {

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private CompanyService companyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        Company company1 = new Company(1L, "Company A", null, null);
        Company company2 = new Company(2L, "Company B", null, null);

        when(companyRepository.findAll()).thenReturn(Arrays.asList(company1, company2));

        List<Company> result = companyService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Company A", result.get(0).getName());
        assertEquals("Company B", result.get(1).getName());

        verify(companyRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        Company company = new Company(1L, "Company A", null, null);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        Company result = companyService.findById(1L);

        assertNotNull(result);
        assertEquals("Company A", result.getName());
        assertEquals(1L, result.getId());

        verify(companyRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        Company result = companyService.findById(1L);

        assertNull(result);

        verify(companyRepository, times(1)).findById(1L);
    }

    @Test
    void testSaveCompany() {
        Company company = new Company(1L, "Company A", null, null);
        when(companyRepository.save(company)).thenReturn(company);

        Company result = companyService.save(company);

        assertNotNull(result);
        assertEquals("Company A", result.getName());
        assertEquals(1L, result.getId());

        verify(companyRepository, times(1)).save(company);
    }

    @Test
    void testDeleteById() {
        companyService.deleteById(1L);

        verify(companyRepository, times(1)).deleteById(1L);
    }

    @Test
    void testUpdateCompany() {
        Company existingCompany = new Company(1L, "Old Company", null, null);
        Company newCompanyData = new Company(1L, "New Company", null, null);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(existingCompany));
        when(companyRepository.save(any(Company.class))).thenReturn(existingCompany);

        Company updatedCompany = companyService.updateCompany(1L, newCompanyData);

        assertNotNull(updatedCompany);
        assertEquals("New Company", updatedCompany.getName());

        verify(companyRepository, times(1)).findById(1L);
        verify(companyRepository, times(1)).save(existingCompany);
    }

    @Test
    void testUpdateCompanyNotFound() {
        when(companyRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            companyService.updateCompany(1L, new Company(1L, "New Company", null, null));
        });

        assertEquals("Professor not found with id 1", exception.getMessage());

        verify(companyRepository, times(1)).findById(1L);
    }
}
