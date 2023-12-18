package steps;

import io.qameta.allure.Step;
import models.requestModels.CustomerRequest;
import models.responseModels.CustomerResponse;
import models.responseModels.PhoneResponse;

import java.util.List;
import org.testng.xml.XmlClass;

import static io.restassured.RestAssured.given;
import static service.Specification.requestSpecification;
import static service.Specification.responseSpecification;

public class SubscriberActivationSteps {

  @Step("Получить спико свободных номеров телефона")
  public List<PhoneResponse> getEmptyPhones(String token) {
    return given().spec(requestSpecification())
        .when()
        .headers("authToken", token)
        .get("/simcards/getEmptyPhone")
        .then().spec(responseSpecification(200)).log().all()
        .extract().response().body().jsonPath().getList("phones", PhoneResponse.class);
  }

  @Step("Создать нового кастомера")
  public String addNewCustomer(CustomerRequest customer, String token) {
    return given().spec(requestSpecification())
        .when()
        .headers("authToken", token)
        .body(customer)
        .post("/customer/postCustomer")
        .then().spec(responseSpecification(200)).log().all()
        .extract().response().body().jsonPath().getString("id");
  }

  @Step("Получить кастомера по id")
  public CustomerResponse getCustomerById(String id, String token) {
    return given().spec(requestSpecification())
        .when()
        .headers("authToken", token)
        .param("customerId", id)
        .get("/customer/getCustomerById")
        .then().spec(responseSpecification(200)).log().all()
        .extract().response().body().as(CustomerResponse.class);
  }

  @Step("Получить кастомера по номеру телефона в старой системе")
  public String getCustomerByPhone(String xmlClass) {
    return given().spec(requestSpecification())
        .when()
        .body(xmlClass)
        .headers("Content-Type","application/xml")
        .post("/customer/findByPhoneNumber")
        .then().spec(responseSpecification(200)).log().all().toString();
  }
}
