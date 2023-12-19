package base;

import io.restassured.response.Response;
import lombok.Getter;
import models.request.AuthRequest;
import steps.Steps;

@Getter
public class BaseTest implements Steps {

    private String token;

    public void login(String login, String password) {
        Response response = AUTH_STEPS.postLoginRequest(
                AuthRequest.builder().login(login).password(password).build());
        token = response.body().jsonPath().getString("token");
    }

}
