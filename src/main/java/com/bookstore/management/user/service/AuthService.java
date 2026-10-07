package com.bookstore.management.user.service;

import com.bookstore.management.security.JwtUtils;
import com.bookstore.management.shared.exception.custom.DuplicateEntityException;
import com.bookstore.management.shared.exception.custom.ResourceNotFoundException;
import com.bookstore.management.user.dto.AuthResponseDTO;
import com.bookstore.management.user.dto.LoginRequestDTO;
import com.bookstore.management.user.dto.RegisterRequestDTO;
import com.bookstore.management.user.entity.Role;
import com.bookstore.management.user.entity.User;
import com.bookstore.management.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEntityException("User", "email", request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.EMPLOYEE)
                .build();

        userRepository.save(user);

        return buildAuthResponse(user);
    }

    public AuthResponseDTO login(LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", request.getEmail()));

        return buildAuthResponse(user);
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        String token = jwtUtils.generateToken(user);
        return AuthResponseDTO.builder()
                .token(token)
                .expiresInMillis(jwtUtils.getExpirationMillis())
                .build();
    }
}
