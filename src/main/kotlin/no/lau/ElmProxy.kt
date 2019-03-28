import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.camel.Exchange
import org.apache.camel.Header
import java.io.IOException

class ElmProxy(internal val targetDir: String) {
    internal var client = OkHttpClient()

    fun perform(@Header(Exchange.HTTP_PATH) filename: String): String {
        val request = Request.Builder()
            .url("$targetDir/$filename")
            .build()
        client.newCall(request).execute().use {response ->
            return if (response.isSuccessful) {
                response.body()!!.string()
            } else {
                throw IOException("Could perform $filename. ${response.networkResponse()!!.message()}")
            }
        }
    }
}