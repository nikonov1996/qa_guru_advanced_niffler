package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;

import java.util.UUID;

public interface UserDataDao {

    void createUserData(UserDataEntity user);

    void deleteUserDataById(UUID userId);
}
