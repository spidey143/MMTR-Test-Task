@XmlSchema(
        elementFormDefault = XmlNsForm.UNQUALIFIED,
        xmlns = {
                @XmlNs(prefix = "ns2", namespaceURI = "soap"),
                @XmlNs(prefix = "ns3", namespaceURI = "http://schemas.xmlsoap.org/soap/envelope")
        }
)
package models.request;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;