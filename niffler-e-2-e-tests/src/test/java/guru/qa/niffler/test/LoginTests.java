package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.dao.AuthUserDao;
import guru.qa.niffler.db.dao.UserDataDao;
import guru.qa.niffler.db.model.Authority;
import guru.qa.niffler.db.model.AuthorityEntity;
import guru.qa.niffler.db.model.UserDataEntity;
import guru.qa.niffler.db.model.UserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.jupiter.annotation.Dao;
import guru.qa.niffler.jupiter.annotation.Entity;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.DaoExtension;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Random;

import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.*;


public class LoginTests extends BaseWebTest {

    @DBUser(
            username = "babaca",
            password = "123",
            enabled = true,
            accountNonLocked = true,
            accountNonExpired = true,
            credentialsNonExpired = true
    )
    @Test
    void checkLoginSuccess(@Entity UserEntity user) {
        step("Open base url", () -> {
            Selenide.open("http://127.0.0.1:3000/");
        });
        step("Login", () -> {
            $("input[name='username']").setValue("");
            $("input[name='password']").setValue("");
            $(".form__submit").click();
        });
    }
}
