package game

import javax.inject.{Inject, Singleton}

import db.DbActions
import db.DbActions._
import db.slicksetup.Tables.{Game, GameRow, SlackTeamRow}
import game.Models.PlayerId
import slack.IncomingEvents.IncomingMessage
import slack.SlackClient
import slick.dbio.DBIOAction

import scala.concurrent.Future

@Singleton
class Engine @Inject()(queries: DbActions, slackClient: SlackClient){
  import Commands._
  import Extras._
  def handleMessage(slackTeamId: String, m: IncomingMessage): ReadWriteAction[Unit] = {
    for {
      team <- queries.getOrCreateTeam(slackTeamId)
      gameOpt <- queries.getCurrentGame(team)
      _ <- gameOpt
        .map(g => handleMessage(team, g, m))
        .getOrElse(handleMessageWithNoGame(team, m))
    } yield ()
  }

  def handleMessageWithNoGame(team: SlackTeamRow, m: IncomingMessage): WriteAction[Unit] =
    if (m.isPublic && m.withDirectMention(team) && m.is(NewGame)) {
      queries.createGame(GameRow(
        slackTeamId = team.slackTeamId,
        gameId = -1,
        slackChannelId = m.channel,
        gameState = initialState,
        completedAt = None
      ))
    } else DBIOAction.successful(())



  def handleMessage(team: SlackTeamRow, game: Game, m: IncomingMessage): ReadWriteAction[Unit] = ???

  implicit class RichIncomingMessage(incomingMessage: IncomingMessage){
    def isPublic: Boolean = ???
    def is(text: String): Boolean =
      incomingMessage.text.toLowerCase.trim == text.toLowerCase.trim
    def withDirectMention(team: SlackTeamRow): Boolean =
      ??? // en fonction du bot Id
  }


  def isInPublicChannel(incomingMessage: IncomingMessage): Boolean = ???


  def tellInPrivate(playerId: PlayerId, text: String): Future[Unit] = ???
  def tellEverybody(teamRow: SlackTeamRow, text: String): Future[Unit] = ???



















}



