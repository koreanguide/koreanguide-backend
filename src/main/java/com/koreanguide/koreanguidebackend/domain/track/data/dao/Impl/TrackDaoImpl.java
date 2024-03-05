package com.koreanguide.koreanguidebackend.domain.track.data.dao.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.review.data.entity.Review;
import com.koreanguide.koreanguidebackend.domain.review.data.repository.ReviewRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.dao.TrackDao;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackImageApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.dto.request.TrackTagApplyRequestDto;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.Track;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackImage;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackLike;
import com.koreanguide.koreanguidebackend.domain.track.data.entity.TrackTag;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackImageRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackLikeRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackRepository;
import com.koreanguide.koreanguidebackend.domain.track.data.repository.TrackTagRepository;
import com.koreanguide.koreanguidebackend.domain.track.exception.TrackNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TrackDaoImpl implements TrackDao {
    private final TrackRepository trackRepository;
    private final TrackTagRepository trackTagRepository;
    private final TrackImageRepository trackImageRepository;
    private final TrackLikeRepository trackLikeRepository;
    private final ReviewRepository reviewRepository;

    public TrackDaoImpl(TrackRepository trackRepository, TrackTagRepository trackTagRepository,
                        TrackImageRepository trackImageRepository, TrackLikeRepository trackLikeRepository,
                        ReviewRepository reviewRepository) {
        this.trackRepository = trackRepository;
        this.trackTagRepository = trackTagRepository;
        this.trackImageRepository = trackImageRepository;
        this.trackLikeRepository = trackLikeRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void deleteTrack(Long trackId, Long userId) {
        Track track = getTrackEntity(trackId);

        if(track.getUser().getId().equals(userId)) {
            throw new RuntimeException();
        }

        List<TrackLike> trackLikeList = trackLikeRepository.findAllByTrack(track);
        trackLikeRepository.deleteAll(trackLikeList);

        List<TrackTag> trackTagList = trackTagRepository.findAllByTrack(track);
        trackTagRepository.deleteAll(trackTagList);

        List<TrackImage> trackImageList = trackImageRepository.findAllByTrack(track);
        trackImageRepository.deleteAll(trackImageList);

        List<Review> reviewList = reviewRepository.getAllByTrack(track);
        reviewRepository.deleteAll(reviewList);

        trackRepository.delete(track);
    }

    @Override
    public List<Track> getUserAllTrack(User user) {
        return trackRepository.getAllByUser(user);
    }

    @Override
    public Long trackLikeCount(Track track) {
        List<TrackLike> trackLikeList = trackLikeRepository.findAllByTrack(track);
        return (long) trackLikeList.size();
    }

    @Override
    public Track getTrackEntity(Long trackId) {
        Optional<Track> track = trackRepository.findById(trackId);

        if(track.isEmpty()) {
            throw new TrackNotFoundException();
        }

        return track.get();
    }

    @Override
    public List<TrackTag> getTrackTagEntityViaId(Long trackId) {
        return trackTagRepository.findAllByTrack(getTrackEntity(trackId));
    }

    @Override
    public List<TrackTag> getTrackTagEntityViaEntity(Track track) {
        return trackTagRepository.findAllByTrack(track);
    }

    @Override
    public List<TrackImage> getTrackImageEntityViaId(Long trackId) {
        return trackImageRepository.findAllByTrack(getTrackEntity(trackId));
    }

    @Override
    public List<TrackImage> getTrackImageEntityViaEntity(Track track) {
        return trackImageRepository.findAllByTrack(track);
    }

    @Override
    public void saveTrack(Track track) {
        trackRepository.save(track);
    }

    @Override
    public void updateTrackTag(Track track, List<TrackTagApplyRequestDto> trackTagApplyRequestDtoList) {
        List<TrackTag> trackTagList = getTrackTagEntityViaEntity(track);
        trackTagRepository.deleteAll(trackTagList);

        for(TrackTagApplyRequestDto trackTagApplyRequestDto : trackTagApplyRequestDtoList) {
            trackTagRepository.save(TrackTag.builder()
                            .track(track)
                            .tagName(trackTagApplyRequestDto.getTagName())
                    .build());
        }
    }

    @Override
    public void updateTrackImage(Track track, List<TrackImageApplyRequestDto> trackImageApplyRequestDtoList) {
        List<TrackImage> trackImageList = getTrackImageEntityViaEntity(track);
        trackImageRepository.deleteAll(trackImageList);

        for(TrackImageApplyRequestDto trackImageApplyRequestDto : trackImageApplyRequestDtoList) {
            trackImageRepository.save(TrackImage.builder()
                            .track(track)
                            .imageUrl(trackImageApplyRequestDto.getImageUrl())
                    .build());
        }
    }

    @Override
    public List<Track> getTopTrackList() {
        return trackRepository.findTop3ByOrderByLikesAndViewCountAndCreatedAtDesc(PageRequest.of(0, 3));
    }
}
