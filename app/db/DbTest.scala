package db

import javax.inject._

import db.slicksetup.Tables._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio._
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class DbTest @Inject() (databaseConfigProvider: DatabaseConfigProvider)(
 implicit ec: ExecutionContext
){

  private val dbConfig = databaseConfigProvider.get[JdbcProfile]
  private def run[R](a: DBIOAction[R, NoStream, Nothing]) = dbConfig.db.run(a)

  import dbConfig.driver.api._

  def doTest: Future[Unit] =
    run {
      for {
        _ <- SlackUsers.delete
        _ <- SlackTeams.delete
        _ <- Games.delete
        _ <- SlackTeams += SlackTeamRow("team1", "slackApiToken")
        _ <- SlackTeams += SlackTeamRow("team2", "slackApiToken2")
        _ <- SlackUsers += SlackUserRow("team1", "userA")
        _ <- SlackUsers += SlackUserRow("team1", "userB")
        _ <- SlackUsers += SlackUserRow("team1", "userC")
        _ <- Games += GameRow("team1", -1, "slackChannel", 0)
        teams <- SlackTeams.result
        users <- SlackUsers.result
      } yield (teams, users)
    }.map { case (teams, users) =>
      Logger.info(s"Teams ${teams.size}")
      teams.foreach {t => Logger.info(t.toString)}
      Logger.info(s"Users ${users.size}")
      users.foreach {t => Logger.info(t.toString)}
    }.recover {
      case t => Logger.error("dbTest failed", t)
    }




}
