package models.response;

import lombok.Data;
import models.request.AdditionalParameters;

@Data
public class Return {
  private String customerId;
  private String name;
  private String status;
  private Long phone;
  private AdditionalParameters additionalParameters;
  private String pd;
}
