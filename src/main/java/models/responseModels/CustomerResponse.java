package models.responseModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CustomerResponse {
  @JsonProperty("return")
  private Return myreturn;
}
