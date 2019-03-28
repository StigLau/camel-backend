
import no.lau.AwsAuth
import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.main.Main
import org.apache.camel.model.language.MethodCallExpression
import org.apache.camel.support.processor.idempotent.FileIdempotentRepository
import java.io.File

/**
 * A static main() so we can easily run these routing rules in our IDE
 */
fun main(args: Array<String>) {
    val main = Main()
    main.addRouteBuilder(ExampleRoutesOriginal())
    main.bind("amazonS3Client", AwsAuth().amazonS3Client())
    main.run(args)
}

class ExampleRoutesOriginal() : RouteBuilder() {

    override fun configure() {
        from("jetty:http://0.0.0.0:8001/src/?matchOnUriPrefix=true")
            .setBody(MethodCallExpression(ElmProxy("http://localhost:8000/src")))



        from("file://inbox?move=.done").id("StoreInS3").streamCaching()
            .log( "consuming:File")
            .to("seda:writequeue")

        from("seda:writequeue")
            .setHeader("CamelAwsS3Key", constant("aKey"))
            .to("aws-s3://cantaraz?region=eu-north-1&amazonS3Client=#amazonS3Client")



        from("aws-s3://cantaraz?region=eu-north-1&amazonS3Client=#amazonS3Client&deleteAfterRead=true&delay=5000").streamCaching()
            .log(LoggingLevel.INFO, "consuming", "Consumer Fired!")
            .idempotentConsumer(header("CamelAwsS3ETag"), FileIdempotentRepository.fileIdempotentRepository(File("target/file.data"), 250, 512000))
            .log(LoggingLevel.INFO, "Fetched file:\${in.header.CamelAwsS3Key} with body: \${body}")
            .to("file:target/s3out?fileName=\${in.header.CamelAwsS3Key}")

    }
}
