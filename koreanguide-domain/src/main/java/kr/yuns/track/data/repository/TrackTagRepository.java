package kr.yuns.track.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.yuns.track.data.entity.Track;
import kr.yuns.track.data.entity.TrackTag;

import java.util.List;

public interface TrackTagRepository extends JpaRepository<TrackTag, Long> {
    List<TrackTag> findAllByTrack(Track track);
}