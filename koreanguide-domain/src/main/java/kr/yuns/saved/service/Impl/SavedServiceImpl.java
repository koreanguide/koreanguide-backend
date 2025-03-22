package kr.yuns.saved.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import kr.yuns.auth.data.dao.UserDao;
import kr.yuns.auth.data.entity.User;
import kr.yuns.saved.data.dao.SavedDao;
import kr.yuns.saved.data.dto.request.SavedRequestDto;
import kr.yuns.saved.data.dto.response.SavedResponseDto;
import kr.yuns.saved.data.entity.Saved;
import kr.yuns.saved.service.SavedService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SavedServiceImpl implements SavedService {
    private final SavedDao savedDao;
    private final UserDao userDao;

    @Override
    public ResponseEntity<?> saveItem(Long userId, SavedRequestDto savedRequestDto) {
        User user = userDao.getUserEntity(userId);

        if(!savedDao.checkAddItemAvaliable(user)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }

        savedDao.saveSavedEntity(Saved.builder()
                        .category(savedRequestDto.getCategory())
                        .address(savedRequestDto.getAddress())
                        .value(savedRequestDto.getValue())
                        .user(user)
                        .useAble(true)
                .build());

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public int getSavedCount(Long userId) {
        User user = userDao.getUserEntity(userId);
        List<Saved> savedList = savedDao.getAllSavedEntity(user);

        return savedList.size();
    }

    @Override
    public ResponseEntity<?> removeSavedItem(Long userId, Long itemId) {
        User user = userDao.getUserEntity(userId);
        Saved saved = savedDao.getSavedEntity(itemId);

        if(!saved.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        saved.setUseAble(false);

        savedDao.saveSavedEntity(saved);

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<?> resetSavedItem(Long userId) {
        User user = userDao.getUserEntity(userId);
        List<Saved> savedList = savedDao.getAllSavedEntity(user);

        for(Saved saved : savedList) {
            saved.setUseAble(false);
            savedDao.saveSavedEntity(saved);
        }

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Override
    public ResponseEntity<List<SavedResponseDto>> getSavedItem(Long userId) {
        User user = userDao.getUserEntity(userId);
        List<Saved> savedList = savedDao.getAllSavedEntity(user);
        List<SavedResponseDto> savedResponseDtoList = new ArrayList<>();

        for(Saved saved : savedList) {
            savedResponseDtoList.add(SavedResponseDto.builder()
                            .id(saved.getId())
                            .address(saved.getAddress())
                            .value(saved.getValue())
                            .category(saved.getCategory())
                    .build());
        }

        return ResponseEntity.status(HttpStatus.OK).body(savedResponseDtoList);
    }
}