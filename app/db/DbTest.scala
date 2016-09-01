package db

import javax.inject._

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.concurrent.Future
@Singleton
class DbTest @Inject() (databaseConfigProvider: DatabaseConfigProvider){

  val dbConfig = databaseConfigProvider.get[JdbcProfile]
  import dbConfig.driver.api._


  def getUsers: Future[Seq[Unit]] = ???
    //dbConfig.db.run {
    //  Teams.filter(_.name === name).result
    //}


}
