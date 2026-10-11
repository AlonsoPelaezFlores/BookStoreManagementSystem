package com.bookstore.management.user.repository;

import com.bookstore.management.user.model.Role;
import com.bookstore.management.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    boolean existsByRole(Role role);
}
