package guru.qa.niffler.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static guru.qa.niffler.jupiter.User.UserType.WITH_FRIENDS;

public class FriendsUsersWebTests extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = WITH_FRIENDS) UserJson userJson) {
        Allure.step("Open base url",()->{
            Selenide.open("http://127.0.0.1:3000/");
        });
        Allure.step("Login",()->{
            $("input[name='username']").setValue(userJson.username());
            $("input[name='password']").setValue(userJson.password());
            $(".form__submit").click();
        });

    }

    @Test
    void checkThatUserHasFriend(@User(userType = WITH_FRIENDS) UserJson userJson){
        $("button[aria-label='Menu']").click();
        $(".MuiMenu-list")
                .$$("li").find(text("Friends")).click();
        var friend = $$x("//tr[contains(@class,'MuiTableRow')]").first();
        Assertions.assertTrue(
                friend.getText().contains(
                                (userJson.username()
                                        .equalsIgnoreCase("pupa"))
                                        ?"lupa"
                                        :"pupa")
        );
    }
}
