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

import com.lps.api.models.Address;
import com.lps.api.repositories.AddressRepository;

class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private AddressService addressService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAll() {
        List<Address> addresses = Arrays.asList(new Address(), new Address());

        when(addressRepository.findAll()).thenReturn(addresses);

        List<Address> result = addressService.findAll();

        assertEquals(2, result.size());
        verify(addressRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Found() {
        Address address = new Address();
        address.setId(1L);

        when(addressRepository.findById(1L)).thenReturn(Optional.of(address));

        Address result = addressService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class, () -> {
            addressService.findById(1L);
        });

        verify(addressRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        Address address = new Address();
        address.setId(1L);

        when(addressRepository.save(address)).thenReturn(address);

        Address result = addressService.save(address);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(addressRepository, times(1)).save(address);
    }
}
