package game.extras.slackextras

import db.slicksetup.Tables.{GameRow, SlackTeamRow}
import slack.SlackClient

class SlackWithTeamExtras(slackClient: SlackClient, team: SlackTeamRow) {

  def withGame(game: GameRow): SlackWithGameExtras =
    new SlackWithGameExtras(slackClient, team, game)

}
