package kr.yuns.track.data.dao;

import java.util.List;

import kr.yuns.auth.data.entity.User;
import kr.yuns.track.data.dto.request.TrackImageApplyRequestDto;
import kr.yuns.track.data.dto.request.TrackTagApplyRequestDto;
import kr.yuns.track.data.entity.Track;
import kr.yuns.track.data.entity.TrackImage;
import kr.yuns.track.data.entity.TrackTag;

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