package game

import java.util.regex.Pattern
import javax.inject.{Inject, Singleton}

import db.DbActions
import db.DbActions._
import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import game.Models.Role.{Fascist, Hitler}
import game.Models.{GameStep, PlayerId, Role}
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
              _ <- listRegisteredPlayers(team, game).asDBIOAction
            } yield ()
          } else if (m.is(Leave) && game.gameState.players.exists(_.id == PlayerId)){
            for {
              _ <- dbActions.updateGame(game.updateState(_.removePlayer(playerId)))
              _ <- slackClient.tellEverybodyOk(team, game).asDBIOAction
              _ <- listRegisteredPlayers(team, game).asDBIOAction
            } yield ()
          } else if (m.is(StartGame) && game.gameState.hasGoodNumberOfPlayers){
            val updatedGame = game.updateState(_.startGame)
            for {
              _ <- dbActions.updateGame(updatedGame)
              state = updatedGame.gameState
              desc = s"Game started. There are ${state.players.size} players. " +
                s"${state.players.count(_.role == Role.Fascist)} are fascist(s). " +
                s"Another player is Hitler. The rest are liberals."
              _ <- slackClient.tellEverybody(team, updatedGame, desc).asDBIOAction
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
      _ <- if (game.gameState.hasGoodNumberOfPlayers) {
        slackClient.tellEverybody(team, game,
          s"You have a good number of players. Say $StartGame to start the game."
        )
      } else funit
    } yield ()

  def tellEachTheirRole(team: SlackTeamRow, game: GameRow): Future[Unit] = {
    val players = game.gameState.players
    val fascists = players.filter(_.role == Fascist)
    val hitler = players.find(_.role == Hitler).getOrElse(err("No Hitler found amongst players ??"))
    Future.traverse(game.gameState.players) { player =>
      val text = (player.role, fascists) match {
        case (Role.Liberal, _) => "You are a liberal."
        //TODO ici fetcher le name
        case (Role.Hitler, Seq(fascist)) =>
          s"You are Hitler. The fascist is ${fascist.slackUserName}. He knows who you are too."
        case (Role.Fascist, Seq(fascist)) =>
          s"You are the only fascist. ${hitler.slackUserName} is Hitler. He knows who you are too."
        case (Role.Hitler, multipleFascists) =>
          s"You are Hitler. There are ${multipleFascists.size} fascists out there that will help you. " +
          s"They know who you are."
        case (Role.Fascist, multipleFascists) =>
          s"You are a fascist. The fascists are the following : ${multipleFascists.map(_.slackUserName).mkString(", ")}. " +
          s"${hitler.slackUserName} is Hitler, but he doesn't know who the fascists are."
      }
      slackClient.tellInPrivate(team, player.id, text)
    }.map(_ => ())
  }

}



