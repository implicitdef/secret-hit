package db

import javax.inject._

import db.slicksetup.Tables._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DbActions @Inject()()(
  implicit ec: ExecutionContext
) {

  //TODO reimplement in SQL

  def inTransaction[A](block: => Future[A]): Future[A] = ???


  def getTeam(slackTeamId: String): Future[Option[SlackTeamRow]] = ???
    //SlackTeams
    //  .filter(_.slackTeamId === slackTeamId)
    //  .result
    //  .map(_.headOption)

  def getOrCreateTeam(team: SlackTeamRow): Future[Unit] = ???
    //(SlackTeams += team).map(_ => ())

  def closeAllCurrentGamesOfTeam(team: SlackTeamRow): Future[Unit] = ???
    //Games
    //  .filter(_.slackTeamId === team.slackTeamId)
    //  .filter(_.completedAt.isEmpty)
    //  .map(_.completedAt)
    //  .update(Some(DateTime.now))
    //  .map(_ => ())

  def getCurrentGame(team: SlackTeamRow): Future[Option[GameRow]] = ???
    //Games
    //  .filter(_.slackTeamId === team.slackTeamId)
    //  .filter(_.completedAt.isEmpty)
    //  .result
    //  .map(_.headOption)

  // voir s'il faut recuperer un nouveau game ?
  // normalement oui pour l'id
  def createGame(game: GameRow): Future[GameRow] = ???
    //Games
    //  .+=(game)
    //  .map(id => game.copy(gameId = id))

  def updateGame(game: GameRow): Future[Unit] = ???
    //Games
    //  .filter(_.gameId === game.gameId)
    //  .update(game)
    //  .map(_ => ())

  def closeGame(game: GameRow): Future[Unit] = ???
    //Games
    //  .filter(_.gameId === game.gameId)
    //  .map(_.completedAt)
    //  .update(Some(DateTime.now))
    //  .map(_ => ())

}

