package guru.qa.niffler.test;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.jupiter.annotation.User.UserType.*;

public class SpendingTests extends BaseWebTest {


    @BeforeEach
    void doLogin(@User(userType = WITHOUT_FRIENDS) UserJson userJson) {
        Allure.step("Open base url", () -> {
            Selenide.open("http://127.0.0.1:3000/");
        });
        Allure.step("Login", () -> {
            $("input[name='username']").setValue(userJson.username());
            $("input[name='password']").setValue(userJson.password());
            $(".form__submit").click();
        });
    }

    @Spend(
            username = "qanva",
            description = "Test",
            category = "TestCategory",
            amount = 10000.00,
            currency = CurrencyValues.RUB
    )
    @Test
    void deletedSpendingAfterDeleteActions(SpendJson spendJson) {
        Allure.step("Click to delete spend", () -> {
            $(".MuiTableBody-root")
                    .$$("tr")
                    .find(text(spendJson.description()))
                    .$$("td")
                    .first()
                    .click();
            $("#delete").click();
        });
        Allure.step("Approve deleting in modal window", () -> {
            $(".MuiDialog-paperScrollPaper")
                    .shouldBe(Condition.visible)
                    .$(byText("Delete")).click();
        });
        Allure.step("Check that spend deleted", () -> {
            $(".MuiTableBody-root")
                    .$$("tr")
                    .shouldHave(CollectionCondition.size(0));
        });
    }
}
