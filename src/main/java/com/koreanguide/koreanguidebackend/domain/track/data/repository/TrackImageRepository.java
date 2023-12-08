package com.koreanguide.koreanguidebackend.domain.track.data.repository;

import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrackImageRepository extends JpaRepository<TrackImage, Long> {
}
