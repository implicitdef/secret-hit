package db

import javax.inject._

import db.DbActions._
import db.slicksetup.Tables.{Game, GameRow, SlackTeamRow}
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.driver.JdbcProfile

import scala.concurrent.Future

@Singleton
class DbActions @Inject()(databaseConfigProvider: DatabaseConfigProvider) {

  private val dbConfig = databaseConfigProvider.get[JdbcProfile]
  private def run[R](a: DBIOAction[R, NoStream, Nothing]) = dbConfig.db.run(a)
  val api = dbConfig.driver.api
  import api._


  def getOrCreateTeam(teamSlackId: String): ReadWriteAction[SlackTeamRow] = ???

  def closeAllCurrentGamesOfTeam(team: SlackTeamRow): WriteAction[Unit] = ???

  def getCurrentGame(team: SlackTeamRow): ReadAction[Option[GameRow]] = ???

  // voir s'il faut recuperer un nouveau game ?
  // normalement oui pour l'id
  def createGame(game: GameRow): WriteAction[GameRow] = ???

  def updateGame(game: GameRow): WriteAction[Unit] = ???

  def closeGame(game: GameRow): WriteAction[Unit] = ???

}

object DbActions {
  type WriteAction[+R] = DBIOAction[R, NoStream, Effect.Write]
  type ReadAction[+R] = DBIOAction[R, NoStream, Effect.Read]
  type ReadWriteAction[+R] = DBIOAction[R, NoStream, Effect.Read with Effect.Write]


}
