package db.slicksetup



object Tables {
  import CustomDriver.api._

  case class SlackTeamRow(
    slackId: String,
    slackApiToken: String,
    slackName: String
  )

  class SlackTeam(tag: Tag) extends Table[SlackTeamRow](tag, "slack_teams") {
    def * = (slackId, slackApiToken, slackName) <> (SlackTeamRow.tupled, SlackTeamRow.unapply)
    val slackId: Rep[String] = column[String]("slack_id", O.PrimaryKey)
    val slackApiToken: Rep[String] = column[String]("slack_api_token")
    val slackName: Rep[String] = column[String]("slack_name")
  }
  lazy val SlackTeams = TableQuery[SlackTeam]
}
