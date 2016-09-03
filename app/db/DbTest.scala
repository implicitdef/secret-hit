package db

import javax.inject._

import db.slicksetup.Tables._
import game.Models
import game.Models.{GameState, PlayerId, Policy}
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
        _ <- Games.delete
        _ <- SlackTeams.delete
        _ <- SlackTeams += SlackTeamRow("team1", "slackApiToken")
        _ <- SlackTeams += SlackTeamRow("team2", "slackApiToken2")
        teams <- SlackTeams.result
        games <- Games.result
      } yield (teams, games)
    }.map { case (teams, games) =>
      Logger.info(s"Teams ${teams.size}")
      teams.foreach {t => Logger.info(t.toString)}
      Logger.info(s"Games ${games.size}")
      games.foreach {t => Logger.info(t.toString)}
    }.recover {
      case t => Logger.error("dbTest failed", t)
    }




}
