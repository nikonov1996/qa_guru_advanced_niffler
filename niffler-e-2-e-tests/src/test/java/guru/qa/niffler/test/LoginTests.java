package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.UserDataDao;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.Dao;
import guru.qa.niffler.jupiter.DaoExtension;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.UUID;

import static com.codeborne.selenide.Selenide.$;

@ExtendWith(DaoExtension.class)
public class LoginTests extends BaseWebTest{

    @Dao
    private AuthUserDao authUser;
    @Dao
    private UserDataDao userData;
    private UserEntity user;
    private UserDataEntity userDataEntity;

    @BeforeEach
    void createUser(){
        user = UserEntity.builder()
                .username("salo6")
                .password("123")
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .authorities(
                        Arrays.stream(Authority.values())
                                .map(authority -> AuthorityEntity.builder()
                                       .authority(authority)
                                       .build()).toList())
                .build();
        authUser.createUser(user);
        //TODO нужно создавать данные в user-data только если данные создались удачно в user-auth
        // если при содании записи в user-data ошибка, то откатывать изменения в user-auth
        userDataEntity = UserDataEntity.builder()
                .username(user.getUsername()).build();
        userData.createUserData(userDataEntity);

    }

    @AfterEach
    void deleteUser(){
        authUser.deleteUserById(user.getId());
        userData.deleteUserDataById(userDataEntity.getId());
    }

    @Test
    void checkLoginSuccess(){
//        Allure.step("Open base url", () -> {
//            Selenide.open("http://127.0.0.1:3000/");
//        });
//        Allure.step("Login", () -> {
//            $("input[name='username']").setValue("");
//            $("input[name='password']").setValue("");
//            $(".form__submit").click();
//        });
    }
}
