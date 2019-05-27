import mnb.MNBArfolyamServiceSoap;
import mnb.MNBArfolyamServiceSoapImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws Exception {
        MNBArfolyamServiceSoapImpl client = new MNBArfolyamServiceSoapImpl();
        MNBArfolyamServiceSoap service = client.getCustomBindingMNBArfolyamServiceSoap();
        Map<String, Double> currencies = getExchangeRates(service);
        System.out.println(currencies);
        Double hufprice = 1000d;
        System.out.println(hufprice + " HUF is EUR " + hufprice / currencies.get("EUR"));
    }

    private static Map<String, Double> getExchangeRates(MNBArfolyamServiceSoap service) throws Exception {
        String response = service.getCurrentExchangeRates();
        Document doc = parse(response);
        NodeList ratesXml = doc.getElementsByTagName("Rate");
        Map<String, Double> rates = new HashMap<>();
        for (int i = 0; i < ratesXml.getLength(); i++) {
            Node rateNode = ratesXml.item(i);
            if (rateNode.getNodeType() == Node.ELEMENT_NODE) {
                Element rate = (Element) rateNode;
                String ticker = rate.getAttribute("curr");
                String rateText = rate.getTextContent().replace(",",".");
                rates.put(ticker, Double.parseDouble(rateText));
            }
        }
        return rates;
    }

    private static Document parse(String response) throws Exception {
        DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        ByteArrayInputStream input = new ByteArrayInputStream(response.getBytes("UTF-8"));
        return builder.parse(input);
    }
}
