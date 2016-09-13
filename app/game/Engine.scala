package game

import javax.inject.{Inject, Singleton}

import db.DbActions
import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import game.Models.GameStep
import game.handlers._
import slack.IncomingEvents.IncomingMessage
import slack.SlackClient
import utils._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class Engine @Inject()(
  dbActions: DbActions,
  slackClient: SlackClient,
  noGameMessageHandler: NoGameMessageHandler,
  registeringPlayersMessageHandler: RegisteringPlayersMessageHandler,
  choosingChancellorMessageHandler: ChoosingChancellorMessageHandler,
  electingMessageHandler: ElectingMessageHandler
){

  def handleMessage(slackTeamId: String, m: IncomingMessage): Future[Unit] = {
    dbActions.inTransaction {
      for {
        teamOpt <- dbActions.getTeam(slackTeamId)
        team = teamOpt.getOrElse(err(s"Didn't found team $slackTeamId"))
        gameOpt <- dbActions.getCurrentGame(team)
        _ <- gameOpt
          .map { g =>
            {
              if (m.isPublic && m.channel != g.slackChannelId) ignoringHandler
              else pickHandler(g.gameState.step)
            }.handleMessage(team, g, m)
          }
          .getOrElse(noGameMessageHandler.handleMessage(team, m))
      } yield ()
    }
  }

  private def pickHandler(gameStep: GameStep): WithGameMessageHandler = gameStep match {
    case GameStep.RegisteringPlayers => registeringPlayersMessageHandler
    case GameStep.ChoosingChancellor => choosingChancellorMessageHandler
    case GameStep.Electing => electingMessageHandler
      //TODO continue here
    case _ => ???
  }

  private def ignoringHandler: WithGameMessageHandler = new WithGameMessageHandler {
    override def handleMessage(team: SlackTeamRow, game: GameRow, m: IncomingMessage): Future[Unit] =
      funit
  }


}



