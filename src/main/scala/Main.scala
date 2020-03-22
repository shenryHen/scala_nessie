import scalaj.http._

object Main extends App {
    val KEY: String = sys.env.getOrElse("NESSIE_API_KEY", "")
    println(KEY)
    val BANK_URL: String = "http://api.reimaginebanking.com"
    val response: HttpResponse[String] = Http(BANK_URL + "/atms").param("key", KEY).asString
    println(response.body)
    println(response.code)
}
