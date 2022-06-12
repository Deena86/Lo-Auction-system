package service

import models._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import java.util.{Timer, TimerTask}
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

trait AuctionService {
  var items: List[AuctionItemWithTime]
  var itemBidsMap: Map[String, List[Bid]]

  def addNewAuctionItems(item: AuctionItem): Future[Boolean]

  def receiveBidOffer(bid: Bid): Future[Boolean]
}

class auctionServiceImpl extends AuctionService {
  var items: List[AuctionItemWithTime] = List()
  var itemBidsMap: Map[String, List[Bid]] = Map()

  override def addNewAuctionItems(item: AuctionItem): Future[Boolean] = {
    println("Adding a new auction Item")
    val itemWithTime = AuctionItem.toAuctionItemWithDate(item)

    items = items.::(itemWithTime)
    startCountDownForItem(item.timeOfAuction, item.id)
    Future(true)
  }

  private def startCountDownForItem(auctionTime: Int, itemId: String): Unit = {
    println("Bidding CountDown started for Item $itemId", itemId)

    val f: Future[String] = Future {
      Thread.sleep(auctionTime)
      "future value"
    }

    val f2 = f.map { s =>
      println("OK!")
      println("OK!")
      printWinningBid(itemId)
    }

    Await.ready(f2, auctionTime.seconds)
    println("exit")

//    try {
//      val timer = new Timer
//      timer.scheduleAtFixedRate(new TimerTask() {
//        var i: Int = auctionTime
//        override def run(): Unit = {
//          i -= 1
//          if (i < 0) {
//            timer.cancel()
//            printWinningBid(itemId)
//          }
//        }
//      }, 0, 1000)
//    } catch {
//      case e: Exception =>
//        e.printStackTrace()
//    }
    println(s"Bidding CountDown Ended for Item $itemId", itemId)
  }

  override def receiveBidOffer(bid: Bid): Future[Boolean] = {
    println("Adding a Bid")
    //TODO: validation for item does not exist
    val auctionItemId = bid.item
    val auctionItem = items.filter(i => i.id == auctionItemId).head
    val endTime = auctionItem.startTime.plusSeconds(auctionItem.timeOfAuction)
    if (endTime.isAfterNow) {
      val bids: Option[List[Bid]] = itemBidsMap.get(auctionItemId).map(i => i.::(bid))

      val newBidsMap: Map[String, List[Bid]] = bids match {
        case Some(b) => Map(auctionItemId -> autoIncrementLosingBids(b))
        case None    => Map(auctionItemId -> List(bid))
      }
      itemBidsMap = itemBidsMap.concat(newBidsMap)
    }
    Future(true)
  }

  private def autoIncrementLosingBids(bids: List[Bid]) = {
    println("Auto increment losing bids")
    val winningBid: Bid = bids.sortBy(b => b.startingBid)(Ordering[Int].reverse).head
    val losingBids = bids
      .filter(b => b.startingBid < winningBid.startingBid)
      .map(
        m =>
          Bid(
            m.id,
            m.name,
            m.`type`,
            m.itemName,
            m.item,
            getNextBid(m.startingBid, m.maxBid, m.bidIncrement),
            m.maxBid,
            m.bidIncrement
        )
      )
    losingBids.::(winningBid)
  }

  private def getNextBid(bidAmt: Int, maxBid: Int, bidIncrement: Int): Int = {
    println("Get next bid amount")
    val newBidAmt = bidAmt + bidIncrement
    if (newBidAmt > maxBid) maxBid else bidAmt + bidIncrement
  }

  def printWinningBid(itemId: String) = {
    val bids: Option[List[Bid]] = itemBidsMap.get(itemId)
    val winningBid = bids.map(b => b.sortBy(b => b.startingBid)(Ordering[Int].reverse).head)
    println(
      s"Winning Buyer & Amount for $itemId is ${winningBid.head.name} and ${winningBid.head.startingBid}"
    )
  }

}
