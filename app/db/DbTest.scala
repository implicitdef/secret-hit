package db

import javax.inject._

import db.models.Tables._
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.{DBIOAction, NoStream}
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
        _ <- Team += TeamRow(AutoIncr, "name", Some("funk"))
        size <- Team.size.result
      } yield size
    }.map { size =>
      Logger.info(s"Teams $size")
    }.recover {
      case t => Logger.error("dbTest failed", t)
    }




}
