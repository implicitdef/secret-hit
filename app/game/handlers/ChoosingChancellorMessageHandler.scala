package game.handlers

import javax.inject.{Inject, Singleton}

import db.DbActions
import db.DbActions._
import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import game.Extras
import slack.IncomingEvents.IncomingMessage
import slack.SlackClient
import utils._

import scala.concurrent.ExecutionContext

@Singleton
class ChoosingChancellorMessageHandler @Inject()(
  dbActions: DbActions, slackClient: SlackClient
)(implicit e: ExecutionContext) extends WithGameMessageHandler {

  import Extras._

  override def handleMessage(team: SlackTeamRow, game: GameRow, m: IncomingMessage): ReadWriteAction[Unit] = {
    val slack = slackClient.withTeam(team).withGame(game)
    if (m.isPublic) {
      val prezCandidate = game.gameState.president.getOrElse(err("No president found at step choosing chancellor"))
      if (
        m.user == prezCandidate.slackUserId &&
        game.gameState.players.map("@" + _.slackUserName).contains(m.text)
      ){
        val name = m.text.drop(1)
        val selectedPlayer = game.gameState.players.find(_.slackUserName == name).getOrElse(err(
          s"Didn't found player $name, even though we just checked that it was a player"
        ))
        if (! game.gameState.isAlive(selectedPlayer.id))
          slack.tellEverybody(s"$name can't be picked for chancellor, he's deceased")
          // other rules : can't be yourself, can't be last chancellor/president (or can he ? check rules)
        else if  // TODO continue
      }
    } else dunit
  }

}
