package game

import game.Models.{GameState, GameStep, Player, PlayerId, Policy, Role}

import scala.util.Random._

object Extras {

  def initialState = GameState(
    GameStep.RegisteringPlayers,
    stack = shuffle(Seq.fill(6)(Policy.Liberal) ++ Seq.fill(11)(Policy.Fascist)),
    Nil,
    Nil,
    Nil,
    Nil,
    Map.empty,
    None,
    None,
    None,
    None,
    None,
    Nil,
    Nil
  )

  implicit class RichState(s: GameState){
    def registerPlayer(playerId: PlayerId, slackUserName: String): GameState =
      s.copy(players = s.players :+ Player(
          playerId,
          slackUserName,
          // we will assign the roles when the game starts
          Role.Liberal
      ))
  }






}
