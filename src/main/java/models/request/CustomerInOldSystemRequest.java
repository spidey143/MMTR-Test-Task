package models.request;

import lombok.*;


import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)

@XmlRootElement(name = "ns3:Envelope")
public class CustomerInOldSystemRequest {
    @XmlElement(name = "ns2:Header")
    public HeaderRequest headerRequest;
    @XmlElement(name = "ns2:Body")
    public BodyRequest bodyRequest;
}
