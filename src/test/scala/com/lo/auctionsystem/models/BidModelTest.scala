package com.lo.auctionsystem.models

import org.scalatest.BeforeAndAfterEach
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class BidModelTest extends AnyFlatSpec with Matchers with BeforeAndAfterEach {

  "toBid" should "convert RawBid object to Bid object" in {

    val rawBid = RawBid("bidId", "testName", "itemType", Some("itemName"), "itemId", 20, 40, 5)

    val expectedBid =
      Bid("bidId", "testName", Some("itemName"), "itemId", 20.toDouble, 40.toDouble, 5, RawBid.currentTime)
    val toBid = RawBid.toBid(rawBid)

    toBid shouldBe (expectedBid)
  }
}
