package com.lo.auctionsystem.models

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class AuctionItemTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  "toAuctionItem" should "convert RawAuctionItem object to AuctionItem object" in {

    val rawAuctionItem = RawAuctionItem("auctionId", "name", "auctionType", Some("desc"), 20)
    val endTime = RawAuctionItem.currentTime.plusSeconds(20)

    val expectedAuctionItem =
      AuctionItem("auctionId", "name", Some("desc"), 20, RawAuctionItem.currentTime, endTime)
    val toAuctionItem = RawAuctionItem.toAuctionItem(rawAuctionItem)

    toAuctionItem shouldBe (expectedAuctionItem)
  }
}
