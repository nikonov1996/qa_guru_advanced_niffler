package guru.qa.niffler.test;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.db.model.jpa.UserEntity;
import guru.qa.niffler.jupiter.annotation.DBUser;
import guru.qa.niffler.jupiter.annotation.Entity;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Selenide.$;
import static io.qameta.allure.Allure.step;


public class LoginTests extends BaseWebTest {

    @DBUser(
            username = "babaca5",
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
