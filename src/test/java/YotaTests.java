import base.BaseTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import models.request.AdditionalParametersRequest;
import models.request.CustomerRequest;
import models.response.GetCustomerByIdResponse;
import models.response.CustomerResponse;
import models.response.PhoneResponse;
import models.response.ReturnResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import service.RetryAnalyzer;
import utils.Generator;

import java.util.List;

@Epic("Тестовое задание Yota")
public class YotaTests extends BaseTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final XmlMapper XML_MAPPER = new XmlMapper();
    private static final Integer ACTIVATION_TIME = 120000;


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
        String addedCustomerId = SUBSCRIBER_ACTIVATION_STEPS.addNewCustomer(
                addCustomerRequest,
                getToken()
        );

        CustomerResponse addedCustomerResponse = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(
                addedCustomerId,
                getToken()
        );
        String pd = addedCustomerResponse.getCustomerReturnResponse().getPd();
        JsonNode jsonNode = OBJECT_MAPPER.readTree(pd);
        String passportSeries = jsonNode.get("passportSeries").asText();
        String passportNumber = jsonNode.get("passportNumber").asText();

        Assert.assertTrue(
                passportSeries.length() == 4 && passportNumber.length() == 6,
                "Паспортные данные клиента не корректны!");

        CustomerResponse expectedCustomerResponse = CustomerResponse.builder()
                .customerReturnResponse(ReturnResponse.builder()
                        .customerId(addedCustomerId)
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
                addedCustomerId,
                getToken()
        );
        Assert.assertEquals(
                addedCustomerWithChangedStatusResponse.getCustomerReturnResponse().getStatus(),
                "ACTIVE",
                "Клиент не активирован!");

        String customerInOldSystemRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<ns3:Envelope xmlns:ns2=\"soap\" xmlns:ns3=\"http://schemas.xmlsoap.org/soap/envelope\">\n"
                + "    <ns2:Header>\n"
                + "        <authToken>" + getToken() + "</authToken>\n"
                + "    </ns2:Header>\n"
                + "    <ns2:Body>\n"
                + "        <phoneNumber>" + addedCustomerResponse.getCustomerReturnResponse().getPhone()
                + "</phoneNumber>\n"
                + "    </ns2:Body>\n"
                + "</ns3:Envelope>";
        Response customerInOldSystemResponse = SUBSCRIBER_ACTIVATION_STEPS.findCustomerByPhoneNumber(
                customerInOldSystemRequest
        );
        GetCustomerByIdResponse getCustomerByIdResponse = XML_MAPPER.readValue(
                customerInOldSystemResponse.asString(),
                GetCustomerByIdResponse.class
        );

        Assert.assertEquals(
                getCustomerByIdResponse.getBodyResponse().getCustomerId(),
                addedCustomerId,
                "Клиент не добавлен в старую систему!"
        );

        login("admin", "password");
        SUBSCRIBER_ACTIVATION_STEPS.changeCustomerStatus(addedCustomerId, getToken(), "NEW", 200);
        CustomerResponse customerResponseWithNewStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(
                addedCustomerId,
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
        String addedCustomerId = SUBSCRIBER_ACTIVATION_STEPS.addNewCustomer(addCustomerRequest, getToken());

        String currentStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(addedCustomerId, getToken())
                .getCustomerReturnResponse()
                .getStatus();

        SUBSCRIBER_ACTIVATION_STEPS.changeCustomerStatus(addedCustomerId, getToken(), "ACTIVE", 200);

        String afterChangeStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(addedCustomerId, getToken())
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
        String addedCustomerId = SUBSCRIBER_ACTIVATION_STEPS.addNewCustomer(addCustomerRequest, getToken());

        String currentStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(addedCustomerId, getToken())
                .getCustomerReturnResponse()
                .getStatus();

        SUBSCRIBER_ACTIVATION_STEPS.changeCustomerStatus(addedCustomerId, getToken(), "ACTIVE", 401);

        String afterChangeStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(addedCustomerId, getToken())
                .getCustomerReturnResponse()
                .getStatus();
        Assert.assertEquals(
                currentStatus,
                afterChangeStatus,
                "А как статус мог измениться под ролью пользователя?!");
    }

}
