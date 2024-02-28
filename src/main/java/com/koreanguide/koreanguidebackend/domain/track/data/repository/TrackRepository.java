package com.koreanguide.koreanguidebackend.domain.track.data.repository;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TrackRepository extends JpaRepository<Track, Long> {
    List<Track> getAllByUser(User user);

    @Query(value = "SELECT t FROM Track t " +
            "LEFT JOIN TrackLike tl ON t.id = tl.track.id " +
            "GROUP BY t.id " +
            "ORDER BY COUNT(tl.id) DESC, t.viewCount DESC, t.createdAt DESC",
            countQuery = "SELECT COUNT(*) FROM Track")
    List<Track> findTop3ByOrderByLikesAndViewCountAndCreatedAtDesc(Pageable pageable);
}
