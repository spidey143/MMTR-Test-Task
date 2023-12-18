package steps;

import io.qameta.allure.Step;
import models.responseModels.PhoneResponse;

import java.util.List;

import static io.restassured.RestAssured.given;
import static service.Specification.requestSpecification;

public class SubscriberActivationSteps {

    @Step("Получить спико свободных номеров телефона")
    public List<PhoneResponse> getEmptyPhones(String token){
        return given().spec(requestSpecification())
                .when()
                .headers("authToken", token)
                .get("/simcards/getEmptyPhone")
                .then().log().all()
                .extract().body().jsonPath().getList("phones");
    }

    @Step("Создать нового кастомера")
    public Customer addNewCustomer(String token){
        return null;
    }
}
