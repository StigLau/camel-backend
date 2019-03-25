
import org.apache.camel.example.MyRouteBuilder
import org.apache.camel.main.Main

/**
 * A static main() so we can easily run these routing rules in our IDE
 */
fun main(args: Array<String>) {
    System.out.println("\n\n\n\n");
    System.out.println("===============================================");
    System.out.println("Open your web browser on http://localhost:8080");
    System.out.println("Press ctrl+c to stop this example");
    System.out.println("===============================================");
    System.out.println("\n\n\n\n");

    val main = Main()
    main.addRouteBuilder(MyRouteBuilder())
    main.run(args)
}
