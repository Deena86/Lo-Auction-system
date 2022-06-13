package com.lo.auctionsystem.controllers

import com.lo.auctionsystem.models.{RawAuctionItem, RawBid}
import com.lo.auctionsystem.service.AuctionService
import play.api.libs.json.{JsSuccess, JsValue}

class AuctionController(auctionService: AuctionService) {

  def processItemFromJson(item: JsValue) =
    parseItem(item)

  def printCurrentListings() =
    auctionService.getCurrentListings

  private def parseItem(item: JsValue) =
    item.validate[RawAuctionItem] match {
      case JsSuccess(i, _) => auctionService.addNewAuctionItems(i)
      case _ =>
        item.validate[RawBid] match {
          case JsSuccess(b, _) => auctionService.receiveBidOffer(b)
          case _               => println("Received Json item did not match Item and Bid schema. Ignoring error")
        }
    }
}
