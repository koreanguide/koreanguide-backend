package kr.yuns.saved.data.dao;

import java.util.List;

import kr.yuns.auth.data.entity.User;
import kr.yuns.saved.data.entity.Saved;
import kr.yuns.saved.data.exception.SavedEntityNotFoundException;

public interface SavedDao {
    Saved getSavedEntity(Long savedId) throws SavedEntityNotFoundException;
    List<Saved> getAllSavedEntity(User user);
    boolean checkAddItemAvaliable(User user);
    void saveSavedEntity(Saved saved);
}