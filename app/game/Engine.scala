package game

import javax.inject.{Inject, Singleton}

import game.Models.{GameState, GameStep, Policy}

import scala.util.Random._

@Singleton
class Engine @Inject()(){

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



}



