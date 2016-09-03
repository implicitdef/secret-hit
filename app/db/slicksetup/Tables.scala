package db.slicksetup

import game.Models.GameState
import org.joda.time.DateTime


object Tables {

  import CustomDriver.api._
  // foreign key names are not used since we don't
  // create our schema through slick
  private val FkName = "dummy_foreign_key"


  //=============================

  case class SlackTeamRow(
    slackTeamId: String,
    slackApiToken: String
                         //TODO rajouter le bot id
  )
  class SlackTeam(tag: Tag) extends Table[SlackTeamRow](tag, "slack_teams") {
    def * = (slackTeamId, slackApiToken) <> (SlackTeamRow.tupled, SlackTeamRow.unapply)
    def slackTeamId: Rep[String] = column[String]("slack_team_id", O.PrimaryKey)
    def slackApiToken: Rep[String] = column[String]("slack_api_token")
  }
  lazy val SlackTeams = TableQuery[SlackTeam]

  //=============================

  case class GameRow(
                      slackTeamId: String,
                      gameId: Int,
                      slackChannelId: String,
                      gameState: GameState,
                      completedAt: Option[DateTime]
  )
  class Game(tag: Tag) extends Table[GameRow](tag, "games") {
    def * = (
      slackTeamId,
      gameId,
      slackChannelId,
      gameState,
      completedAt) <> (GameRow.tupled, GameRow.unapply)
    def slackTeamId: Rep[String] = column[String]("slack_team_id")
    def gameId: Rep[Int] = column[Int]("game_id", O.AutoInc, O.PrimaryKey)
    def slackChannelId: Rep[String] = column[String]("slack_channel_id")
    def gameState: Rep[GameState] = column[GameState]("game_state")
    def completedAt: Rep[Option[DateTime]] = column[Option[DateTime]]("completed_at")
    def slackTeam = foreignKey(FkName, slackTeamId, SlackTeams)(_.slackTeamId)
  }
  lazy val Games = TableQuery[Game]

}
