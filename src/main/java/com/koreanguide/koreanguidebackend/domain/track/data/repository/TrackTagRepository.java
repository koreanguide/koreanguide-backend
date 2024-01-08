package com.koreanguide.koreanguidebackend.domain.track.data.repository;

import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackTagRepository extends JpaRepository<TrackTag, Long> {
    List<TrackTag> findAllByTrack(Track track);
}
