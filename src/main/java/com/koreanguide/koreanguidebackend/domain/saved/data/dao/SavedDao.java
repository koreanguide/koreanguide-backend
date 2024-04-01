package com.koreanguide.koreanguidebackend.domain.saved.data.dao;

import com.koreanguide.koreanguidebackend.domain.auth.data.entity.User;
import com.koreanguide.koreanguidebackend.domain.saved.data.entity.Saved;

import java.util.List;

public interface SavedDao {
    Saved getSavedEntity(Long savedId);
    List<Saved> getAllSavedEntity(User user);
    boolean checkAddItemAvaliable(User user);
    void saveSavedEntity(Saved saved);
}
