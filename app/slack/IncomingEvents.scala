package slack

import play.api.libs.json._

object IncomingEvents {

  sealed trait IncomingEvent

  case class IncomingMessage(
    channel: String,
    user: String,
    text: String
  ) extends IncomingEvent

  case class UrlVerificationChallenge(
    challenge: String
  ) extends IncomingEvent

  case object RateLimited extends IncomingEvent

  case class Other(`type`: String) extends IncomingEvent

  implicit val format1 = Json.format[IncomingMessage]
  implicit val format2 = Json.format[UrlVerificationChallenge]
  implicit val reads = new Reads[IncomingEvent]{
    override def reads(o: JsValue): JsResult[IncomingEvent] =
      (o \ "type").validate[String].flatMap {
        case "message.channels" | "message.groups" | "message.im" => o.validate[IncomingMessage]
        case "url_verification" => o.validate[UrlVerificationChallenge]
        case "app_rate_limited" => JsSuccess(RateLimited)
        case other => JsSuccess(Other(other))
      }
  }





}
