import base.BaseTest;
import io.qameta.allure.Epic;
import models.responseModels.PhoneResponse;
import org.testng.annotations.Test;

import java.util.List;

import static enums.Role.USER;

@Epic("Тестовое задание Yota")

public class YotaTests extends BaseTest {

    @Test(testName = "Тест активации абонента", description = "Тест активации абонента")
    public void subscriberActivationTest(){
        BaseTest base = new BaseTest();
        base.login(USER, "user","password");
        List<PhoneResponse> emptyPhones = SUBSCRIBER_ACTIVATION_STEPS.getEmptyPhones(base.getToken());
    }

    @Test(testName = "Тест смены статуса", description = "Тест смены статуса")
    public void changeStatusTest(){

    }
}
