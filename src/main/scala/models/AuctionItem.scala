package models

import org.joda.time.DateTime
import org.joda.time.format.{DateTimeFormat, ISODateTimeFormat}
import play.api.libs.json.{Format, Json}

case class AuctionItem(
  id: String,
  name: String,
  `type`: String,
  description: Option[String],
  timeOfAuction: Int
)

case class AuctionItemWithTime(
  id: String,
  name: String,
  `type`: String,
  description: Option[String],
  timeOfAuction: Int,
  startTime: DateTime
)

object AuctionItem {

  def toAuctionItemWithDate(item: AuctionItem) =
    AuctionItemWithTime(item.id, item.name, item.`type`, item.description, item.timeOfAuction, DateTime.now())

  implicit val fmt: Format[AuctionItem] = Json.format[AuctionItem]
}

object DateParser {
  private val legacyFormat = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")
  private val iso8601Format = ISODateTimeFormat.dateTime()

  def parse(s: String): DateTime =
    try {
      legacyFormat.withZoneUTC().parseDateTime(s)
    } catch {
      case _: IllegalArgumentException =>
        // Instead of assuming UTC, retain the TZ in the date string.
        iso8601Format.parseDateTime(s)
    }

  def serialize(d: DateTime): String = legacyFormat.print(d)
}
