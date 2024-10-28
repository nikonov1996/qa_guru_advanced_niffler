package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.dao.jdbc.AuthUserDaoJDBC;
import guru.qa.niffler.db.dao.jdbc.UserDataDaoJDBC;
import guru.qa.niffler.db.dao.spring.UserDataDaoSpring;
import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;

import java.util.UUID;

public interface UserDataDao {
    static UserDataDao getInstance() {
        String dbImpl = "";//System.getProperty("db.impl");
        return switch (dbImpl) {
            case "spring" -> new UserDataDaoSpring();
            default -> new UserDataDaoJDBC();
        };
    }

    void createUserData(UserDataEntity user);

    void deleteUserDataById(UUID userId);
}
