package models.request;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerByIdRequest {
    @JacksonXmlProperty(localName = "Header")
    public Header header;
    @JacksonXmlProperty(localName = "Body")
    public Body body;
}
