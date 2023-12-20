package models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.request.AdditionalParametersRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnResponse {
  private String customerId;
  private String name;
  private String status;
  private Long phone;
  private AdditionalParametersRequest additionalParameters;
  private String pd;

  public boolean equals(ReturnResponse other) {
    return customerId.equals(other.customerId)
            && name.equals(other.name) && status.equals(other.status) && phone.equals(other.phone);
  }
}
