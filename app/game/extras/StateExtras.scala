package game.extras

import db.slicksetup.Tables.GameRow
import game.Models.{GameState, GameStep, Player, PlayerId, Role}
import utils._

import scala.util.Random._

trait StateExtras {

  implicit class RichState(s: GameState){
    def registerPlayer(playerId: PlayerId, slackUserName: String): GameState =
      s.copy(players = s.players :+ Player(
        playerId,
        slackUserName,
        // we will assign the roles when the game starts
        Role.Liberal
      ))
    def removePlayer(playerId: PlayerId): GameState =
      s.copy(players = s.players.filterNot(_.id == playerId))
    def hasGoodNumberOfPlayers: Boolean =
      5 to 10 contains s.players.size
    def startGame =
      s
        .assignRolesAndShuffleOrder
        .setPresidentToHeadOfAlivePlayers
        .withStep(GameStep.ChoosingChancellor)
    def withStep(step: GameStep) =
      s.copy(step = step)
    def setPresidentToHeadOfAlivePlayers =
      s.copy(president = s.alivePlayers.headOption.map(_.id))
    def cyclingPlayers = {
      val head = s.players.headOption.getOrElse(err(s"Can't cycle, no player left"))
      s.copy(players = s.players.tail :+ head)
    }
    def assignRolesAndShuffleOrder = {
      val nbFascists = s.players.size match {
        case 5 | 6 => 1
        case 7 | 8 => 2
        case 9 | 10 => 3
      }
      val playersWithRoles =
        shuffle(s.players).zipWithIndex.map {
          case (p, 0) => p.copy(role = Role.Hitler)
          case (p, idx) if (1 to nbFascists).contains(idx) => p.copy(role = Role.Fascist)
          case (p, _) => p.copy(role = Role.Liberal)
        }
      s.copy(players = shuffle(playersWithRoles))
    }
    def alivePlayers: Seq[Player] =
      s.players.filterNot(p => s.dead.contains(p.id))
  }

  implicit class RichGameRow(g: GameRow){
    def updateState(func: GameState => GameState) =
      g.copy(gameState = func(g.gameState))
  }



}
