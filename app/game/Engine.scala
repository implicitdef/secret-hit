package game

import java.util.regex.Pattern
import javax.inject.{Inject, Singleton}

import db.DbActions
import db.DbActions._
import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import game.Models.{GameStep, PlayerId}
import slack.IncomingEvents.IncomingMessage
import slack.SlackClient
import slick.dbio._
import utils._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Engine @Inject()(dbActions: DbActions, slackClient: SlackClient){
  import Commands._
  import Extras._
  import dbActions.api._

  //TODO setup transaction and pin session
  def handleMessage(slackTeamId: String, m: IncomingMessage): ReadWriteAction[Unit] = {
    (for {
      team <- dbActions.getOrCreateTeam(slackTeamId)
      gameOpt <- dbActions.getCurrentGame(team)
      _ <- gameOpt
        .map(g => handleMessage(team, g, m))
        .getOrElse(handleMessageWithNoGame(team, m))
    } yield ()).transactionally
  }

  def handleMessageWithNoGame(team: SlackTeamRow, m: IncomingMessage): WriteAction[Unit] =
    if (m.isPublic && m.withDirectMention(team) && m.is(NewGame)) {
      for {
        game: GameRow <- dbActions.createGame(GameRow(
          slackTeamId = team.slackTeamId,
          gameId = -1,
          slackChannelId = m.channel,
          gameState = initialState,
          completedAt = None
        ))
        _ <- registerPlayerAndSave(team, game, PlayerId(m.user))
        _ <- DBIOAction.from(tellEverybody(team, "Game started, first user registered, please register the others"))
      } yield ()
    } else DBIOAction.successful(())

  def handleMessage(team: SlackTeamRow, game: GameRow, m: IncomingMessage): ReadWriteAction[Unit] = {
    game.gameState.step match {
      case GameStep.RegisteringPlayers =>
        ???
      case _ =>
        ???
    }





  }

  implicit class RichIncomingMessage(m: IncomingMessage){
    def isPublic: Boolean = m.channel.startsWith("C")
    def is(text: String): Boolean =
      m.text.toLowerCase.trim == text.toLowerCase.trim
    def withDirectMention(team: SlackTeamRow): Boolean = {
      val pattern = s"""<@${Pattern.quote(team.slackBotId)}(|[^>]+)?>""".r
      pattern.findFirstMatchIn(m.text).isDefined
    }
  }


  def registerPlayerAndSave(team: SlackTeamRow, game: GameRow, playerId: PlayerId): WriteAction[Unit] =
    for {
      name <- DBIOAction.from(fetchSlackUserName(team, playerId))
      updatedGame = game.copy(gameState = game.gameState.registerPlayer(playerId, name))
      _ <- dbActions.updateGame(updatedGame)
    } yield ()


  def tellInPrivate(team: SlackTeamRow, playerId: PlayerId, text: String): Future[Unit] =
    slackClient.post(team.slackApiToken, playerId.slackUserId, text)
  def tellEverybody(team: SlackTeamRow, game: GameRow, text: String): Future[Unit] =
    slackClient.post(team.slackApiToken, game.slackChannelId, text)
  def fetchSlackUserName(team: SlackTeamRow, id: PlayerId): Future[String] =
    fetchSlackUserNames(team, Seq(id)).map(_(id))
  def fetchSlackUserNames(team: SlackTeamRow, ids: Seq[PlayerId]): Future[Map[PlayerId, String]] =
    slackClient.listUsers(team.slackApiToken).map { members =>
      ids.map { id =>
        id -> members.find(_.id == id).getOrElse(err(s"Didn't found user $id")).name
      }.toMap
    }


















}



