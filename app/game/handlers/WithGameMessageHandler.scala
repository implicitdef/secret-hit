package game.handlers

import db.DbActions._
import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import slack.IncomingEvents.IncomingMessage

trait WithGameMessageHandler {

  def handleMessage(team: SlackTeamRow, game: GameRow, m: IncomingMessage): ReadWriteAction[Unit]

}
