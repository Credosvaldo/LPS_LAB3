package com.lps.api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lps.api.dtos.ChangePasswordDTO;
import com.lps.api.exceptions.ChangePasswordException;
import com.lps.api.exceptions.ObjectNotFoundException;
import com.lps.api.models.PasswordResetToken;
import com.lps.api.models.User;
import com.lps.api.repositories.PasswordTokenRepository;
import com.lps.api.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Transactional
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordTokenRepository passwordTokenRepository;

    @Autowired
    private final PasswordEncoder encoder = new BCryptPasswordEncoder();

    public PasswordEncoder getBcrypt() {
        return this.encoder;
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Usuário não encontrado"));
    }

    public void changePassword(ChangePasswordDTO changePasswordDTO)
            throws ObjectNotFoundException, ChangePasswordException {
        if (changePasswordDTO.token() != null && !changePasswordDTO.token().isEmpty()) {

            User user = userRepository.findByEmail(changePasswordDTO.email());
            PasswordResetToken token = passwordTokenRepository.findByUserAndToken(user,
                    changePasswordDTO.token());
            if (token == null) {
                throw new ChangePasswordException("Token invalido");
            }
            if (user == null) {
                throw new ObjectNotFoundException("Usuário não existente");
            }
            if (!changePasswordDTO.novaSenha().equals(changePasswordDTO.confirmacaoNovaSenha())) {
                throw new ChangePasswordException("Nova senha e a confirmação não são iguais");
            }

            user.setPassword(changePasswordDTO.novaSenha());
            save(user);
            passwordTokenRepository.delete(token);
        }
    }

    public User save(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        this.userRepository.save(user);

        return user;

    }

    public void update(User user, Long id) {

        User userInBase = this.getById(id);
        user.setId(userInBase.getId());

        this.save(user);
    }

    public User deleteUserById(Long id) throws EntityNotFoundException {
        User obj = this.getById(id);
        this.userRepository.delete(obj);

        return obj;
    }

    public void createPasswordResetTokenForUser(Long id, String token) {
        User user = this.getById(id);

        // Renomear Basetoken para baseToken para seguir as convenções de nomenclatura java 
        PasswordResetToken Basetoken = passwordTokenRepository.findByUser(user);
        PasswordResetToken myToken;
        if (Basetoken == null) {
            myToken = new PasswordResetToken(null, token, user);
        } else {
            myToken = Basetoken;
            myToken.setToken(token);
        }

        passwordTokenRepository.save(myToken);
    }

    // Retornar Optional para evitar null pointer exception
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
