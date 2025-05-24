package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.dao.jdbc.UserDataDaoJDBC;
import guru.qa.niffler.db.dao.jpa.UserDataDaoHibernate;
import guru.qa.niffler.db.dao.spring.UserDataDaoSpring;
import guru.qa.niffler.db.model.jpa.UserDataEntity;

public interface UserDataDao {
    static UserDataDao getInstance() {
        String dbImpl = "hibernate";//System.getProperty("db.impl");
        return switch (dbImpl) {
            case "spring" -> new UserDataDaoSpring();
            case "hibernate" -> new UserDataDaoHibernate();
            default -> new UserDataDaoJDBC();
        };
    }

    void createUserData(UserDataEntity user);

    void deleteUser(UserDataEntity user);

    UserDataEntity getUserDataByUsername(UserDataEntity user);
}
