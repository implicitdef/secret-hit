package game.extras

import db.slicksetup.Tables.GameRow
import game.Models.{GameState, GameStep, Player, PlayerId, Role}
import utils._

import scala.util.Random._

trait GameRowExtras {

  implicit class RichGameRow(g: GameRow){
    def state: GameState =
      g.gameState
    def updateState(func: GameState => GameState): GameRow =
      g.copy(gameState = func(g.gameState))
  }



}
