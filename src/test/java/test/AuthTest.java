package test;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.chrome.ChromeOptions;
import page.LoginPage;
import page.VerificationPage;
import data.*;
import static data.DataHelper.*;
import static data.SQLHelper.*;

import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static com.codeborne.selenide.Selenide.back;
import static com.codeborne.selenide.Selenide.open;

public class AuthTest {
    LoginPage loginPage;
    private final String incorrectAuthMsg = "Неверно указан логин или пароль";
    private final String incorrectCodeMsg = "Неверно указан код! Попробуйте ещё раз";
    private final String limitMsg = "Ошибка!";

    @BeforeEach
    public void setup() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        Configuration.browserCapabilities = options;
        loginPage = open("http://localhost:9999", LoginPage.class);
    }
    @AfterEach
    public void after() {
        cleanVerificationTable();
    }
    @AfterAll
    public static void tearDownAll() {
        cleanDB();
    }

    @Test
    public void shouldLogin() {
        AuthInfo user = getValidAuthInfo();
        VerificationPage verificationPage = loginPage.validLogin(user);
        String verificationCode = getVerificationCode();
        verificationPage.validVerify(verificationCode);
    }
    @Test
    public void shouldGetErrorMsgInvalidLogin() {
        loginPage.invalidLogin(generateRandomAuthInfo(), incorrectAuthMsg);
    }
    @Test
    public void shouldGetErrorInvalidCode() {
        AuthInfo user = getValidAuthInfo();
        VerificationPage verificationPage = loginPage.validLogin(user);
        String verificationCode = getRandomVerificationCode();
        verificationPage.invalidVerify(verificationCode, incorrectCodeMsg);
    }
    @Test
    public void shouldBlockedAuthorize() {
        AuthInfo user = getValidAuthInfo();
        VerificationPage verificationPage = loginPage.validLogin(user);
        back();
        loginPage.validLogin(user);
        back();
        loginPage.validLogin(user);
        back();
        loginPage.validLogin(user);
        String verificationCode = getVerificationCode();
        verificationPage.overLimitVerify(verificationCode, limitMsg);
    }
}
