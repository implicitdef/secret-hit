package game.handlers

import javax.inject.{Inject, Singleton}

import db.DbActions
import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import slack.IncomingEvents.IncomingMessage
import slack.SlackClient
import utils._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ChoosingChancellorMessageHandler @Inject()(
  dbActions: DbActions, slackClient: SlackClient
)(implicit e: ExecutionContext) extends WithGameMessageHandler {

  import game.Commands._

  override def handleMessage(team: SlackTeamRow, game: GameRow, m: IncomingMessage): Future[Unit] = {
    if (m.isPublic) {
      val prezCandidate = game.gameState.president.getOrElse(err("No president found at step choosing chancellor"))
      if (m.user == prezCandidate.slackUserId){
        m.parseAsCommandWithDirectMention(Pick) match {
          case Some(mentionedId) =>
            if (game.gameState.eligiblePlayersForChancellor.exists(_.id == mentionedId)) {
              val updatedGame = game.updateState(_.setPickedChancellorAndStartElection(mentionedId))
               for {
                 _ <- dbActions.updateGame(updatedGame)
                 slack = slackClient.withTeam(team).withGame(updatedGame)
                 _ <- slack.tellEverybodyOk
                 prezCandidate = updatedGame.gameState.presidentName.getOrElse(err(
                   "No president candidate found, but we just started the election"
                 ))
                 chancellorCandidate = updatedGame.gameState.chancellorName.getOrElse(err(
                   "No chancellor candidate found, but we just started the election"
                 ))
                 _ <- slack.tellEverybody {
                   s"The election is starting. $prezCandidate and $chancellorCandidate are " +
                   s"candidates for president and chancellor, respectively. Should we elect them ? " +
                   s"Each player, including the candidates, must vote in secret, by sending a " +
                   s"""private message to myself saying ja or nein."""
                 }
                 _ <- Future.traverse(updatedGame.gameState.alivePlayers){ p =>
                   slack.tellInPrivate(p.id)(
                     "Please say ja or nein to vote for or against " +
                     "the current candidates for president and chancellor. " +
                     "Your vote will stay secret."
                  )
                 }
               } yield ()
            } else funit
          case None => funit
        }
      } else funit
    } else funit
  }

}
