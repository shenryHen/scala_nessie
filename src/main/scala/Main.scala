import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal


import scala.concurrent.Future
import scala.util.{ Failure, Success }

object Main extends App {
    val KEY: String = sys.env.getOrElse("NESSIE_API_KEY", "")
    val BANK_URL: String = "http://api.reimaginebanking.com"
   	implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext = system.dispatcher
 	val req = HttpRequest(
		method = HttpMethods.GET,
		uri = BANK_URL+"/atms/?key=" + KEY,
		entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, "data")
	)
	val responseFuture: Future[HttpResponse] = Http().singleRequest(req)
	responseFuture
      .onComplete {
        case Success(res) => println(Unmarshal(res).to[String])

        case Failure(_)   => sys.error("something wrong")
      }
}
