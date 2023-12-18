import base.BaseTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Epic;
import models.requestModels.AdditionalParameters;
import models.requestModels.CustomerRequest;
import models.responseModels.CustomerResponse;
import models.responseModels.PhoneResponse;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.log4testng.Logger;
import service.RetryAnalyzer;
import utils.Generator;

import java.util.List;

import static enums.Role.ADMIN;
import static enums.Role.USER;

@Epic("Тестовое задание Yota")
public class YotaTests extends BaseTest {

  private static final Logger LOGGER = Logger.getLogger(YotaTests.class);


  @Test(testName = "Тест активации абонента", description = "Тест активации абонента")
  public void subscriberActivationTest() throws InterruptedException, JsonProcessingException {
    login(USER, "user", "password");
    List<PhoneResponse> emptyPhones = SUBSCRIBER_ACTIVATION_STEPS.getEmptyPhones(getToken());
    Assert.assertFalse(emptyPhones.isEmpty(), "Список свободных номеров пуст");
    emptyPhones.forEach(
        p -> System.out.println("phone: " + p.getPhone() + " locale: " + p.getLocale()));
    CustomerRequest customerRequest = CustomerRequest.builder()
        .name(Generator.generateRandomString())
        .phone(emptyPhones.get(Generator.RANDOM.nextInt(emptyPhones.size())).getPhone())
        .additionalParameters(
            AdditionalParameters.builder().string(Generator.generateRandomString()).build())
        .build();
    String id = SUBSCRIBER_ACTIVATION_STEPS.addNewCustomer(customerRequest, getToken());
    Thread.sleep(120000);
    CustomerResponse customerResponse = SUBSCRIBER_ACTIVATION_STEPS.getCustomerById(id, getToken());
    Assert.assertEquals(id, customerResponse.getMyreturn().getCustomerId());
    String pd = customerResponse.getMyreturn().getPd();
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode jsonNode = objectMapper.readTree(pd);
    String passportSeries = jsonNode.get("passportSeries").asText();
    String passportNumber = jsonNode.get("passportNumber").asText();
    System.out.println(passportSeries + " " + passportNumber);
    Assert.assertEquals(customerResponse.getMyreturn().getStatus(), "ACTIVE");
    String xmlRequest = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
        + "<ns3:Envelope xmlns:ns2=\"soap\" xmlns:ns3=\"http://schemas.xmlsoap.org/soap/envelope\">\n"
        + "    <ns2:Header>\n"
        + "        <authToken>${getToken()}</authToken>\n"
        + "    </ns2:Header>\n"
        + "    <ns2:Body>\n"
        + "        <phoneNumber>${customerRequest.getPhone()}</phoneNumber>\n"
        + "    </ns2:Body>\n"
        + "</ns3:Envelope>";
    String xmlResponse = SUBSCRIBER_ACTIVATION_STEPS.getCustomerByPhone(xmlRequest);
    System.out.println(xmlResponse);
  }

  @Test(testName = "Тест создания нового клиента", description = "Тест смены статуса")
  public void addNewCustomerTest() {
    BaseTest base = new BaseTest();
    base.login(ADMIN, "admin", "password");

  }
}
