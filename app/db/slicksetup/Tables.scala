package db.slicksetup

import game.Models.GameState
import org.joda.time.DateTime


object Tables {

  case class SlackTeamRow(
    slackTeamId: String,
    slackApiToken: String,
    slackBotId: String
  )

  case class GameRow(
    slackTeamId: String,
    gameId: Int,
    slackChannelId: String,
    gameState: GameState,
    completedAt: Option[DateTime]
  )
}
