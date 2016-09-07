package game

import javax.inject.{Inject, Singleton}

import db.DbActions
import db.DbActions._
import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import game.Models.GameStep
import game.handlers.{ChoosingChancellorMessageHandler, NoGameMessageHandler, RegisteringPlayersMessageHandler, WithGameMessageHandler}
import slack.IncomingEvents.IncomingMessage
import slack.SlackClient
import utils._

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class Engine @Inject()(
  dbActions: DbActions,
  slackClient: SlackClient,
  noGameMessageHandler: NoGameMessageHandler,
  registeringPlayersMessageHandler: RegisteringPlayersMessageHandler,
  choosingChancellorMessageHandler: ChoosingChancellorMessageHandler
){
  import dbActions.api._
  import Extras._

  def handleMessage(slackTeamId: String, m: IncomingMessage): ReadWriteTxAction[Unit] = {
    (for {
      teamOpt <- dbActions.getTeam(slackTeamId)
      team = teamOpt.getOrElse(err(s"Didn't found team $slackTeamId"))
      gameOpt <- dbActions.getCurrentGame(team)
      _ <- gameOpt
        .map { g =>
          if (m.isPublic && m.channel != g.slackChannelId) ignoringHandler
          else pickHandler(g.gameState.step).handleMessage(team, g, m)
        }
        .getOrElse(noGameMessageHandler.handleMessage(team, m))
    } yield ()).transactionally
  }

  private def pickHandler(gameStep: GameStep): WithGameMessageHandler = gameStep match {
    case GameStep.RegisteringPlayers => registeringPlayersMessageHandler
    case GameStep.ChoosingChancellor => choosingChancellorMessageHandler
      //TODO continue here
    case _ => ???
  }

  private def ignoringHandler = new WithGameMessageHandler {
    override def handleMessage(team: SlackTeamRow, game: GameRow, m: IncomingMessage): ReadWriteAction[Unit] =
      dunit
  }


}



