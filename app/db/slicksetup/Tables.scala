package db.slicksetup



object Tables {

  import CustomDriver.api._
  // foreign key names are not used since we don't
  // create our schema through slick
  private val FkName = "dummy_foreign_key"

  case class SlackTeamRow(
    slackTeamId: String,
    slackApiToken: String
  )
  class SlackTeam(tag: Tag) extends Table[SlackTeamRow](tag, "slack_teams") {
    def * = (slackTeamId, slackApiToken) <> (SlackTeamRow.tupled, SlackTeamRow.unapply)
    def slackTeamId: Rep[String] = column[String]("slack_team_id", O.PrimaryKey)
    def slackApiToken: Rep[String] = column[String]("slack_api_token")
  }
  lazy val SlackTeams = TableQuery[SlackTeam]

  case class SlackUserRow(
     slackTeamId: String,
     slackUserId: String
   )
  class SlackUser(tag: Tag) extends Table[SlackUserRow](tag, "slack_users") {
    def * = (slackTeamId, slackUserId) <> (SlackUserRow.tupled, SlackUserRow.unapply)
    def slackTeamId: Rep[String] = column[String]("slack_team_id")
    def slackUserId: Rep[String] = column[String]("slack_user_id", O.PrimaryKey)
    def slackTeam = foreignKey(FkName, slackTeamId, SlackTeams)(_.slackTeamId)
  }
  lazy val SlackUsers = TableQuery[SlackUser]

}
