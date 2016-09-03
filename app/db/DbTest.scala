package db

import javax.inject._

import play.api.db.slick.DatabaseConfigProvider
import slick.dbio._
import slick.driver.JdbcProfile
import utils._

import scala.concurrent.{ExecutionContext, Future}
@Singleton
class DbTest @Inject() (databaseConfigProvider: DatabaseConfigProvider)(
 implicit ec: ExecutionContext
){

  private val dbConfig = databaseConfigProvider.get[JdbcProfile]
  private def run[R](a: DBIOAction[R, NoStream, Nothing]) = dbConfig.db.run(a)

  def doTest: Future[Unit] =
    funit




}
