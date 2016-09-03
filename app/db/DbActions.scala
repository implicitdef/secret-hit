package db

import javax.inject._

import db.DbActions._
import db.slicksetup.Tables
import db.slicksetup.Tables._
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext

@Singleton
class DbActions @Inject()(
  databaseConfigProvider: DatabaseConfigProvider)(
  implicit ec: ExecutionContext
) {

  val dbConfig = databaseConfigProvider.get[JdbcProfile]
  private def run[R](a: DBIOAction[R, NoStream, Nothing]) = dbConfig.db.run(a)
  val api = dbConfig.driver.api
  import api._


  def getTeam(slackTeamId: String): ReadAction[Option[SlackTeamRow]] =
    for {
      teams <- SlackTeams.filter(_.slackTeamId === slackTeamId).result
      teamOpt = teams.headOption
    } yield teamOpt

  def getOrCreateTeam(team: SlackTeamRow): ReadWriteAction[Unit] =
    (SlackTeams += team).map(_ => ())

  def closeAllCurrentGamesOfTeam(team: SlackTeamRow): WriteAction[Unit] = {
    Games
      //.filter(_.slackTeamId === team.slackTeamId)
      //.filter(_.completedAt.isEmpty)
      .map(_.slackTeamId)
      .update("ff")
  }

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
  type ReadWriteTxAction[+R] = DBIOAction[R, NoStream, Effect.Read with Effect.Write with Effect.Transactional]


}
