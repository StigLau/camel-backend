package no.lau;

import org.w3c.dom.Document;

import org.apache.camel.CamelContextAware;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class CxfJavaOnlyCamelContextAwareTest extends CamelTestSupport {
    private static int port1 = 8009;

    @Test
    public void testCxfEndpointHasCamelContext() throws Exception {
        String s = "<GetPerson xmlns=\"http://camel.apache.org/wsdl-first/types\"><personId>123</personId></GetPerson>";
        Document xml = context.getTypeConverter().convertTo(Document.class, s);

        log.info("Endpoints: {}", context.getEndpoints());
        Object output = template.requestBody("personService", xml);
        assertNotNull(output);

        // using CxfPayload in payload mode

        // convert the payload body to string
        String reply = "omfg";
        assertNotNull(reply);

        assertTrue(reply.contains("<personId>123</personId"));
        assertTrue(reply.contains("<ssn>456</ssn"));
        assertTrue(reply.contains("<name>Donald Duck</name"));

        assertTrue(context.getEndpoint("personService") instanceof CamelContextAware);
        assertNotNull("CamelContext should be set on CxfEndpoint", context.getEndpoint("personService").getCamelContext());
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {

                from("direct:omfg").process(new Processor() {
                    @Override
                    public void process(Exchange exchange) throws Exception {
                        String s = "<GetPersonResponse xmlns=\"http://camel.apache.org/wsdl-first/types\">"
                                + "<personId>123</personId><ssn>456</ssn><name>Donald Duck</name>"
                                + "</GetPersonResponse>";

                        Document xml = context.getTypeConverter().convertTo(Document.class, s);
                        exchange.getOut().setBody(xml);
                    }
                });
            }
        };
    }
}
