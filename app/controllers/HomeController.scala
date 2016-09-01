package controllers

import javax.inject._

import play.api.Logger
import utils._
import play.api.mvc._
import slack.IncomingEvents._
import slack.SlackClient

import scala.concurrent.ExecutionContext
@Singleton
class HomeController @Inject() (
                               slackClient: SlackClient
)(implicit ec: ExecutionContext) extends Controller {

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

  def test = Action.async { req =>
    slackClient.authTest.map { res =>
      Logger.info(s">> ${res}")
      Ok("ok")
    }
  }


}
