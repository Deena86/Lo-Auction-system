package com.lo.auctionsystem.service

import com.lo.auctionsystem.models.{AuctionItem, Bid, RawAuctionItem, RawBid}
import com.lo.auctionsystem.timer._
import org.joda.time.{DateTime, Seconds}

/** Interface for the Auction Service */
trait AuctionService {
  var auctionItems: List[AuctionItem]
  var auctionItemBids: Map[String, List[Bid]]

  def addNewAuctionItem(item: RawAuctionItem): List[AuctionItem]

  def receiveBidOffer(bid: RawBid): Map[String, List[Bid]]

  def getCurrentListings: List[String]
}

/** Auction Service Implementation
  *
  * Data Structures used:
  * auctionItems - List to hold all the auction items
  * auctionItemBids - Map to hold bids for the corresponding auction items
  */
class AuctionServiceImpl extends AuctionService {
  var auctionItems: List[AuctionItem] = List()
  var auctionItemBids: Map[String, List[Bid]] = Map()

  /** Adds a new auction item to the list and starts the countdown timer */
  override def addNewAuctionItem(item: RawAuctionItem): List[AuctionItem] = {
    println(s"Adding auction Item ${item.id}")
    val auctionItemWithTime = RawAuctionItem.toAuctionItem(item)
    CountDownTimer.startCountDownForItem(item.timeOfAuction, item.id, getHighestBid)
    auctionItems = auctionItems.::(auctionItemWithTime)
    getCurrentListings
    auctionItems
  }

  /** Receives bid offer for the existing auction items */
  override def receiveBidOffer(rawBid: RawBid): Map[String, List[Bid]] = {
    val bid = RawBid.toBid(rawBid)
    val auctionItemId = bid.itemId
    println(s"Received Bid offer for $auctionItemId")

    if (!auctionItems.exists(x => x.id == auctionItemId)) {
      throw new NoSuchElementException
    }

    val auctionItem = auctionItems.filter(i => i.id == auctionItemId).head

    if (auctionItem.endTime.isAfterNow) {
      val bids: Option[List[Bid]] = auctionItemBids.get(auctionItemId).map(i => i.::(bid))

      val newBidsMap: Map[String, List[Bid]] = bids match {
        case Some(b) => Map(auctionItemId -> autoIncrementLosingBids(b))
        case None    => Map(auctionItemId -> List(bid))
      }
      auctionItemBids = auctionItemBids.concat(newBidsMap)
    }
    getCurrentListings
    auctionItemBids
  }

  /** Prints the auction item listings */
  override def getCurrentListings: List[String] =
    if (auctionItems.isEmpty) {
      println("********* There are no auction listings **********")
      List()
    } else {
      println("****** Auction Listings ***********")
      val currentListings = auctionItems.map(x => {
        val diffCurrentNEndTime = Seconds.secondsBetween(DateTime.now(), x.endTime).getSeconds
        val timeRemaining: Int = if (diffCurrentNEndTime < 0) 0 else diffCurrentNEndTime
        val itemId = x.id
        val active: Boolean = timeRemaining > 0
        val highestBid = getHighestBid(itemId).map(h => h._2)
        println(
          s"Item Id: $itemId    Item Name: ${x.name}   Description: ${x.description}   " +
            s"Time Remaining: $timeRemaining   Highest Bid: $highestBid   Active:$active"
        )
        s"Item Id: $itemId    Item Name: ${x.name}   Description: ${x.description}   Time Remaining: $timeRemaining  Highest Bid: $highestBid"
      })
      println("**************************************************************************")
      currentListings
    }

  /** Auto increments losing bids relative to the highest bid for an item */
  private def autoIncrementLosingBids(bids: List[Bid]) = {
    val highestBid: Bid = getWinningBid(bids)

    val reassignedBids = bids
      .filter(b => b.bidAmount < highestBid.bidAmount)
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
    reassignedBids.::(highestBid)
  }

  /** Gets the next bid amount for a bid using current bid amount and auto-increment value */
  private def getNextBid(bidAmt: Double, maxBid: Double, bidIncrement: Int): Double = {
    val newBidAmt = bidAmt + bidIncrement
    if (newBidAmt > maxBid) maxBid else bidAmt + bidIncrement
  }

  /** Gets the highest bid for an item */
  private def getHighestBid(itemId: String): Option[(String, Double)] = {
    val bids: Option[List[Bid]] = auctionItemBids.get(itemId)
    if (bids.isEmpty) {
      None
    } else {
      val winningBid = bids.map(b => getWinningBid(b))
      Some((winningBid.head.bidderName, winningBid.head.bidAmount))
    }
  }

  /** Finds the highest bid amongst the existing bids */
  private def getWinningBid(bids: List[Bid]) =
    bids.sortBy(b => (-b.bidAmount, b.offerPlacedTime.getMillis)).head

}
