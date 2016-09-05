package game

import java.util.regex.Pattern

import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import game.Models.{GameState, GameStep, Player, PlayerId, Policy, Role}
import slack.IncomingEvents.IncomingMessage
import slack.SlackClient
import utils._

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random._

object Extras {

  def initialState = GameState(
    GameStep.RegisteringPlayers,
    stack = shuffle(Seq.fill(6)(Policy.Liberal) ++ Seq.fill(11)(Policy.Fascist)),
    Nil,
    Nil,
    Nil,
    Nil,
    Map.empty,
    None,
    None,
    None,
    None,
    None,
    Nil,
    Nil
  )

  implicit class RichIncomingMessage(m: IncomingMessage){
    def isPublic: Boolean = m.channel.startsWith("C")
    def is(text: String): Boolean =
      m.text.toLowerCase.trim == text.toLowerCase.trim
    def withDirectMention(team: SlackTeamRow): Boolean = {
      val pattern = s"""<@${Pattern.quote(team.slackBotId)}(|[^>]+)?>""".r
      pattern.findFirstMatchIn(m.text).isDefined
    }
  }

  implicit class RichState(s: GameState){
    def registerPlayer(playerId: PlayerId, slackUserName: String): GameState =
      s.copy(players = s.players :+ Player(
          playerId,
          slackUserName,
          // we will assign the roles when the game starts
          Role.Liberal
      ))
    def removePlayer(playerId: PlayerId): GameState =
      s.copy(players = s.players.filterNot(_.id == playerId))
  }

  implicit class RichGameRow(g: GameRow){
    def updateState(func: GameState => GameState) =
      g.copy(gameState = func(g.gameState))
  }


  implicit class RichSlackClient(slackClient: SlackClient){
    def tellInPrivate(team: SlackTeamRow, playerId: PlayerId, text: String): Future[Unit] =
      slackClient.post(team.slackApiToken, playerId.slackUserId, text)
    def tellEverybody(team: SlackTeamRow, game: GameRow, text: String): Future[Unit] =
      slackClient.post(team.slackApiToken, game.slackChannelId, text)
    def tellInPrivateOk(team: SlackTeamRow, playerId: PlayerId): Future[Unit] =
      tellInPrivate(team, playerId, "OK")
    def tellEverybodyOk(team: SlackTeamRow, game: GameRow): Future[Unit] =
      tellEverybody(team, game, "OK")
    def fetchSlackUserName(team: SlackTeamRow, id: PlayerId)
                          (implicit e: ExecutionContext): Future[String] =
      fetchSlackUserNames(team, Seq(id)).map(_(id))
    def fetchSlackUserNames(team: SlackTeamRow, ids: Seq[PlayerId])
                           (implicit e: ExecutionContext): Future[Map[PlayerId, String]] =
      slackClient.listUsers(team.slackApiToken).map { members =>
        ids.map { id =>
          id -> members.find(_.id == id).getOrElse(err(s"Didn't found user $id")).name
        }.toMap
      }
  }






}
