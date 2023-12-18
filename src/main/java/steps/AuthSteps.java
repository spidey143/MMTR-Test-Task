package steps;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import models.requestModels.AuthRequest;

import static io.restassured.RestAssured.*;
import static service.Specification.requestSpecification;
import static service.Specification.responseSpecification;

public class AuthSteps {

    @Step("Авторизация пользователя")
    public Response postLoginRequest(AuthRequest user){
        return given().spec(requestSpecification())
                .when()
                .body(user)
                .post("/login")
                .then().spec(responseSpecification(200)).log().all()
                .extract().response();
    }
}