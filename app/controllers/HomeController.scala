package controllers

import javax.inject._

import play.api.Logger
import utils._
import play.api.mvc._
import slack.IncomingEvents._
@Singleton
class HomeController @Inject() () extends Controller {

  def index = Action {
    Ok("ok")
  }

  def slack = Action.async(parse.json) { req =>
    req.body.validate[IncomingEvent].map {
      case e: UrlVerificationChallenge =>
        fsucc(Ok(e.challenge))
      case RateLimited =>
        Logger.warn(s"We are being rate limited by Slack")
        fsucc(Ok("ok"))
      case other: Other =>
        Logger.info(s"Unhandled message ${other.`type`}")
        fsucc(Ok("ok"))
      case im: IncomingMessage =>
        //TODO handle
        fsucc(Ok("ok"))
    }.getOrElse {
      fsucc(BadRequest("unrecognized json"))
    }
  }


}
