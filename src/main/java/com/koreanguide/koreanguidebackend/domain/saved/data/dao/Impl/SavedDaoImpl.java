package com.koreanguide.koreanguidebackend.domain.saved.data.dao.Impl;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.saved.data.dao.SavedDao;
import com.koreanguide.koreanguidebackend.domain.saved.data.entity.Saved;
import com.koreanguide.koreanguidebackend.domain.saved.data.repository.SavedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SavedDaoImpl implements SavedDao {
    private final SavedRepository savedRepository;

    @Override
    public Saved getSavedEntity(Long savedId) {
        return savedRepository.getById(savedId);
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
