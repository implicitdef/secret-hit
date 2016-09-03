package slack

import javax.inject.Inject

import com.google.inject.Singleton
import play.api.libs.json.{Json, Reads}
import play.api.libs.ws.WSClient
import setup.Settings
import slack.SlackClient._
import utils._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SlackClient @Inject() (
  settings: Settings,
  wSClient: WSClient
)(implicit ec: ExecutionContext) {

  def authTest: Future[AuthTestResponse] =
    call("auth.test")(authTestResponseReads)




  private def call[A : Reads](apiMethod: String, params: (String, String)*): Future[A] = {
    val url = s"https://slack.com/api/$apiMethod"
    wSClient
      .url(url)
      .withQueryString(params: _*)
      .withQueryString("token" -> settings.slackApiToken)
      .get
      .map { res =>
        if (res.isGoodStatus) {
          val json = res.json
          if ((json \ "ok").as[Boolean]) json.as[A]
          else err(s"KO response from $url : $json")
        } else err(s"Got ${res.status} from $url")
      }
  }

}

object SlackClient {

  case class AuthTestResponse(
    team: String,
    team_id: String,
    user_id: String
  )
  implicit val authTestResponseReads = Json.reads[AuthTestResponse]

}



