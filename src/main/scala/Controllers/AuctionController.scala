package Controllers

import play.api.libs.json.{JsValue, Json}
import service.AuctionService

import scala.concurrent.{ExecutionContext, Future}

class AuctionController(auctionService: AuctionService, private implicit val ec: ExecutionContext) {

  // GET /amplify/posts
  def startAuction(item: JsValue) =
    for {
      memberId <- ControllerUtils.extractAuthedMemberId(request).toFuture
      amplifyPosts <- amplifyApi.getAmplifyPosts(
        topic           = topic,
        pageSize        = pageSize.map(_.toInt),
        next            = next,
        previous        = previous,
        lastFetchedDate = lastFetchedDate,
        xAsMemberId     = Some(memberId)
      )
    } yield {
      Ok(
        Json.toJson(
          MobileApiResponse(
            errors = Set(),
            data   = Json.toJson(AmplifyPostsResponse.fromTotemApiResponse(amplifyPosts))
          )
        )
      )
    }
}
