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
    def hasGoodNumberOfPlayers: Boolean =
      5 to 10 contains s.players.size
    def startGame =
      s
        .assignRolesAndShuffleOrder
        .setPresidentToHeadOfAlivePlayers
        .withStep(GameStep.Electing)
    def withStep(step: GameStep) =
      s.copy(step = step)
    def setPresidentToHeadOfAlivePlayers =
      s.copy(president = s.alivePlayers.headOption.map(_.id))
    def cyclingPlayers = {
      val head = s.players.headOption.getOrElse(err(s"Can't cycle, no player left"))
      s.copy(players = s.players.tail :+ head)
    }
    def assignRolesAndShuffleOrder = {
      val nbFascists = s.players.size match {
        case 5 | 6 => 1
        case 7 | 8 => 2
        case 9 | 10 => 3
      }
      val playersWithRoles =
        shuffle(s.players).zipWithIndex.map {
          case (p, 0) => p.copy(role = Role.Hitler)
          case (p, idx) if (1 to nbFascists).contains(idx) => p.copy(role = Role.Fascist)
          case (p, _) => p.copy(role = Role.Liberal)
        }
      s.copy(players = shuffle(playersWithRoles))
    }
    def alivePlayers: Seq[Player] =
      s.players.filterNot(p => s.dead.contains(p.id))
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
