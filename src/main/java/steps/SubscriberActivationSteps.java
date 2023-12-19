package steps;

import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import models.request.ChangeCustomerStatusResponse;
import models.request.CustomerRequest;
import models.response.CustomerResponse;
import models.response.PhoneResponse;

import java.util.List;

import static io.restassured.RestAssured.given;
import static service.Specification.requestSpecification;
import static service.Specification.responseSpecification;

public class SubscriberActivationSteps {

    @Step("Получить спикок свободных номеров телефона")
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
    public CustomerResponse getCustomerById(String customerId, String token) {
        return given().spec(requestSpecification())
                .when()
                .headers("authToken", token)
                .param("customerId", customerId)
                .get("/customer/getCustomerById")
                .then().spec(responseSpecification(200)).log().all()
                .extract().response().body().as(CustomerResponse.class);
    }

    @Step("Получить кастомера по номеру телефона в старой системе")
    public Response findCustomerByPhoneNumber(String requestCustomerByIdDto) {
        return given().contentType(ContentType.XML)
                .when()
                .body(requestCustomerByIdDto)
                .headers("Content-Type", "application/xml")
                .post("/customer/findByPhoneNumber")
                .then().statusCode(200).log().all().extract().response();
    }

    @Step("Сменить статус клиента")
    public void changeCustomerStatus(String customerId, String token, String status, Integer code){
        given().spec(requestSpecification())
                .when()
                .headers("authToken", token)
                .body(ChangeCustomerStatusResponse.builder().status(status).build())
                .post("/customer/"+customerId+"/changeCustomerStatus")
                .then().statusCode(code).log().all();
    }
}
