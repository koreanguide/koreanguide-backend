package kr.yuns.saved.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.auth.data.entity.User;
import kr.yuns.saved.data.entity.Saved;

import java.util.List;

public interface SavedRepository extends JpaRepository<Saved, Long> {
    List<Saved> getAllByUser(User user);
}