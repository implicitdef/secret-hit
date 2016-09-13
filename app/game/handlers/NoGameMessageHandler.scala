package game.handlers

import javax.inject.{Inject, Singleton}

import db.DbActions
import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import game.Models.{GameState, GameStep, PlayerId, Policy}
import game.Commands
import slack.IncomingEvents.IncomingMessage
import slack.SlackClient

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random._
import utils._

@Singleton
class NoGameMessageHandler @Inject()(
  dbActions: DbActions, slackClient: SlackClient
)(implicit e: ExecutionContext){
  import Commands._

  def handleMessage(team: SlackTeamRow, m: IncomingMessage): Future[Unit] =
    if (m.isPublic && m.withDirectMention(team) && m.is(NewGame)) {
      for {
        game: GameRow <- dbActions.createGame(GameRow(
          slackTeamId = team.slackTeamId,
          gameId = -1,
          slackChannelId = m.channel,
          gameState = initialState,
          completedAt = None
        ))
        slack = slackClient.withTeam(team).withGame(game)
        firstPlayerId = PlayerId(m.user)
        name <- slack.fetchSlackUserName(firstPlayerId)
        updatedGame = game.updateState(_.registerPlayer(firstPlayerId, name))
        _ <- dbActions.updateGame(updatedGame)
        _ <- slack.withGame(updatedGame).tellEverybody {
          "Game started, first user registered, please register the others"
        }
      } yield ()
    } else funit


  private def initialState = GameState(
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
}
