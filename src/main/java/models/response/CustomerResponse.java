package models.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CustomerResponse {
    @JsonProperty("return")
    private Return customerReturn;
}
