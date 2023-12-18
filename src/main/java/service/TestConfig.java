package service;

import org.aeonbits.owner.Config;

@Config.Sources("classpath:resources.properties")
public interface TestConfig extends Config {
    @Key("baseURl")
    String baseURl();
}
