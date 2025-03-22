package kr.yuns.track.data.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import kr.yuns.auth.data.entity.User;
import kr.yuns.track.data.entity.Track;

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