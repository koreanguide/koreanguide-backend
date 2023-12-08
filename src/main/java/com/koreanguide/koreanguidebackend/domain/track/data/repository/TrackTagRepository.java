package com.koreanguide.koreanguidebackend.domain.track.data.repository;

import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackTagRepository extends JpaRepository<TrackTag, Long> {
}
