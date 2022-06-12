package models

import play.api.libs.json.{Format, Json}

case class Bid(
  id: String,
  name: String,
  `type`: String,
  itemName: Option[String],
  item: String,
  startingBid: Int,
  maxBid: Int,
  bidIncrement: Int
)

object Bid {
  implicit val fmt: Format[Bid] = Json.format[Bid]
}
