package models.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerRequest {
  private String name;
  private Long phone;
  private AdditionalParameters additionalParameters;
}
