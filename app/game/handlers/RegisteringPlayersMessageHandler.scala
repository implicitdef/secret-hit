package game.handlers

import javax.inject.{Inject, Singleton}

import db.DbActions
import db.DbActions._
import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import game.Models.{PlayerId, Role}
import game.extras.slackextras.SlackWithGameExtras
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
    val slack = slackClient.withTeam(team).withGame(game)
    if (m.isPublic) {
      val playerId = PlayerId(m.user)
      if (m.is(Join) && ! game.gameState.players.exists(_.id == PlayerId)){
        for {
          name <- slack.fetchSlackUserName(playerId).action
          updatedGame = game.updateState(_.registerPlayer(playerId, name))
          _ <- dbActions.updateGame(updatedGame)
          updatedSlack = slack.withGame(updatedGame)
          _ <- updatedSlack.tellEverybodyOk.action
          _ <- tellRegisteredPlayers(updatedSlack, updatedGame).action
        } yield ()
      } else if (m.is(Leave) && game.gameState.players.exists(_.id == PlayerId)){
        val updatedGame = game.updateState(_.removePlayer(playerId))
        for {
          _ <- dbActions.updateGame(updatedGame)
          updatedSlack = slack.withGame(updatedGame)
          _ <- slack.tellEverybodyOk.action
          _ <- tellRegisteredPlayers(updatedSlack, updatedGame).action
        } yield ()
      } else if (m.is(StartGame) && game.gameState.hasGoodNumberOfPlayers){
        val updatedGame = game.updateState(_.startGame)
        for {
          _ <- dbActions.updateGame(updatedGame)
          state = updatedGame.gameState
          updatedSlack = slack.withGame(updatedGame)
          _ <- updatedSlack.tellEverybody {
            s"Game started. There are ${state.players.size} players. " +
            s"${state.players.count(_.role == Role.Fascist)} are fascist(s). " +
            s"Another player is Hitler. The rest are liberals."
          }.action
          _ <- tellEachTheirRole(updatedSlack, updatedGame).action
          _ <- updatedSlack.tellEverybody(s"You will play in the following " +
            s"order : ${state.players.map(_.slackUserName).commas}").action
          prezCandidateName = updatedGame.gameState.presidentName.getOrElse(err(
            "No president found despite having started the game"
          ))
          _ <- updatedSlack.tellEverybody("The first candidate" +
            s" for presidency is therefore $prezCandidateName").action
          _ <- updatedSlack.tellEverybody(s"$prezCandidateName, please choose " +
            s"""your chancellor to run with you. Just say "pick @YOUR_CHOICE_FOR_CHANCELLOR.""").action
          _ <- updatedSlack.tellEverybody(s"The possible choices for chancellor are : " +
            updatedGame.gameState.eligiblePlayersForChancellor.map(_.slackUserName).commas).action
        } yield ()
      } else dunit
    } else dunit
  }

  //TOOD enregistrer les @ direct en DB : pas besoin des noms sans les @ !


  private def tellRegisteredPlayers(slack: SlackWithGameExtras, game: GameRow): Future[Unit] = {
    val players = game.gameState.players
    for {
      _ <- slack.tellEverybody {
        s"Current players ${players.size}: ${players.map(_.slackUserName).commas}"
      }
      _ <- if (game.gameState.hasGoodNumberOfPlayers) {
        slack.tellEverybody(
          s"You have a good number of players. Say $StartGame to start the game."
        )
      } else funit
    } yield ()
  }

  private def tellEachTheirRole(slack: SlackWithGameExtras, game: GameRow): Future[Unit] = {
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
      slack.tellInPrivate(player.id)(text)
    }.map(_ => ())
  }
}
