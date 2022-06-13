package com.lo.auctionsystem.service

import com.lo.auctionsystem.models.{AuctionItem, Bid, RawAuctionItem, RawBid}
import com.lo.auctionsystem.timer._
import org.joda.time.{DateTime, Seconds}

trait AuctionService {
  var auctionItems: List[AuctionItem]
  var auctionItemBids: Map[String, List[Bid]]

  def addNewAuctionItems(item: RawAuctionItem): List[AuctionItem]

  def receiveBidOffer(bid: RawBid): Map[String, List[Bid]]

  def getCurrentListings: List[String]
}

class AuctionServiceImpl extends AuctionService {
  var auctionItems: List[AuctionItem] = List()
  var auctionItemBids: Map[String, List[Bid]] = Map()

  override def addNewAuctionItems(item: RawAuctionItem): List[AuctionItem] = {
    println(s"Adding auction Item ${item.id}")
    val itemWithTime = RawAuctionItem.toAuctionItem(item)
    //TODO check if same auction item already exists in active state
    auctionItems = auctionItems.::(itemWithTime)
    CountDownTimer.startCountDownForItem(item.timeOfAuction, item.id, getHighestBid)
    auctionItems
  }

  override def receiveBidOffer(rawBid: RawBid): Map[String, List[Bid]] = {
    val bid = RawBid.toBid(rawBid)
    val auctionItemId = bid.itemId
    println(s"Received Bid offer for $auctionItemId")

    if (!auctionItems.exists(x => x.id == auctionItemId)) {
      throw new NoSuchElementException
    }

    val auctionItem = auctionItems.filter(i => i.id == auctionItemId).head

    val endTime = auctionItem.endTime
    if (endTime.isAfterNow) {
      val bids: Option[List[Bid]] = auctionItemBids.get(auctionItemId).map(i => i.::(bid))

      val newBidsMap: Map[String, List[Bid]] = bids match {
        case Some(b) => Map(auctionItemId -> autoIncrementLosingBids(b))
        case None    => Map(auctionItemId -> List(bid))
      }
      auctionItemBids = auctionItemBids.concat(newBidsMap)
    }
    auctionItemBids
  }

  override def getCurrentListings: List[String] =
    if (auctionItems.isEmpty) {
      println("There are no auction listings.")
      List()
    } else {
      println("Auction Listings:")
      val currentListings = auctionItems.map(x => {
        val diffCurrentNEndTime = Seconds.secondsBetween(DateTime.now(), x.endTime).getSeconds
        val timeRemaining: Int = if (diffCurrentNEndTime < 0) 0 else diffCurrentNEndTime
        val itemId = x.id
        val active: Boolean = timeRemaining > 0
        val (_, highestBid) = getHighestBid(itemId)
        println(
          s"Item Id: $itemId    Item Name: ${x.name}   Description: ${x.description}   " +
            s"Time Remaining: $timeRemaining   Highest Bid: $highestBid   Active:$active"
        )
        s"Item Id: $itemId    Item Name: ${x.name}   Description: ${x.description}   Time Remaining: $timeRemaining  Highest Bid: $highestBid"
      })
      currentListings
    }

  private def autoIncrementLosingBids(bids: List[Bid]) = {
    println("Auto increment losing bids")
    val winningBid: Bid = getWinningBid(bids)

    val losingBids = bids
      .filter(b => b.bidAmount < winningBid.bidAmount)
      .map(
        m =>
          Bid(
            m.id,
            m.bidderName,
            m.itemName,
            m.itemId,
            getNextBid(m.bidAmount, m.maxBidAmount, m.bidIncrement),
            m.maxBidAmount,
            m.bidIncrement,
            m.offerPlacedTime
        )
      )
    losingBids.::(winningBid)
  }

  private def getNextBid(bidAmt: Double, maxBid: Double, bidIncrement: Int): Double = {
    println("Get next bid amount")
    val newBidAmt = bidAmt + bidIncrement
    if (newBidAmt > maxBid) maxBid else bidAmt + bidIncrement
  }

  private def getHighestBid(itemId: String): (String, Double) = {
    //TODO validate empty bids
    val bids: Option[List[Bid]] = auctionItemBids.get(itemId)
    val winningBid = bids.map(b => getWinningBid(b))
    (winningBid.head.bidderName, winningBid.head.bidAmount)
  }

  private def getWinningBid(bids: List[Bid]) =
    bids.sortBy(b => (-b.bidAmount, b.offerPlacedTime.getMillis)).head

}
