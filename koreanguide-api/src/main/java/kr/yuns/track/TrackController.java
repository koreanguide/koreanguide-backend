package kr.yuns.track;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import kr.yuns.JwtTokenProvider;
import kr.yuns.auth.data.dao.UserDao;
import kr.yuns.track.data.dto.request.TrackApplyRequestDto;
import kr.yuns.track.data.dto.request.TrackRemoveRequestDto;
import kr.yuns.track.data.dto.request.TrackUpdateRequestDto;
import kr.yuns.track.service.TrackService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/track")
@RequiredArgsConstructor
public class TrackController {
    private final TrackService trackService;
    private final JwtTokenProvider tokenProvider;
    private final UserDao userDao;

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return userDao.getUserId(tokenProvider.getUserEmail(request));
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllTrack(HttpServletRequest request) {
        return trackService.getAllTrack(GET_USER_ID_BY_TOKEN(request));
    }

    @PostMapping("/")
    public ResponseEntity<?> applyTrack(HttpServletRequest request, @RequestBody TrackApplyRequestDto trackApplyRequestDto) {
        return trackService.applyTrack(GET_USER_ID_BY_TOKEN(request), trackApplyRequestDto);
    }

    @PutMapping("/")
    public ResponseEntity<?> updateTrack(HttpServletRequest request, @RequestBody TrackUpdateRequestDto trackUpdateRequestDto) {
        return trackService.updateTrack(GET_USER_ID_BY_TOKEN(request), trackUpdateRequestDto);
    }

    @GetMapping("/edit")
    public ResponseEntity<?> getTrackEditInfo(HttpServletRequest request, @RequestParam Long trackId) {
        return trackService.getTrackEditInfo(GET_USER_ID_BY_TOKEN(request), trackId);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getTrackInfo(HttpServletRequest request, @RequestParam Long trackId) {
        return trackService.getTrackInfo(GET_USER_ID_BY_TOKEN(request), trackId);
    }

    @GetMapping("/top")
    public ResponseEntity<?> getTopTrackUsedByMainPage() {
        return trackService.getTopTrackUsedByMainPage();
    }

    @DeleteMapping("/")
    public ResponseEntity<?> removeTrack(HttpServletRequest request, @RequestBody TrackRemoveRequestDto trackRemoveRequestDto) {
        return trackService.removeTrack(GET_USER_ID_BY_TOKEN(request), trackRemoveRequestDto);
    }

    @PostMapping("/star")
    public ResponseEntity<?> setPrimaryTrack(HttpServletRequest request, @RequestParam Long trackId) {
        return trackService.setPrimaryTrack(GET_USER_ID_BY_TOKEN(request), trackId);
    }

    @GetMapping("/deleteInfo")
    public ResponseEntity<?> getTrackDeleteInfo(HttpServletRequest request, @RequestParam Long trackId) {
        return trackService.getTrackDeleteInfo(GET_USER_ID_BY_TOKEN(request), trackId);
    }
}