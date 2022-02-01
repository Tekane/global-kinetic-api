package com.example.globalkineticapi.repositories;

import com.example.globalkineticapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findOneByUsername(String username);
    Optional<User> findById(Long id);
    Boolean existsByUsername(String username);
}
