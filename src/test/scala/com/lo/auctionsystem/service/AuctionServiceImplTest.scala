package com.lo.auctionsystem.service

import com.lo.auctionsystem.models.{AuctionItem, Bid, RawAuctionItem, RawBid}
import org.mockito.scalatest.MockitoSugar
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AuctionServiceImplTest
  extends AnyFlatSpec
  with Matchers
  with MockitoSugar
  with ScalaFutures
  with BeforeAndAfterEach {
  val auctionServiceImpl = new AuctionServiceImpl

  "addNewAuctionItems" should "contain new item in the list" in {
    val auctionItem = RawAuctionItem("auctionId1", "name", "auctionType", Some("desc"), 20)
    val endTime = RawAuctionItem.currentTime.plusSeconds(20)

    val expectedResponse =
      List(AuctionItem("auctionId1", "name", Some("desc"), 20, RawAuctionItem.currentTime, endTime))

    auctionServiceImpl.addNewAuctionItem(auctionItem)
    val response: List[AuctionItem] = auctionServiceImpl.auctionItems
    response should equal(expectedResponse)
  }

  "addNewAuctionItems" should "contain multiple items in the list" in {
    val auctionItem = RawAuctionItem("auctionId2", "name", "auctionType", Some("desc"), 20)
    val endTime = RawAuctionItem.currentTime.plusSeconds(20)

    val expectedResponse =
      List(
        AuctionItem("auctionId2", "name", Some("desc"), 20, RawAuctionItem.currentTime, endTime),
        AuctionItem("auctionId1", "name", Some("desc"), 20, RawAuctionItem.currentTime, endTime)
      )

    auctionServiceImpl.addNewAuctionItem(auctionItem)
    val response: List[AuctionItem] = auctionServiceImpl.auctionItems
    response should equal(expectedResponse)
  }

  "receiveBidOffer" should "throw no such element error" in {
    val bid = RawBid("bidId", "testName", "itemType", Some("itemName"), "itemId", 20, 40, 5)

    assertThrows[NoSuchElementException] {
      auctionServiceImpl.receiveBidOffer(bid)
    }
  }

  "receiveBidOffer" should "match item itemBidsMap" in {
    val auctionItem = RawAuctionItem("auctionId2", "name", "auctionType", Some("desc"), 20)
    auctionServiceImpl.addNewAuctionItem(auctionItem)

    val rawBid = RawBid("bidId", "testName", "itemType", Some("itemName"), "auctionId2", 20, 40, 5)

    val bid =
      Bid(
        "bidId",
        "testName",
        Some("itemName"),
        "auctionId2",
        20.toDouble,
        40.toDouble,
        5,
        RawBid.currentTime
      )
    auctionServiceImpl.auctionItems.foreach(x => println(x))
    val exceptedResponse = Map("auctionId2" -> List(bid))

    auctionServiceImpl.receiveBidOffer(rawBid)
    val response = auctionServiceImpl.auctionItemBids
    println(response)
    response should equal(exceptedResponse)
  }

  "receiveBidOffer" should "increase the losing bids" in {
    val auctionItem = RawAuctionItem("auctionId2", "name", "auctionType", Some("desc"), 20)
    auctionServiceImpl.addNewAuctionItem(auctionItem)

    val rawBid1 = RawBid("bidId1", "testName", "itemType", Some("itemName"), "auctionId2", 20, 40, 10)
    val rawBid2 = RawBid("bidId2", "testName", "itemType", Some("itemName"), "auctionId2", 25, 40, 5)

    val bid1 =
      Bid(
        "bidId1",
        "testName",
        Some("itemName"),
        "auctionId2",
        30.toDouble,
        40.toDouble,
        10,
        RawBid.currentTime
      )

    val bid2 =
      Bid(
        "bidId2",
        "testName",
        Some("itemName"),
        "auctionId2",
        25.toDouble,
        40.toDouble,
        5,
        RawBid.currentTime
      )

    val exceptedResponse = Map("auctionId2" -> List(bid2, bid1))

    auctionServiceImpl.receiveBidOffer(rawBid1)
    auctionServiceImpl.receiveBidOffer(rawBid2)

    val response = auctionServiceImpl.auctionItemBids
    response should equal(exceptedResponse)
  }

  "printCurrentListings" should "return list of current listings for the item" in {
    val auctionServiceListings = new AuctionServiceImpl

    val auctionItem = RawAuctionItem("auctionId2", "name", "auctionType", Some("desc"), 20)
    auctionServiceListings.addNewAuctionItem(auctionItem)

    val rawBid1 = RawBid("bidId1", "testName", "itemType", Some("itemName"), "auctionId2", 20, 40, 10)
    val rawBid2 = RawBid("bidId2", "testName", "itemType", Some("itemName"), "auctionId2", 25, 40, 5)

    val exceptedResponse = List(
      "Item Id: auctionId2    Item Name: name   Description: Some(desc)   Time Remaining: 19  Highest Bid: Some(30.0)"
    )

    auctionServiceListings.receiveBidOffer(rawBid1)
    auctionServiceListings.receiveBidOffer(rawBid2)

    val response = auctionServiceListings.getCurrentListings

    response should equal(exceptedResponse)
  }

}
