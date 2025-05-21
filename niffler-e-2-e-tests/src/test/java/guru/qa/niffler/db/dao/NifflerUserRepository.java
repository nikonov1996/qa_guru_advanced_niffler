package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.jpa.UserDataEntity;
import guru.qa.niffler.db.model.jpa.UserEntity;
import guru.qa.niffler.model.CurrencyValues;

public class NifflerUserRepository {

    private static AuthUserDao authUserDao = AuthUserDao.getInstance();
    private static UserDataDao userDataDao = UserDataDao.getInstance();

    public void createUser(UserEntity user){
        authUserDao.createUser(user);
        userDataDao.createUserData(fromAuthUser(user));
    }

    public void removeUser(UserEntity user){
        authUserDao.deleteUser(user);
        userDataDao.getUserDataByUsername(fromAuthUser(user));
    }


    private UserDataEntity fromAuthUser (UserEntity user){
        UserDataEntity userData = new UserDataEntity();
        userData.setUsername(user.getUsername());
        userData.setCurrency(CurrencyValues.RUB);
        return userData;
    }
}
