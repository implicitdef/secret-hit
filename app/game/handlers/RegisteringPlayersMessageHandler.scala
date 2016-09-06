package game.handlers

import javax.inject.{Inject, Singleton}

import db.DbActions
import db.DbActions._
import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import game.Models.Role.Hitler
import game.Models.{PlayerId, Role}
import game.{Commands, Extras}
import slack.IncomingEvents.IncomingMessage
import slack.SlackClient
import utils._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RegisteringPlayersMessageHandler @Inject()(
  dbActions: DbActions, slackClient: SlackClient
)(implicit e: ExecutionContext) extends WithGameMessageHandler {

  import Commands._
  import Extras._

  override def handleMessage(team: SlackTeamRow, game: GameRow, m: IncomingMessage): ReadWriteAction[Unit] = {
    if (m.isPublic) {
      val playerId = PlayerId(m.user)
      if (m.is(Join) && ! game.gameState.players.exists(_.id == PlayerId)){
        for {
          name <- slackClient.fetchSlackUserName(team, playerId).asDBIOAction
          _ <- dbActions.updateGame(game.updateState(_.registerPlayer(playerId, name)))
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
          _ <- tellEachTheirRole(team, updatedGame).asDBIOAction
        } yield ()
      } else dunit
    } else dunit
  }

  private def listRegisteredPlayers(team: SlackTeamRow, game: GameRow): Future[Unit] =
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


  private def tellEachTheirRole(team: SlackTeamRow, game: GameRow): Future[Unit] = {
    val players = game.gameState.players
    val fascists = players.filter(_.role == Role.Fascist)
    val hitler = players.find(_.role == Role.Hitler).getOrElse(err("No Hitler found amongst players ??"))
    Future.traverse(game.gameState.players) { player =>
      val text = (player.role, fascists) match {
        case (Role.Liberal, _) => "You are a liberal."
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
