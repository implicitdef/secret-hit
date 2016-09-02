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
        _ <- SlackTeams += SlackTeamRow("slackId", "slackApiToken", "slackName")
        _ <- SlackTeams += SlackTeamRow("slackId2", "slackApiToken", "slackName")
        teams <- SlackTeams.result
      } yield teams
    }.map { teams =>
      Logger.info(s"Teams ${teams.size}")
      teams.foreach {t => Logger.info(t.toString)}
    }.recover {
      case t => Logger.error("dbTest failed", t)
    }




}
