package game

import java.util.regex.Pattern
import javax.inject.{Inject, Singleton}

import db.DbActions
import db.DbActions._
import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import game.Models.{GameStep, PlayerId}
import play.api.Logger
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

  //// PRIVATE

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
        _ <- slackClient.tellEverybody(
          team, game, "Game started, first user registered, please register the others"
        ).asDBIOAction
      } yield ()
    } else DBIOAction.successful(())

  def handleMessage(team: SlackTeamRow, game: GameRow, m: IncomingMessage): ReadWriteAction[Unit] = {
    game.gameState.step match {
      case GameStep.RegisteringPlayers =>
        if (m.isPublic) {
          val playerId = PlayerId(m.user)
          if (m.is(Join) && ! game.gameState.players.exists(_.id == PlayerId)){
            for {
              _ <- registerPlayerAndSave(team, game, playerId)
              _ <- slackClient.tellEverybodyOk(team, game).asDBIOAction
            } yield ()
          } else if (m.is(Leave) && game.gameState.players.exists(_.id == PlayerId)){
            for {
              _ <- dbActions.updateGame(game.updateState(_.removePlayer(playerId)))
              _ <- slackClient.tellEverybodyOk(team, game).asDBIOAction
            } yield ()
          } else dunit
        } else dunit
      case _ =>
        ???
    }
  }

  def registerPlayerAndSave(team: SlackTeamRow, game: GameRow, playerId: PlayerId): WriteAction[Unit] =
    for {
      name <- slackClient.fetchSlackUserName(team, playerId).asDBIOAction
      _ <- dbActions.updateGame(game.updateState(_.registerPlayer(playerId, name)))
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



