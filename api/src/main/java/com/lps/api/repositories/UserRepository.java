package com.lps.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lps.api.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);

}
