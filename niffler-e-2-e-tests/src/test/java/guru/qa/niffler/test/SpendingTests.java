package guru.qa.niffler.test;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.Spend;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.chrome.ChromeOptions;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

public class SpendingTests {

    static {
//        System.setProperty("webdriver.chrome.driver", "/Users/qanva/Desktop/driver/chromedriver");
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox", "--ignore-certificate-errors");
        Configuration.webdriverLogsEnabled = false;
        Configuration.browser = "chrome";
        Configuration.headless = false;
        Configuration.browserSize = "1920x1080";
        Configuration.pageLoadStrategy = "eager";
    }

    @BeforeEach
    void doLogin() {
        Selenide.open("http://127.0.0.1:3000/");
        $("input[name='username']").setValue("qanva");
        $("input[name='password']").setValue("123");
        $(".form__submit").click();
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
        $(".MuiTableBody-root")
                .$$("tr")
                .find(text(spendJson.description()))
                .$$("td")
                .first()
                .click();

        $("#delete").click();
        $(".MuiDialog-paperScrollPaper")
                .shouldBe(Condition.visible)
                .$(byText("Delete")).click();
        $(".MuiTableBody-root")
                .$$("tr").shouldHave(CollectionCondition.size(0));
    }
}
