import scalaj.http._
import cats.syntax.either._
import io.circe._, io.circe.parser._

// class Account {
// 	val accounID: String
// 	val firstName: String
// 	val lastName: String
// }

object Main extends App {
	// var is mutable
	// val is immutable
    val KEY: String = sys.env.getOrElse("NESSIE_API_KEY", "")
    println(KEY)
    val BANK_URL: String = "http://api.reimaginebanking.com"
    var response: HttpResponse[String] = Http(BANK_URL + "/atms").param("key", KEY).asString
    //print()
    val customerID = "5d6ecf67322fa016762f2f6d"
    val customerURL = "/customers/" + customerID
    response = Http(BANK_URL+customerURL).param("key", KEY).asString
    val customer: String = response.body
    println(customer)
    var doc: Json = parse(customer).getOrElse(Json.Null)
    val cursor: HCursor = doc.hcursor
    //val firstName: Decoder.Result[String] = cursor.downField("first_name").as[String].toString()
    var firstName: String = cursor.downField("first_name").as[String].productElement(0).toString()
    println(s"Customer first name $firstName")
    val modCursor: ACursor = cursor.downField("first_name").withFocus(_.mapString(s => "Raquel"))
    println(modCursor.top)
    val modifiedField: String = """{"street_name": "Octocat St", "city": "Atlanta", "state": "GA"}"""
    println(BANK_URL+customerURL)
    // val putResponse: HttpResponse[String] = Http(BANK_URL+customerURL).param("key", KEY).method("PUT").postData(modifiedField).asString
    // println(putResponse.body)
    // println(putResponse.code)
   	// println(putResponse.headers)

   	//POST for /account, /bill
}
