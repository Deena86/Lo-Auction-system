package com.lo.auctionsystem.models

import org.joda.time.DateTime
import play.api.libs.json.{Format, Json}

case class RawAuctionItem(
  id: String,
  name: String,
  `type`: String,
  description: Option[String],
  timeOfAuction: Int
)

case class AuctionItem(
  id: String,
  name: String,
  description: Option[String],
  timeOfAuction: Int,
  startTime: DateTime,
  endTime: DateTime
)

object RawAuctionItem {
  val currentTime: DateTime = DateTime.now()

  def toAuctionItem(item: RawAuctionItem): AuctionItem = {
    val auctionDuration = item.timeOfAuction
    AuctionItem(
      item.id,
      item.name,
      item.description,
      auctionDuration,
      currentTime,
      currentTime.plusSeconds(auctionDuration)
    )
  }

  implicit val fmt: Format[RawAuctionItem] = Json.format[RawAuctionItem]
}
