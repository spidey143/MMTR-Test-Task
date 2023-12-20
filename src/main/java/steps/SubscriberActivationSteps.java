package steps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.request.ChangeCustomerStatusRequest;
import models.request.CustomerInOldSystemRequest;
import models.request.CustomerRequest;
import models.response.CustomerIdResponse;
import models.response.CustomerResponse;
import models.response.PassportDetailsResponse;
import models.response.PhoneResponse;

import java.util.List;

import static io.restassured.RestAssured.given;
import static service.Specification.requestSpecification;
import static service.Specification.responseSpecification;

public class SubscriberActivationSteps {

    @Step("Получить спикок свободных номеров телефона")
    public List<PhoneResponse> getEmptyPhones(String token) {
        return given().spec(requestSpecification())
                .when().log().all()
                .headers("authToken", token)
                .get("/simcards/getEmptyPhone")
                .then().spec(responseSpecification(200)).log().all()
                .extract().response().body().jsonPath().getList("phones", PhoneResponse.class);
    }

    @Step("Создать нового клиента")
    public CustomerIdResponse addNewCustomer(CustomerRequest customer, String token) {
        return given().spec(requestSpecification())
                .when().log().all()
                .headers("authToken", token)
                .body(customer)
                .post("/customer/postCustomer")
                .then().spec(responseSpecification(200)).log().all()
                .extract().response().body().as(CustomerIdResponse.class);
    }

    @Step("Получить клиента по id")
    public CustomerResponse getCustomerById(String customerId, String token) {
        return given().spec(requestSpecification())
                .when().log().all()
                .headers("authToken", token)
                .param("customerId", customerId)
                .get("/customer/getCustomerById")
                .then().spec(responseSpecification(200)).log().all()
                .extract().response().body().as(CustomerResponse.class);
    }

    @Step("Получить клиента по номеру телефона в старой системе")
    public Response findCustomerByPhoneNumber(CustomerInOldSystemRequest customerInOldSystemRequest) {
        return given().contentType(ContentType.XML)
                .when().log().all()
                .body(customerInOldSystemRequest)
                .headers("Content-Type", "application/xml")
                .post("/customer/findByPhoneNumber")
                .then().statusCode(200).log().all()
                .extract().response();
    }

    @Step("Сменить статус клиента")
    public void changeCustomerStatus(String customerId, String token, String status, Integer code) {
        given().spec(requestSpecification())
                .when().log().all()
                .headers("authToken", token)
                .body(ChangeCustomerStatusRequest.builder().status(status).build())
                .post("/customer/" + customerId + "/changeCustomerStatus")
                .then().statusCode(code).log().all();
    }

    @Step("Получить паспортные данных")
    public PassportDetailsResponse getCustomerResponsePd(CustomerResponse customerResponse) throws JsonProcessingException {
        ObjectMapper OBJECT_MAPPER = new ObjectMapper();
        String pd = customerResponse.getCustomerReturnResponse().getPd();
        JsonNode jsonNode = OBJECT_MAPPER.readTree(pd);
        String passportSeries = jsonNode.get("passportSeries").asText();
        String passportNumber = jsonNode.get("passportNumber").asText();
        return new PassportDetailsResponse(passportNumber,passportSeries);
    }
}
