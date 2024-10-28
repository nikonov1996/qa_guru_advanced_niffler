package guru.qa.niffler.db.dao;

import guru.qa.niffler.db.model.UserEntity;

import java.util.UUID;

public class AuthUserDaoSpring implements AuthUserDao{
    @Override
    public void createUser(UserEntity user) {
    }

    @Override
    public void deleteUserById(UUID userId) {
    }

    @Override
    public UserEntity getUserById(UUID userId) {

        return null;
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return null;
    }
}
