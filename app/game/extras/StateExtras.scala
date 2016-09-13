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
    def setPickedChancellorAndStartElection(id: PlayerId) =
      s.copy(chancellor = Some(id))
        .copy(votes = Map.empty)
        .withStep(GameStep.Electing)
    def withStep(step: GameStep) =
      s.copy(step = step)
    def isAlive(id: PlayerId) =
      ! s.dead.contains(id)
    def setPresidentToHeadOfAlivePlayers =
      s.copy(president = s.alivePlayers.headOption.map(_.id))
    def presidentName =
      s.president.flatMap(id => s.players.find(_.id == id)).map(_.slackUserName)
    def chancellorName =
      s.chancellor.flatMap(id => s.players.find(_.id == id)).map(_.slackUserName)
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
    def canVote(id: PlayerId): Boolean =
      s.alivePlayers.exists(_.id == id) &&
      ! s.votes.contains(id)
    def hasEverybodyVoted: Boolean =
      s.alivePlayers.map(_.id).forall(id => s.votes.contains(id))
    def registerVote(id: PlayerId, vote: Boolean) =
      s.copy(votes = s.votes + (id -> vote))
    def votesResult: Map[Boolean, Int] =
      s.votes.values.groupBy(identity).mapValues(_.size)
    def votesResultIsYes =
      votesResult.maxBy(_._2)._1
    def endElectionAsFailureAndMoveToNextStep = {
      // nothing to do, the votes etc. will be cleared up later
      s.copy()

        withStep(GameStep.ChoosingChancellor)
    }
    def endElectionAsSuccess = {
      //TODO

    }
    def alivePlayers: Seq[Player] =
      s.players.filterNot(p => s.dead.contains(p.id))
    def eligiblePlayersForChancellor =
      s.alivePlayers.filter { p =>
        // it can't be the presidential candidate
        ! s.president.contains(p.id) &&
        // nor the last chancellor
        ! s.lastChancellor.contains(p.id) &&
        // nor the last president, if there are more than five players left
        (! s.lastPresident.contains(p.id) || s.alivePlayers.size <= 5)
      }
  }

  implicit class RichGameRow(g: GameRow){
    def updateState(func: GameState => GameState): GameRow =
      g.copy(gameState = func(g.gameState))
  }



}
