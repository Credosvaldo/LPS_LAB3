package com.lps.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lps.api.models.Professor;
import com.lps.api.repositories.ProfessorRepository;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class ProfessorService {
    @Autowired
    private ProfessorRepository professorRepository;

    public List<Professor> findAll() {
        return professorRepository.findAll();
    }

    public Optional<Professor> findById(Long id) {
        return professorRepository.findById(id);
    }

    public Professor save(Professor professor) {
        return professorRepository.save(professor);
    }

    public void deleteById(Long id) {
        professorRepository.deleteById(id);
    }

    public Professor updateProfessor(Long id, Professor professor) {
        if(professor.getName().isEmpty()) {
            throw new IllegalArgumentException("Nome do professor não pode ser vazio");	
        }
        if(professor.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email do professor não pode ser vazio");	
        }
        if(professor.getPassword().length() < 6) {
            throw new IllegalArgumentException("Senha do professor tem que ter ao menos 6 digitos");	
        }
        if(professor.getCpf().length() < 11) {
            throw new IllegalArgumentException("CPF inválido");	
        }

        Optional<Professor> existingProfessor = professorRepository.findById(id);
        if (existingProfessor.isPresent()) {
            Professor updatedProfessor = existingProfessor.get();
            updatedProfessor.setName(professor.getName());
            updatedProfessor.setDepartment(professor.getDepartment());
            updatedProfessor.setBalance(professor.getBalance());

            return professorRepository.save(updatedProfessor);
        } else {
            throw new RuntimeException("Professor not found with id " + id);
        }
    }

}
