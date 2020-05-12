import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.unmarshalling.Unmarshal
import play.api.libs.json._

import scala.concurrent._
import scala.util.{ Failure, Success }
import scala.concurrent.duration._
import scala.language.postfixOps

object Main extends App {
  val KEY: String = sys.env.getOrElse("NESSIE_API_KEY", "")
  val BANK_URL: String = "http://api.reimaginebanking.com"
  val CUSTOMER_URL: String = BANK_URL + "/customers"
  val ACCOUNT_URL: String = BANK_URL + "/accounts"
 	implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  //GET, atms paginated
  var atmEndpoint2: String = null
 	val atmReq = HttpRequest(
		method = HttpMethods.GET,
		uri = BANK_URL+"/atms?lat=38.9283&lng=-77.1753&rad=50&key=" + KEY,
		entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, "data")
	)
  val atmResp = Http().singleRequest(atmReq)
    .flatMap { res =>
      Unmarshal(res).to[String].map { respData =>
        val body: JsValue = Json.parse(respData)
        val atmArr = (body \ "data").as[JsArray]
        println("Page 1, last ATM data: ", atmArr.last)
        atmEndpoint2 = (body \ "paging" \ "next").as[String]
      }
    }
  atmResp.onComplete{
    case Success(_) => true
    case Failure(e) => println(e)
  }
  Thread.sleep(1000)
  //val nextPage = Await.result(atmResp, 3 second)
  val atmURL2 = String.format("%s%s", BANK_URL, atmEndpoint2)
  val atmReq2 = HttpRequest(
    method= HttpMethods.GET,
    uri = atmURL2,
    entity = HttpEntity(ContentTypes.`text/plain(UTF-8)`, "data")
  )
  val atmResp2 = Http().singleRequest(atmReq2)
    .flatMap { res => 
      Unmarshal(res).to[String].map { respData => 
        val body: JsValue = Json.parse(respData)
        val atmArr2 = (body \ "data").as[JsArray]
        println("Page 2, first ATM data:", atmArr2.head)
      }
    }
  // POST, create customer
  var newCustID: String = null 
  val custPostReq = HttpRequest(
    method = HttpMethods.POST,
    uri = CUSTOMER_URL + "/?key=" + KEY,
    entity = HttpEntity(ContentTypes.`application/json`, """{"first_name": "Mace", "last_name": "Windu", "address":{"street_number": "1", "street_name": "Council Rd", "city": "Coruscant", "state": "NJ", "zip": "31154"}}""")
  )
  val result = Http().singleRequest(custPostReq)
    .flatMap { res =>
      Unmarshal(res).to[String].map { data =>
        var body: JsValue = Json.parse(data)
        newCustID = (body \ "objectCreated" \ "_id").as[String]
      }
    }
  result.onComplete {
    case Success(_) =>
      println(s"New customer ID: $newCustID")
    case Failure(e) =>
      println(s"Failure: $e")
  }
  Thread.sleep(2000)

  // //POST create account
  var newAccID: String = null
  val newCustURL = String.format("%s%s%s%s%s%s", BANK_URL, "/customers/", s"$newCustID", "/accounts", "/?key=", KEY) // for customer "obiwan"
  val newAccNum: String = "0000000000000003"
  val accountPostReq = HttpRequest(
    method = HttpMethods.POST,
    uri = newCustURL,
    entity = HttpEntity(ContentTypes.`application/json`, s"""{"type":"Credit Card","nickname":"High Ground Account","rewards": 10000,"balance": 0,"account_number": "$newAccNum"}""")
  )
  val accResult = Http().singleRequest(accountPostReq)
    .flatMap { res =>
      Unmarshal(res).to[String].map { data =>
        var body: JsValue = Json.parse(data)
        newAccID = (body \ "objectCreated" \ "_id").as[String]
      }
    }
  accResult.onComplete {
    case Success(_) =>
      println(s"New account ID: $newAccID")
    case Failure(e) =>
      println(e)
  }
  Thread.sleep(2000)


  // // POST, create bills
  val newBillURL = String.format("%s%s%s%s", ACCOUNT_URL, s"/$newAccID" ,"/bills", s"?key=$KEY")
  var newBillID: String = null
  val billPostReq = HttpRequest(
    method = HttpMethods.POST,
    uri = newBillURL,
    entity = HttpEntity(ContentTypes.`application/json`, """{"status":"pending","payee":"blaf","nickname":"daf","payment_date":"2020-04-29","recurring_date": 1,"payment_amount": 23.23}""")
  )
  val newBillResp = Http().singleRequest(billPostReq)
    .flatMap { res =>
      Unmarshal(res).to[String].map { data =>
        var body: JsValue = Json.parse(data)
        newBillID = (body \ "objectCreated" \ "_id").as[String]
      }
    }
  newBillResp.onComplete {
    case Success(_) =>
      println(s"New bill ID: $newBillID")
    case Failure(e) =>
      println(e)
  }
  Thread.sleep(2000)

  // //POST, deposit money movement endpoint
  val depositURL = String.format("%s%s", ACCOUNT_URL, s"/$newAccID/deposits/?key=$KEY")
  var depositID: String = null
  val depositReq = HttpRequest(
    method = HttpMethods.POST,
    uri = depositURL,
    entity = HttpEntity(ContentTypes.`application/json`, """{"medium": "balance",
      "amount": 66.66,
      "transaction_date": "2020-05-10",
      "status": "completed",
      "description": "string"
    }"""
  ))

  val depositResp = Http().singleRequest(depositReq)
    .flatMap { res =>
      Unmarshal(res).to[String].map { data =>
        var body: JsValue = Json.parse(data)
        depositID = (body \ "objectCreated" \ "_id").as[String]
      }
    }
  depositResp.onComplete {
    case Success(_) =>
      println(s"New deposit ID: $depositID")
    case Failure(e) =>
      println(e)
  }

  Thread.sleep(2000)

  // // GET, get Enterprise Merchant 
  val entMerchID: String = "57cf75cea73e494d8675ec4a"
  var entMerchData: JsValue = null
  val entMerchURL = String.format("%s%s", BANK_URL, s"/enterprise/merchants/$entMerchID?key=$KEY")
  val entMerchReq = HttpRequest(uri = entMerchURL)
  val entMerch = Http().singleRequest(entMerchReq)
    .flatMap { res =>
      Unmarshal(res).to[String].map { data => 
        entMerchData = Json.parse(data)
      }
    }
  entMerch.onComplete {
    case Success(_) => println(s"Enterprise mechant data:\n ${Json.stringify(entMerchData)}")
    case Failure(e) => println(e)
  }
  Thread.sleep(3000)
  
  // POST, create new purchases for account
  var newPurchaseID: String = null
  val newPurchaseURL = String.format("%s%s", ACCOUNT_URL, s"/$newAccID/purchases?key=$KEY")
  val purchaseReq = HttpRequest(
    method = HttpMethods.POST,
    uri = newPurchaseURL,
    entity = HttpEntity(ContentTypes.`application/json`, """{"merchant_id": "57cf75cea73e494d8675ec4a", "medium": "balance", "purchase_date": "2020-05-10", "amount": 419, "status": "pending", "description": "JawaJiugs"}"""
  ))
  val purchaseResp = Http().singleRequest(purchaseReq)
    .flatMap{ res =>
      Unmarshal(res).to[String].map { data =>
        var body: JsValue = Json.parse(data)
        newPurchaseID = (body \ "objectCreated" \ "_id").as[String]
      }
    }
  purchaseResp.onComplete {
    case Success(_) => println(s"New purchase ID: $newPurchaseID")
    case Failure(e) => println(e)
  }
  Thread.sleep(2000)
  
  // DELETE, delete new account
  val deleteURL: String = String.format("%s%s", BANK_URL, s"/data?type=Accounts&key=$KEY")
  val delReq = HttpRequest(
    method = HttpMethods.DELETE,
    uri = deleteURL,
    //entity = HttpEntity(ContentTypes.`application/json`, "Accept")
  )
  val delResp = Http().singleRequest(delReq)
    .onComplete {
      case Success(_) => println("DELETE Accounts finished.\nAll Nessie API requests complete.")
      case Failure(e) => println(e)
    }
  Thread.sleep(2000)
}
