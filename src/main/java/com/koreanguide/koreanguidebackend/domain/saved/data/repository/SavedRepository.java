package com.koreanguide.koreanguidebackend.domain.saved.data.repository;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.saved.data.entity.Saved;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedRepository extends JpaRepository<Saved, Long> {
    List<Saved> getAllByUser(User user);
}
