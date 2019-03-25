package no.lau

import org.w3c.dom.Document

import org.apache.camel.CamelContextAware
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Assert
import org.junit.Test


class CxfJavaOnlyCamelContextAwareTest : CamelTestSupport() {

    @Test
    fun testCxfEndpointHasCamelContext() {
        val s = "<GetPerson xmlns=\"http://camel.apache.org/wsdl-first/types\"><personId>123</personId></GetPerson>"
        val xml = context.typeConverter.convertTo(Document::class.java, s)

        log.info("Endpoints: {}", context.endpoints)
        val output = template.requestBody("direct:person", xml)

    }

    override fun createRouteBuilder(): RouteBuilder {
        return object : RouteBuilder() {
            @Throws(Exception::class)
            override fun configure() {

                from("direct:person").process { exchange ->
                    exchange.out.body = "woop"
                }
            }
        }
    }

    companion object {
        private val port1 = 8009
    }
}
