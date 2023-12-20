import base.BaseTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import models.request.*;
import models.response.*;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;
import service.RetryAnalyzer;
import utils.Generator;

import java.util.List;

@Epic("Тестовое задание Yota")
public class YotaTests extends BaseTest {
    private static final XmlMapper XML_MAPPER = new XmlMapper();
    private static final Integer ACTIVATION_TIME = 120000;
    //public static final Logger LOGGER = Logger.getLogger(YotaTests.class);


    @Test(testName = "Тест бизнес-сценария активации абонента", description = "бизнес-сценария активации абонента",
            retryAnalyzer = RetryAnalyzer.class)
    public void subscriberActivationTest() throws JsonProcessingException, InterruptedException {
        login("user", "password");

        List<PhoneResponse> emptyPhones = SUBSCRIBER_ACTIVATION_STEPS.getEmptyPhones(getToken());

        Assert.assertFalse(
                emptyPhones.isEmpty(),
                "Список свободных номеров пуст"
        );

        CustomerRequest addCustomerRequest = CustomerRequest.builder()
                .name(Generator.generateRandomString())
                .phone(emptyPhones.get(Generator.RANDOM.nextInt(emptyPhones.size())).getPhone())
                .additionalParameters(
                        AdditionalParametersRequest.builder()
                                .string(Generator.generateRandomString())
                                .build())
                .build();
        CustomerIdResponse customerIdResponse = SUBSCRIBER_ACTIVATION_STEPS.addNewCustomer(
                addCustomerRequest,
                getToken()
        );

        CustomerResponse addedCustomerResponse = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(
                customerIdResponse.getId(),
                getToken()
        );

        PassportDetailsResponse passportDetailsResponse = SUBSCRIBER_ACTIVATION_STEPS.getCustomerResponsePd(
                addedCustomerResponse);

        Assert.assertTrue(
                passportDetailsResponse.getPassportSeries().length() == 4
                        && passportDetailsResponse.getPassportNumber().length() == 6,
                "Паспортные данные клиента не корректны!");

        CustomerResponse expectedCustomerResponse = CustomerResponse.builder()
                .customerReturnResponse(ReturnResponse.builder()
                        .customerId(customerIdResponse.getId())
                        .phone(addCustomerRequest.getPhone())
                        .name(addCustomerRequest.getName())
                        .status("NEW")
                        .additionalParameters(AdditionalParametersRequest.builder()
                                .string(addCustomerRequest.getAdditionalParameters().getString())
                                .build())
                        .build())
                .build();

        Assert.assertTrue(
                addedCustomerResponse.getCustomerReturnResponse()
                        .equals(expectedCustomerResponse.getCustomerReturnResponse())
        );

        Thread.sleep(ACTIVATION_TIME);

        CustomerResponse addedCustomerWithChangedStatusResponse = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(
                customerIdResponse.getId(),
                getToken()
        );

        Assert.assertEquals(
                addedCustomerWithChangedStatusResponse.getCustomerReturnResponse().getStatus(),
                "ACTIVE",
                "Клиент не активирован!");

        CustomerInOldSystemRequest customerInOldSystemRequest = new CustomerInOldSystemRequest(
                new HeaderRequest(getToken()),
                new BodyRequest(addedCustomerResponse.getCustomerReturnResponse().getPhone()));

        GetCustomerByIdResponse getCustomerByIdResponse = XML_MAPPER.readValue(
                SUBSCRIBER_ACTIVATION_STEPS.findCustomerByPhoneNumber(
                        customerInOldSystemRequest
                ).asString(),
                GetCustomerByIdResponse.class
        );

        Assert.assertEquals(
                getCustomerByIdResponse.getBodyResponse().getCustomerId(),
                customerIdResponse.getId(),
                "Клиент не добавлен в старую систему!"
        );

        login("admin", "password");
        SUBSCRIBER_ACTIVATION_STEPS.changeCustomerStatus(customerIdResponse.getId(), getToken(), "NEW", 200);
        CustomerResponse customerResponseWithNewStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(
                customerIdResponse.getId(),
                getToken()
        );

        Assert.assertEquals(customerResponseWithNewStatus.getCustomerReturnResponse().getStatus(), "NEW");
    }

    @Test(testName = "Тест на возможность смены статуса под ролью Администратора",
            description = "Тест на возможность смены статуса под ролью Админа",
            retryAnalyzer = RetryAnalyzer.class)
    public void shouldChangeCustomerStatusWithRoleAdminTest() {
        login("admin", "password");

        List<PhoneResponse> emptyPhones = SUBSCRIBER_ACTIVATION_STEPS.getEmptyPhones(getToken());

        Assert.assertFalse(
                emptyPhones.isEmpty(),
                "Список свободных номеров пуст"
        );

        CustomerRequest addCustomerRequest = CustomerRequest.builder()
                .name(Generator.generateRandomString())
                .phone(emptyPhones.get(Generator.RANDOM.nextInt(emptyPhones.size())).getPhone())
                .additionalParameters(
                        AdditionalParametersRequest.builder()
                                .string(Generator.generateRandomString())
                                .build())
                .build();
        CustomerIdResponse customerIdResponse = SUBSCRIBER_ACTIVATION_STEPS.addNewCustomer(
                addCustomerRequest,
                getToken()
        );

        String currentStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(customerIdResponse.getId(), getToken())
                .getCustomerReturnResponse()
                .getStatus();

        SUBSCRIBER_ACTIVATION_STEPS.changeCustomerStatus(
                customerIdResponse.getId(),
                getToken(), "ACTIVE",
                200);

        String afterChangeStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(
                        customerIdResponse.getId(), getToken())
                .getCustomerReturnResponse()
                .getStatus();
        Assert.assertNotEquals(currentStatus, afterChangeStatus);
    }

    @Test(testName = "Тест на возможность смены статуса под ролью обычного пользователя",
            description = "Тест на возможность смены статуса под ролью обычного пользователя",
            retryAnalyzer = RetryAnalyzer.class)
    public void shouldChangeCustomerStatusWithRoleUserTest() {
        login("user", "password");

        List<PhoneResponse> emptyPhones = SUBSCRIBER_ACTIVATION_STEPS.getEmptyPhones(getToken());

        Assert.assertFalse(
                emptyPhones.isEmpty(),
                "Список свободных номеров пуст"
        );

        CustomerRequest addCustomerRequest = CustomerRequest.builder()
                .name(Generator.generateRandomString())
                .phone(emptyPhones.get(Generator.RANDOM.nextInt(emptyPhones.size())).getPhone())
                .additionalParameters(
                        AdditionalParametersRequest.builder()
                                .string(Generator.generateRandomString())
                                .build())
                .build();
        CustomerIdResponse customerIdResponse = SUBSCRIBER_ACTIVATION_STEPS.addNewCustomer(
                addCustomerRequest, getToken());

        String currentStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(customerIdResponse.getId(), getToken())
                .getCustomerReturnResponse()
                .getStatus();

        SUBSCRIBER_ACTIVATION_STEPS.changeCustomerStatus(
                customerIdResponse.getId(),
                getToken(), "ACTIVE", 401);

        String afterChangeStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(
                        customerIdResponse.getId(), getToken())
                .getCustomerReturnResponse()
                .getStatus();
        Assert.assertEquals(
                currentStatus,
                afterChangeStatus,
                "А как статус мог измениться под ролью пользователя?!");
    }

}
