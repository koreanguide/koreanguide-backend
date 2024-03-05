package com.koreanguide.koreanguidebackend.domain.track.data.dao;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackImageApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackTagApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackImage;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackTag;

import java.util.List;

public interface TrackDao {
    void deleteTrack(Long trackId, Long userId);
    List<Track> getUserAllTrack(User user);
    Long trackLikeCount(Track track);
    Track getTrackEntity(Long trackId);
    List<TrackTag> getTrackTagEntityViaId(Long trackId);
    List<TrackTag> getTrackTagEntityViaEntity(Track track);
    List<TrackImage> getTrackImageEntityViaId(Long trackId);
    List<TrackImage> getTrackImageEntityViaEntity(Track track);
    void saveTrack(Track track);

    void updateTrackTag(Track track, List<TrackTagApplyRequestDto> trackTagApplyRequestDtoList);

    void updateTrackImage(Track track, List<TrackImageApplyRequestDto> trackImageApplyRequestDtoList);

    List<Track> getTopTrackList();
}
