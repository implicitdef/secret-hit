package game.extras.slackextras

import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import game.Models.PlayerId
import slack.SlackClient
import utils._

import scala.concurrent.{ExecutionContext, Future}

class SlackWithGameExtras(slackClient: SlackClient, team: SlackTeamRow, game: GameRow) {

  def withGame(newGame: GameRow) =
    new SlackWithGameExtras(slackClient, team, newGame)

  def tellInPrivate(playerId: PlayerId)(text: String): Future[Unit] =
    slackClient.post(team.slackApiToken, playerId.slackUserId, text)

  def tellEverybody(text: String): Future[Unit] =
    slackClient.post(team.slackApiToken, game.slackChannelId, text)

  def tellInPrivateOk(playerId: PlayerId): Future[Unit] =
    tellInPrivate(playerId)("OK")

  def tellEverybodyOk: Future[Unit] =
    tellEverybody("OK")

  //TODO virer tous les appels à ça (sauf à la creation du game) : ils sont stockés dans le game state !
  def fetchSlackUserName(id: PlayerId)
                        (implicit e: ExecutionContext): Future[String] =
    fetchSlackUserNames(Seq(id)).map(_(id))

  def fetchSlackUserNames(ids: Seq[PlayerId])
                         (implicit e: ExecutionContext): Future[Map[PlayerId, String]] =
    slackClient.listUsers(team.slackApiToken).map { members =>
      ids.map { id =>
        id -> members.find(_.id == id).getOrElse(err(s"Didn't found user $id")).name
      }.toMap
    }

}
