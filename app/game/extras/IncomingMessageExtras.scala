package game.extras

import java.util.regex.Pattern

import db.slicksetup.Tables.SlackTeamRow
import game.Models.PlayerId
import slack.IncomingEvents.IncomingMessage

trait IncomingMessageExtras {


  implicit class RichIncomingMessage(m: IncomingMessage){
    def isPublic: Boolean = m.channel.startsWith("C")
    def is(text: String): Boolean =
      m.text.toLowerCase.trim == text.toLowerCase.trim
    def withDirectMention(team: SlackTeamRow): Boolean = {
      val pattern = s"""<@${Pattern.quote(team.slackBotId)}(|[^>]+)?>""".r
      pattern.findFirstMatchIn(m.text).isDefined
    }
    def parseAsCommandWithDirectMention(command: String): Option[PlayerId] = {
      val ThePattern = s"""${Pattern.quote(command)} <@([^>|]+)(|[^>]+)?>""".r
      m.text match {
        case ThePattern(id, _) => Some(PlayerId(id))
        case _ => None
      }
    }
  }
}
