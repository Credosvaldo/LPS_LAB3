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

    // Adicione um método privado para validar os dados do professor antes de salvar
    public Professor updateProfessor(Long id, Professor professor) {
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
