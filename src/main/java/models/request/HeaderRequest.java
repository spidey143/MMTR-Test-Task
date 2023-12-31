package models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeaderRequest {
    private String authToken;
}
