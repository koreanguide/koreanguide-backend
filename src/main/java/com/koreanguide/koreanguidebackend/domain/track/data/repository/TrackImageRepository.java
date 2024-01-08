package com.koreanguide.koreanguidebackend.domain.track.data.repository;

import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrackImageRepository extends JpaRepository<TrackImage, Long> {
    List<TrackImage> findAllByTrack(Track track);
}
