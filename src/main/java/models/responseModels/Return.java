package models.responseModels;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.requestModels.AdditionalParameters;

@Data
public class Return {
  private String customerId;
  private String name;
  private String status;
  private Long phone;
  private AdditionalParameters additionalParameters;
  private String pd;
}
