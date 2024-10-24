package guru.qa.niffler.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.User;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.exist;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static guru.qa.niffler.jupiter.User.UserType.INVITATION_RECEIVED;
import static guru.qa.niffler.jupiter.User.UserType.WITH_FRIENDS;

public class InvitedUsersWebTests extends BaseWebTest {

    @BeforeEach
    void doLogin(@User(userType = INVITATION_RECEIVED) UserJson userJson) {
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
    void checkThatUserHasInvite1(@User(userType = INVITATION_RECEIVED) UserJson userJson){
        $("button[aria-label='Menu']").click();
        $(".MuiMenu-list")
                .$$("li").find(text("Friends")).click();
        $$x("//tr[contains(@class,'MuiTableRow')]")
                .first()
                .$(byText("Accept"))
                .shouldBe(exist);
    }

    @Test
    void checkThatUserHasInvite2(@User(userType = INVITATION_RECEIVED) UserJson userJson){
        $("button[aria-label='Menu']").click();
        $(".MuiMenu-list")
                .$$("li").find(text("Friends")).click();
        $$x("//tr[contains(@class,'MuiTableRow')]")
                .first()
                .$(byText("Decline"))
                .shouldBe(exist);
    }
}
