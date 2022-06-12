import play.api.libs.json.{JsSuccess, JsValue, Json}
import models._
import org.joda.time.DateTime
import service._

import scala.io.{Codec, Source}

object AuctionSystem extends App with AuctionSystemBoot {
  println(s"Start Time: ${DateTime.now()}")
  val itemsOrBids = loadJson()
  val ingestionRate = 1
  itemsOrBids.foreach(i => {
    addItemOrBid(i)
    Thread.sleep(ingestionRate)
  })
  println(s"End Time: ${DateTime.now()}")
}

trait AuctionSystemBoot {
  val serviceImpl: auctionServiceImpl = new auctionServiceImpl

  def addItemOrBid(item: JsValue) = {
    item.validate[AuctionItem] match {
      case JsSuccess(i, _) => serviceImpl.addNewAuctionItems(i)
      case _               => println("Ignoring error")
    }

    item.validate[Bid] match {
      case JsSuccess(b, _) => serviceImpl.receiveBidOffer(b)
      case _               => println("Ignoring error")
    }

  }

  def loadJson() = {
    println("Loading Json file")
    val strItemsOrBids =
      Source
        .fromInputStream(getClass.getResourceAsStream("/input/auctionItems.json"))((Codec.UTF8))
        .getLines()
        .mkString
    val itemsOrBids = Json.parse(strItemsOrBids).as[Seq[JsValue]]
    itemsOrBids
  }
}
