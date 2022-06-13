//package com.lo.auctionsystem.Controllers
//
//import akka.stream.Materializer
//import org.scalatest._
//import flatspec._
//import matchers._
//import org.mockito.scalatest.MockitoSugar
//import org.scalatest.concurrent.ScalaFutures
//import org.scalatestplus.play.guice.GuiceOneAppPerSuite
//import play.api.libs.json.JsValue
//import play.api.libs.json._
//import com.lo.auctionsystem.Controllers.AuctionController
//import com.lo.auctionsystem.service.AuctionService
//import org.mockito.Mockito.RETURNS_SMART_NULLS
//import play.api.test.Helpers.call
//
//import scala.concurrent.ExecutionContext
//
//class AuctionControllerTest
//  extends AnyFlatSpec
//  with MockitoSugar
//  with BeforeAndAfterEach
//  with ScalaFutures
//  with GuiceOneAppPerSuite {
//
//  implicit val ec = ExecutionContext.Implicits.global
//  implicit val mat: Materializer = app.materializer
//  private val mockAuctionService = mock[AuctionService](RETURNS_SMART_NULLS)
//
//  private val testAuctionController = new AuctionController(mockAuctionService)
//
//  "item Jsvalue" should "be converted to Item object" in {
//
//    val itemJs: JsValue = Json.parse("""
//  {
//    "id": "a8cfcb76-7f24-4420-a5ba-d46dd77bdffd",
//    "type": "newItem",
//    "name": "Bicycle",
//    "description": "Hot Wheels Child's Bicycle",
//    "timeOfAuction": 20
//  }
//  """)
//
//    val result =
//      call(testAuctionController.platform, appVersion, locale), getAnnouncementsFakeRequest(asMemberId))
//
//  }
//}
