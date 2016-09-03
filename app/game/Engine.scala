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
  def handleMessage(slackTeamId: String, m: IncomingMessage): ReadWriteTxAction[Unit] = {
    (for {
      teamOpt <- dbActions.getTeam(slackTeamId)
      team = teamOpt.getOrElse(err(s"Didn't found team $slackTeamId"))
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
        _ <- DBIOAction.from(slackClient.tellEverybody(
          team, game, "Game started, first user registered, please register the others"
        ))
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

  def registerPlayerAndSave(team: SlackTeamRow, game: GameRow, playerId: PlayerId): WriteAction[Unit] =
    for {
      name <- DBIOAction.from(slackClient.fetchSlackUserName(team, playerId))
      updatedGame = game.copy(gameState = game.gameState.registerPlayer(playerId, name))
      _ <- dbActions.updateGame(updatedGame)
    } yield ()

  def listRegisteredPlayers(team: SlackTeamRow, game: GameRow): Future[Unit] =
    for {
      names <- slackClient.fetchSlackUserNames(team, game.gameState.players.map(_.id))
      _ <- slackClient.post(
        team.slackApiToken,
        game.slackChannelId,
        s"Current players ${game.gameState.players.size}: ${names.map("@" + _).mkString(", ")}"
      )
    } yield ()


}



