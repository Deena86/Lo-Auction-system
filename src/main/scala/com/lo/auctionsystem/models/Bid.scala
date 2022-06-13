package com.lo.auctionsystem.models

import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

case class RawBid(
  id: String,
  name: String,
  `type`: String,
  itemName: Option[String],
  item: String,
  startingBid: Int,
  maxBid: Int,
  bidIncrement: Int
)

case class Bid(
  id: String,
  bidderName: String,
  itemName: Option[String],
  itemId: String,
  bidAmount: Double,
  maxBidAmount: Double,
  bidIncrement: Int,
  offerPlacedTime: DateTime
)

object RawBid {
  val currentTime: DateTime = DateTime.now()

  def toBid(bid: RawBid): Bid =
    Bid(
      bid.id,
      bid.name,
      bid.itemName,
      bid.item,
      bid.startingBid.toDouble,
      bid.maxBid.toDouble,
      bid.bidIncrement,
      currentTime
    )

  implicit val fmt: Format[RawBid] = Json.format[RawBid]
}
