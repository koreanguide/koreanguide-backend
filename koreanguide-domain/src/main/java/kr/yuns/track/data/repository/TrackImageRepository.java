package kr.yuns.track.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.track.data.entity.Track;
import kr.yuns.track.data.entity.TrackImage;

import java.util.List;

public interface TrackImageRepository extends JpaRepository<TrackImage, Long> {
    List<TrackImage> findAllByTrack(Track track);
}