package game.handlers

import javax.inject.{Inject, Singleton}

import db.DbActions
import db.DbActions._
import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import game.Extras
import game.Models.PlayerId
import slack.IncomingEvents.IncomingMessage
import slack.SlackClient
import utils._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ElectingMessageHandler @Inject()(
  dbActions: DbActions, slackClient: SlackClient
)(implicit e: ExecutionContext) extends WithGameMessageHandler {

  import Extras._
  import game.Commands._

  override def handleMessage(team: SlackTeamRow, game: GameRow, m: IncomingMessage): ReadWriteAction[Unit] = {
    val state = game.gameState
    val voterId = PlayerId(m.user)
    if (! m.isPublic && state.canVote(voterId)) {
      (m.text match {
        case Ja => Some(true)
        case Nein => Some(false)
        case _ => None
      }).map { vote =>


        for {
          updatedGame <- game.updateState(_.registerVote(voterId, vote))
          slack = slackClient.withTeam(team).withGame(updatedGame)
          _ <- slack.tellInPrivateOk(voterId)
          _ <- if (updatedGame.gameState.hasEverybodyVoted) {

          }
        } yield ()



      }



      val prezCandidate = game.gameState.president.getOrElse(err("No president found at step choosing chancellor"))
      if (m.user == prezCandidate.slackUserId){
        m.parseAsCommandWithDirectMention(Pick) match {
          case Some(mentionedId) =>
            if (game.gameState.eligiblePlayersForChancellor.exists(_.id == mentionedId)) {
              val updatedGame = game.updateState(_.setPickedChancellorAndStartElection(mentionedId))
               for {
                 _ <- dbActions.updateGame(updatedGame)
                 slack = slackClient.withTeam(team).withGame(updatedGame)
                 _ <- slack.tellEverybodyOk.action
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
                 }.action
                 _ <- Future.traverse(updatedGame.gameState.alivePlayers){ p =>
                   slack.tellInPrivate(p.id)(
                     "Please say ja or nein to vote for or against " +
                     "the current candidates for president and chancellor. " +
                     "Your vote will stay secret."
                  )
                 }.action
               } yield ()
            } else dunit
          case None => dunit
        }
      } else dunit
    } else dunit
  }

}
