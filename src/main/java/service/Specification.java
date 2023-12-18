package service;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.aeonbits.owner.ConfigFactory;

import static io.restassured.http.ContentType.JSON;

public class Specification{
    private static final TestConfig CONFIG = ConfigFactory.create(TestConfig.class);

    public static RequestSpecification requestSpecification(){
        return new RequestSpecBuilder()
                .setBaseUri(CONFIG.baseURl())
                .setContentType(JSON)
                .build();
    }

    public static ResponseSpecification responseSpecification(Integer code){
        return new ResponseSpecBuilder()
                .expectStatusCode(code)
                .expectContentType("application/json")
                .build();
    }
}
