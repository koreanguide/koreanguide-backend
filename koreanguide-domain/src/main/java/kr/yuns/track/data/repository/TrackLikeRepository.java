package kr.yuns.track.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.track.data.entity.Track;
import kr.yuns.track.data.entity.TrackLike;

import java.util.List;

public interface TrackLikeRepository extends JpaRepository<TrackLike, Long> {
    List<TrackLike> findAllByTrack(Track track);
}