package game.handlers

import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import slack.IncomingEvents.IncomingMessage

import scala.concurrent.Future

trait WithGameMessageHandler {

  def handleMessage(team: SlackTeamRow, game: GameRow, m: IncomingMessage): Future[Unit]

}
