package models.response;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.request.HeaderRequest;
import models.response.BodyResponse;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerByIdResponse {
    @JacksonXmlProperty(localName = "Header")
    public HeaderResponse headerResponse;
    @JacksonXmlProperty(localName = "Body")
    public BodyResponse bodyResponse;
}
