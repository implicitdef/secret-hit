package db

import java.sql.Timestamp
import javax.inject._

import db.slicksetup.Enums.Genders
import db.slicksetup.Tables._
import org.joda.time.DateTime
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio._
import slick.driver.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}
import scala.util.control.NonFatal
@Singleton
class DbTest @Inject() (databaseConfigProvider: DatabaseConfigProvider)(
 implicit ec: ExecutionContext
){

  private val dbConfig = databaseConfigProvider.get[JdbcProfile]
  private def run[R](a: DBIOAction[R, NoStream, Nothing]) = dbConfig.db.run(a)
  // auto-incremented columns values are ignored when inserted
  // so we just use this dummy value
  private val AutoIncr = -1

  import dbConfig.driver.api._

  def doTest: Future[Unit] =
    run {
      for {
        _ <- Teams += TeamRow(AutoIncr, "name", Genders.male, DateTime.now, Some("funk"))
        _ <- Teams += TeamRow(AutoIncr, "name", Genders.female, DateTime.now.minusYears(10), Some("funk"))
        teams <- Teams.result
      } yield teams
    }.map { teams =>
      Logger.info(s"Teams ${teams.size}")
      teams.foreach {t => Logger.info(t.toString)}
    }.recover {
      case t => Logger.error("dbTest failed", t)
    }




}
