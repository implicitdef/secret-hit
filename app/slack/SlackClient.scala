package slack

import javax.inject.Inject

import com.google.inject.Singleton
import play.api.libs.json.{JsValue, Json, Reads}
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

  def authTest(apiToken: String): Future[AuthTestResponse] =
    call("auth.test", "token" -> apiToken)(authTestResponseReads)

  def post(apiToken: String, channelOrUserId: String, text: String): Future[Unit] = {
    call[JsValue]("chat.postMessage",
      "token" -> apiToken,
      "channel" -> channelOrUserId,
      "text" -> text
    ).map(_ => ())
  }

  def listUsers(apiToken: String): Future[Seq[Member]] = {
    call[ListUsersResponse]("users.list", "token" -> apiToken)
      .map(_.members)
  }

  private def call[A : Reads](apiMethod: String, params: (String, String)*): Future[A] = {
    val url = s"https://slack.com/api/$apiMethod"
    wSClient
      .url(url)
      .withQueryString(params: _*)
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
  case class Member(
    id: String,
    name: String
  )
  case class ListUsersResponse(
    members: Seq[Member]
  )
  implicit val memberReads = Json.reads[Member]
  implicit val listUserResponseReads = Json.reads[ListUsersResponse]
  implicit val authTestResponseReads = Json.reads[AuthTestResponse]

}



