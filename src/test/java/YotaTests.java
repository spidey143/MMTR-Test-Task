import base.BaseTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import io.qameta.allure.Epic;
import io.restassured.response.Response;
import models.request.AdditionalParameters;
import models.request.CustomerRequest;
import models.request.GetCustomerByIdRequest;
import models.response.CustomerResponse;
import models.response.PhoneResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.util.RetryAnalyzerCount;
import utils.Generator;

import java.util.List;

@Epic("Тестовое задание Yota")
public class YotaTests extends BaseTest {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final XmlMapper XML_MAPPER = new XmlMapper();
    private static final Integer ACTIVATION_TIME = 120000;


    @Test(testName = "Тест бизнес-сценария активации абонента", description = "Тест активации абонента", priority = 1)
    public void subscriberActivationTest() throws JsonProcessingException, InterruptedException {
        login("user", "password");

        List<PhoneResponse> emptyPhones = SUBSCRIBER_ACTIVATION_STEPS.getEmptyPhones(getToken());
        Assert.assertFalse(emptyPhones.isEmpty(), "Список свободных номеров пуст");

        CustomerRequest customerRequest = CustomerRequest.builder()
                .name(Generator.generateRandomString())
                .phone(emptyPhones.get(Generator.RANDOM.nextInt(emptyPhones.size())).getPhone())
                .additionalParameters(
                        AdditionalParameters.builder().string(Generator.generateRandomString()).build())
                .build();
        String id = SUBSCRIBER_ACTIVATION_STEPS.addNewCustomer(customerRequest, getToken());

        Thread.sleep(ACTIVATION_TIME);

        CustomerResponse customerResponse = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(id, getToken());

        Assert.assertEquals(id, customerResponse.getCustomerReturn().getCustomerId());
        String pd = customerResponse.getCustomerReturn().getPd();
        JsonNode jsonNode = OBJECT_MAPPER.readTree(pd);
        String passportSeries = jsonNode.get("passportSeries").asText();
        String passportNumber = jsonNode.get("passportNumber").asText();

        Assert.assertTrue(passportSeries.length() == 4 && passportNumber.length() == 6);
        Assert.assertEquals(customerResponse.getCustomerReturn().getStatus(), "ACTIVE");


        String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<ns3:Envelope xmlns:ns2=\"soap\" xmlns:ns3=\"http://schemas.xmlsoap.org/soap/envelope\">\n"
                + "    <ns2:Header>\n"
                + "        <authToken>" + getToken() + "</authToken>\n"
                + "    </ns2:Header>\n"
                + "    <ns2:Body>\n"
                + "        <phoneNumber>" + customerRequest.getPhone() + "</phoneNumber>\n"
                + "    </ns2:Body>\n"
                + "</ns3:Envelope>";
        Response xmlResponse = SUBSCRIBER_ACTIVATION_STEPS.findCustomerByPhoneNumber(xmlRequest);
        GetCustomerByIdRequest getCustomerByIdRequest = XML_MAPPER.readValue(
                xmlResponse.asString(),
                GetCustomerByIdRequest.class
        );

        Assert.assertEquals(
                getCustomerByIdRequest.getBody().getCustomerId(),
                customerResponse.getCustomerReturn().getCustomerId(),
                "Клиент не добавлен в старую систему"
        );

        login("admin", "password");
        SUBSCRIBER_ACTIVATION_STEPS.changeCustomerStatus(id, getToken(), "NEW", 200);
        CustomerResponse customerResponseWithNewStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(id, getToken());

        Assert.assertEquals(customerResponseWithNewStatus.getCustomerReturn().getStatus(), "NEW");
    }

    @Test(testName = "Тест на возможность смены статуса под ролью Админа",  priority = 2)
    public void shouldChangeCustomerStatusWithRoleAdminTest(){
        login("admin", "password");
        List<PhoneResponse> emptyPhones = SUBSCRIBER_ACTIVATION_STEPS.getEmptyPhones(getToken());
        Assert.assertFalse(emptyPhones.isEmpty(), "Список свободных номеров пуст");
        CustomerRequest customerRequest = CustomerRequest.builder()
                .name(Generator.generateRandomString())
                .phone(emptyPhones.get(Generator.RANDOM.nextInt(emptyPhones.size())).getPhone())
                .additionalParameters(
                        AdditionalParameters.builder().string(Generator.generateRandomString()).build())
                .build();
        String id = SUBSCRIBER_ACTIVATION_STEPS.addNewCustomer(customerRequest, getToken());
        String currentStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(id, getToken()).getCustomerReturn().getStatus();
        SUBSCRIBER_ACTIVATION_STEPS.changeCustomerStatus(id, getToken(), "ACTIVE", 200);
        String afterChangeStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(id, getToken()).getCustomerReturn().getStatus();
        Assert.assertNotEquals(currentStatus, afterChangeStatus);
    }

    @Test(testName = "Тест на возможность смены статуса под ролью обычного пользователя", priority = 3)
    public void shouldChangeCustomerStatusWithRoleUserTest() {
        login("user", "password");
        List<PhoneResponse> emptyPhones = SUBSCRIBER_ACTIVATION_STEPS.getEmptyPhones(getToken());
        Assert.assertFalse(emptyPhones.isEmpty(), "Список свободных номеров пуст");
        CustomerRequest customerRequest = CustomerRequest.builder()
                .name(Generator.generateRandomString())
                .phone(emptyPhones.get(Generator.RANDOM.nextInt(emptyPhones.size())).getPhone())
                .additionalParameters(
                        AdditionalParameters.builder().string(Generator.generateRandomString()).build())
                .build();
        String id = SUBSCRIBER_ACTIVATION_STEPS.addNewCustomer(customerRequest, getToken());
        String currentStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(id, getToken()).getCustomerReturn().getStatus();
        SUBSCRIBER_ACTIVATION_STEPS.changeCustomerStatus(id, getToken(), "ACTIVE", 401);
        String afterChangeStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(id, getToken()).getCustomerReturn().getStatus();
        Assert.assertEquals(
                currentStatus,
                afterChangeStatus,
                "А как статус мог измениться под ролью пользователя?!");

    }

    @Test(testName = "Тест на возможность смены статуса под ролью Админа",  priority = 2)
    public void shouldChangeCustomerStatusWithRoleAdminWithRandomStatusTest() {
        login("admin", "password");
        List<PhoneResponse> emptyPhones = SUBSCRIBER_ACTIVATION_STEPS.getEmptyPhones(getToken());
        Assert.assertFalse(emptyPhones.isEmpty(), "Список свободных номеров пуст");
        CustomerRequest customerRequest = CustomerRequest.builder()
                .name(Generator.generateRandomString())
                .phone(emptyPhones.get(Generator.RANDOM.nextInt(emptyPhones.size())).getPhone())
                .additionalParameters(
                        AdditionalParameters.builder().string(Generator.generateRandomString()).build())
                .build();
        String id = SUBSCRIBER_ACTIVATION_STEPS.addNewCustomer(customerRequest, getToken());
        String currentStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(id, getToken()).getCustomerReturn().getStatus();
        SUBSCRIBER_ACTIVATION_STEPS.changeCustomerStatus(id, getToken(), Generator.generateRandomString(), 200);
        String afterChangeStatus = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(id, getToken()).getCustomerReturn().getStatus();
        Assert.assertNotEquals(currentStatus, afterChangeStatus);
    }
}
