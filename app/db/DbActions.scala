package db

import javax.inject.Inject

import db.DbActions._
import db.slicksetup.Tables.{Game, GameRow, SlackTeamRow}
import slick.dbio.{DBIOAction, Effect, NoStream}

import scala.concurrent.Future

@Singleton
class DbActions @Inject()() {


  def getOrCreateTeam(teamSlackId: String): ReadWriteAction[SlackTeamRow] = ???

  def closeAllCurrentGamesOfTeam(team: SlackTeamRow): WriteAction[Unit] = ???

  def getCurrentGame(team: SlackTeamRow): ReadAction[Option[Game]] = ???

  // voir s'il faut recuperer un nouveau game ?
  // normalement oui pour l'id
  def createGame(game: GameRow): WriteAction[Unit] = ???

  def updateGame(game: GameRow): WriteAction[Unit] = ???

  def closeGame(game: GameRow): WriteAction[Unit] = ???

}

object DbActions {
  type WriteAction[+R] = DBIOAction[R, NoStream, Effect.Write]
  type ReadAction[+R] = DBIOAction[R, NoStream, Effect.Read]
  type ReadWriteAction[+R] = DBIOAction[R, NoStream, Effect.Read with Effect.Write]


}
