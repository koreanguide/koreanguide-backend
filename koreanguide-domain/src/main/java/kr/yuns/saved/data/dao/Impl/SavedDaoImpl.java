package kr.yuns.saved.data.dao.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import kr.yuns.auth.data.entity.User;
import kr.yuns.saved.data.dao.SavedDao;
import kr.yuns.saved.data.entity.Saved;
import kr.yuns.saved.data.exception.SavedEntityNotFoundException;
import kr.yuns.saved.data.repository.SavedRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SavedDaoImpl implements SavedDao {
    private final SavedRepository savedRepository;

    @Override
    public Saved getSavedEntity(Long savedId) throws SavedEntityNotFoundException {
        Optional<Saved> foundEntity = savedRepository.findById(savedId);

        if(foundEntity.isEmpty()) {
            throw new SavedEntityNotFoundException();
        } else {
            return foundEntity.get();
        }
    }

    @Override
    public List<Saved> getAllSavedEntity(User user) {
        List<Saved> savedList = savedRepository.getAllByUser(user);
        List<Saved> useAbleSavedList = new ArrayList<>();

        for(Saved saved : savedList) {
            if(saved.isUseAble()) {
                useAbleSavedList.add(saved);
            }
        }

        return useAbleSavedList;
    }

    @Override
    public boolean checkAddItemAvaliable(User user) {
        List<Saved> savedList = savedRepository.getAllByUser(user);
        List<Saved> useAbleSavedList = new ArrayList<>();

        for(Saved saved : savedList) {
            if(saved.isUseAble()) {
                useAbleSavedList.add(saved);
            }
        }

        return useAbleSavedList.size() < 3;
    }

    @Override
    public void saveSavedEntity(Saved saved) {
        savedRepository.save(saved);
    }
}