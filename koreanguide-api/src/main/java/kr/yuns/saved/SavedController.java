package kr.yuns.saved;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import kr.yuns.JwtTokenProvider;
import kr.yuns.auth.data.dao.UserDao;
import kr.yuns.saved.data.dto.request.SavedRequestDto;
import kr.yuns.saved.data.dto.response.SavedResponseDto;
import kr.yuns.saved.service.SavedService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/saved")
@RequiredArgsConstructor
public class SavedController {
    private final SavedService savedService;
    private final JwtTokenProvider tokenProvider;
    private final UserDao userDao;

    public Long GET_USER_ID_BY_TOKEN(HttpServletRequest request) {
        return userDao.getUserId(tokenProvider.getUserEmail(request));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addItem(HttpServletRequest request, @RequestBody SavedRequestDto savedRequestDto) {
        return savedService.saveItem(GET_USER_ID_BY_TOKEN(request), savedRequestDto);
    }

    @GetMapping("/count")
    public int getSavedCount(HttpServletRequest request) {
        return savedService.getSavedCount(GET_USER_ID_BY_TOKEN(request));
    }

    @DeleteMapping("/")
    public ResponseEntity<?> removeSavedItem(HttpServletRequest request, @RequestParam Long itemId) {
        return savedService.removeSavedItem(GET_USER_ID_BY_TOKEN(request), itemId);
    }

    @DeleteMapping("/reset")
    public ResponseEntity<?> resetSavedItem(HttpServletRequest request) {
        return savedService.resetSavedItem(GET_USER_ID_BY_TOKEN(request));
    }

    @GetMapping("/")
    public ResponseEntity<List<SavedResponseDto>> getSavedItem(HttpServletRequest request) {
        return savedService.getSavedItem(GET_USER_ID_BY_TOKEN(request));
    }
}