package nullpointer.test.controller;

import java.io.InputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import nullpointer.test.model.RateController;
import nullpointer.test.model.RateRequest;
import nullpointer.test.model.RateResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class CbrRateController implements RateController
{
    private static final Logger log = LoggerFactory.getLogger(CbrRateController.class);
    final private static DocumentBuilderFactory BF = DocumentBuilderFactory.newInstance();
    final private static XPathFactory XF = XPathFactory.newInstance();
    final private static XPathExpression EXP_DATE;
    static {
        try {
            XPath phDate = XF.newXPath();
            EXP_DATE = phDate.compile("/ValCurs/@Date");
        } catch (Exception ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }

    private Document readDoc(InputStream in) {
        try {
            DocumentBuilder db = BF.newDocumentBuilder();
            return db.parse(in);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private BigDecimal getRate(Document doc, String cur) {
        try {
            XPath ph = XF.newXPath();
            XPathExpression exp = ph.compile(String.format("/ValCurs/Valute[CharCode/text()='%s']/Value", cur));
            String result = exp.evaluate(doc);
            result = result.replace(",", ".");
            log.trace("rate {} for curr {}", result, cur);
            // FIXME null
            return new BigDecimal(result);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private Date getDate(Document doc) {
        try {
            String date = EXP_DATE.evaluate(doc);
            // FIXME null
            return new SimpleDateFormat(date.contains("/") ? "dd/MM/yyyy" : "dd.MM.yyyy").parse(date);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public RateResponse getRate(RateRequest request) {
        if (request == null)
            throw new NullPointerException("request");
        try {
            URL url =
                new URL(String.format(
                    "http://www.cbr.ru/scripts/XML_daily.asp?date_req=%1$td/%1$tm/%1$tY",
                    request.getDate()));
            try (InputStream in = url.openStream()) {
                final Document doc = readDoc(in);
                log.trace("doc = {}", new Object()
                {
                    @Override
                    public String toString() {
                        try {
                            Transformer transformer = TransformerFactory.newInstance().newTransformer();
                            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                            // initialize StreamResult with File object to save to file
                            StreamResult result = new StreamResult(new StringWriter());
                            DOMSource source = new DOMSource(doc);
                            transformer.transform(source, result);
                            return result.getWriter().toString();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });
                return new RateResponse( //
                    request.getCode(),
                    getRate(doc, request.getCode()),
                    getDate(doc));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Error processing " + request, ex);
        }
    }

    public static void main(String[] args) {
        System.out.println(new CbrRateController().getRate(new RateRequest("USD", new Date())));
    }
}
