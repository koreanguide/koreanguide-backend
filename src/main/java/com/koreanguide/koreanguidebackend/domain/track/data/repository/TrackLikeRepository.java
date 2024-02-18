package com.koreanguide.koreanguidebackend.domain.track.data.repository;

import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackLikeRepository extends JpaRepository<TrackLike, Long> {
    List<TrackLike> findAllByTrack(Track track);
}
