package com.lps.api.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lps.api.dtos.StudentRegisterDto;
import com.lps.api.mappers.StudentMapper;
import com.lps.api.models.Course;
import com.lps.api.models.Student;
import com.lps.api.repositories.StudentRepository;

import jakarta.transaction.Transactional;

@Transactional
@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    public List<Student> findByCourse(String course) {
        return studentRepository.findByCourse_Name(course);
    }

    // Retornar Optional para evitar null pointer exception
    public Student findById(Long id) {
        return studentRepository.findById(id).get();
    }

    // Retornar Optional para evitar null pointer exception
    public Student findByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    // Retornar Optional para evitar null pointer exception
    public Student findByCpf(String cpf) {
        return studentRepository.findByCpf(cpf);
    }

    @Transactional
    public Student save(StudentRegisterDto studentDTO) {
        Course course = courseService.findById(studentDTO.courseId());
        Student student = StudentMapper.toStudent(studentDTO, course);

        // Transformar em um metodo privado a validação de estudante para melhorar a clareza e reutilização de código, princípio da responsabilidade única (SRP)
        if (student.getName() == null || student.getName().isEmpty() ||
                student.getCpf() == null || student.getCpf().isEmpty()) {
            throw new IllegalArgumentException("Name and CPF are required fields");
        }

        var newPassword = encoder.encode(student.getPassword());
        student.setPassword(newPassword);

        Student studentSaved = studentRepository.save(student);


        return studentSaved;
    }

    public void deleteById(Long id) {
        studentRepository.deleteById(id);
    }

    public void delete(Student student) {
        studentRepository.delete(student);
    }

}
