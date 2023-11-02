package com.koreanguide.koreanguidebackend.domain.auth.service.impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        log.info("UserDetailsServiceImpl - loadUserByUsername: 사용자 ID로 사용자 검색 수행, ID: {}", email);
        return userRepository.getByEmail(email);
    }
}
