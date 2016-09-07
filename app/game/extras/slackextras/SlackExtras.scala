package game.extras.slackextras

import db.slicksetup.Tables.SlackTeamRow
import slack.SlackClient

trait SlackExtras {

  implicit class RichSlackClient(slackClient: SlackClient){
    def withTeam(team: SlackTeamRow): SlackWithTeamExtras =
      new SlackWithTeamExtras(slackClient, team)

  }




}
