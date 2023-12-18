package base;

import enums.Role;
import io.restassured.response.Response;
import lombok.Getter;
import models.requestModels.AuthRequest;
import steps.Steps;

public class BaseTest implements Steps {

  private Response response;
  @Getter
  private String token;

  public void login(Role role, String login, String password) {
    response = AUTH_STEPS.postLoginRequest(
        AuthRequest.builder().login(login).password(password).build());
    token = response.body().jsonPath().getString("token");
    System.out.println(token);
  }

}
