package com.koreanguide.koreanguidebackend.domain.track.data.repository;

import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackRepository extends JpaRepository<Track, Long> {
}
